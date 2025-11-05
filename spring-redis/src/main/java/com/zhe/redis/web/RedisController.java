package com.zhe.redis.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author 朱厚恩
 */

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/get")
    public Integer set(@RequestParam("key") String key) {
        return (Integer) redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/stock")
    public ResponseEntity<String> stock() {
        Long balance = redisTemplate.opsForValue().decrement("account_balance");
        if (balance >= 0) {
            System.out.println(Thread.currentThread().getName() + " 取款，库存: " + balance);
            return ResponseEntity.ok("库存扣除成功");
        } else {
            redisTemplate.opsForValue().increment("account_balance");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("库存不足");
        }
    }
}
