package com.zhe.redis;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zhe.rabbitmq.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageProducer messageProducer;
    public boolean stock(String orderId) {
        Long balance = redisTemplate.opsForValue().decrement("account_balance");
        if (balance >= 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("stock", balance);
            messageProducer.sendTestMessage(orderId, "seckill", map);
            return true;
        } else {
            redisTemplate.opsForValue().increment("account_balance");
            return false;
        }
    }
}
