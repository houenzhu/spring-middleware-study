package com.zhe.redis.service.impl;

import com.zhe.redis.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STOCK_KEY = "seckill:stock:";
    // 用户限购KEY
    private static final String USER_LIMIT_KEY = "seckill:user:";
    // 已抢购用户集合
    private static final String USERS_KEY = "seckill:users:";

    @Override
    public String seckill(String productId, String userId) {
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("lua/seckill.lua"));
        defaultRedisScript.setResultType(Long.class);
        long result = redisTemplate.execute(defaultRedisScript, List.of(STOCK_KEY + productId, USERS_KEY + productId), userId);
        if (result == -1) {
            return "库存不足";
        } else if (result == -2) {
            return String.format("%s用户已抢购过", userId);
        }
        return "抢购成功";
    }
}
