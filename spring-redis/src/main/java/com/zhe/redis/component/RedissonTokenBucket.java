package com.zhe.redis.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonTokenBucket {
    private final RedissonClient redissonClient;

    /**
     * 获取令牌（阻塞直到获取到令牌）
     */
    public boolean acquire(String key, long permits) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        return rateLimiter.tryAcquire(permits);
    }

    /**
     * 尝试获取令牌（非阻塞）
     */
    public boolean tryAcquire(String key, long permits) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        return rateLimiter.tryAcquire(permits, Duration.ofSeconds(0));
    }

    /**
     * 尝试获取令牌（带超时）
     */
    public boolean tryAcquire(String key, long permits, long timeout, TimeUnit unit) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        return rateLimiter.tryAcquire(permits, Duration.of(timeout, unit.toChronoUnit()));
    }

    public void init(String key, long rate, long capacity) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, Duration.ofSeconds(capacity));
        log.info("初始化令牌桶: key={}, rate={}/秒, capacity={}", key, rate, capacity);
    }

    /**
     * 获取剩余令牌数
     */
    public long availablePermits(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        return rateLimiter.availablePermits();
    }

    /**
     * 动态修改速率
     */
    public void setRate(String key, long rate) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.setRate(RateType.OVERALL, rate, Duration.ofSeconds(1));
    }

    /**
     * 获取配置的速率
     */
    public long getRate(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        return rateLimiter.getConfig().getRate();
    }

    /**
     * 删除令牌桶
     */
    public void delete(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.delete();
    }
}
