package com.zhe.redis.aspect;

import com.zhe.redis.annotation.RateLimiter;
import com.zhe.redis.component.RedissonTokenBucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@Aspect
public class RateLimiterAspect {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final RedissonTokenBucket redissonTokenBucket;

    @Pointcut("@annotation(com.zhe.redis.annotation.RateLimiter)")
    public void declareRateLimiter() {
    }

    @Around("declareRateLimiter()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        long result = algorithmChoose(request, signature);
        if (result == 0L) {
            log.warn("请求过于频繁，请稍后再试");
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }
        return pjp.proceed();
    }

    private long algorithmChoose(HttpServletRequest request, MethodSignature signature) {
        String ip = request.getRemoteAddr();
        ip = ip.replace(".", "_");
        RateLimiter rateLimiter = signature.getMethod().getDeclaredAnnotation(RateLimiter.class);
        String key = rateLimiter.key() + ip;
        long result = 0L;
        switch (rateLimiter.type()) {
            case SLIDING_WINDOW -> result = slidingWindow(rateLimiter, key);
            case TOKEN_BUCKET -> result = tokenBucket(rateLimiter, key);
            default -> {}
        }
        return result;
    }

    /**
     * 滑动窗口算法
     * @param rateLimiter
     * @param key
     * @return
     */
    private long slidingWindow(RateLimiter rateLimiter, String key) {

        String uuid = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();
        long windowSize = rateLimiter.time() * 1000L;
        // 获取上一时间段的开始时间
        long windowStart = now - windowSize;
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("lua/ratelimit.lua"));
        defaultRedisScript.setResultType(Long.class);

        return stringRedisTemplate.execute(defaultRedisScript, List.of(key), String.valueOf(now), String.valueOf(windowStart),
                String.valueOf(rateLimiter.count()), String.valueOf(windowSize), uuid);
    }

    private long tokenBucket(RateLimiter rateLimiter, String key) {
        // 初始化令牌桶
        redissonTokenBucket.init(key, rateLimiter.rate(), rateLimiter.capacity());
        boolean acquire = false;
        if (rateLimiter.time() > 0) {
            acquire = redissonTokenBucket.tryAcquire(key, rateLimiter.permits(), rateLimiter.time(), TimeUnit.SECONDS);
        } else {
            acquire = redissonTokenBucket.tryAcquire(key, rateLimiter.permits());
        }

        if (!acquire) {
            log.warn("Redisson限流: key={}, rate={}/秒, capacity={}",
                    key, rateLimiter.rate(), rateLimiter.capacity());
            long available = redissonTokenBucket.availablePermits(key);
            if (available <= 0) {
                // 计算下一个令牌产生时间
                long nextTokenTime = (long) (1000.0 / rateLimiter.rate());
                log.warn("系统繁忙，请{}秒后重试", nextTokenTime / 1000);
            }
            return 0;
        }
        return 1;
    }
}
