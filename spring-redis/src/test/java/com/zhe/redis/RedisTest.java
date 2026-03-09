package com.zhe.redis;

import com.zhe.redis.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@SpringBootTest
@Slf4j
public class RedisTest implements InitializingBean {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderService orderService;

    private static final Integer CORE_POOL_SIZE = 5;
    private static final Integer MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private static final String BALANCE_KEY = "account_balance";
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Test
    public void m2() {
        // 排行榜
        redisTemplate.opsForZSet().add("ranking", "user1", 100);
        redisTemplate.opsForZSet().add("ranking", "user2", 90);
        redisTemplate.opsForZSet().add("ranking", "user3", 80);
    }

    @Test
    public void m1() {
        Runnable task = () -> {
            // 预扣减
            Long balance = redisTemplate.opsForValue().decrement(BALANCE_KEY);
            if (balance >= 0) {
                try {
                    Thread.sleep(10);
                    System.out.println(Thread.currentThread().getName() + " 取款，余额: " + balance);
                } catch (InterruptedException e) {
                    redisTemplate.opsForValue().increment(BALANCE_KEY);
                    throw new RuntimeException(e);
                }
            } else {
                redisTemplate.opsForValue().increment(BALANCE_KEY);
            }
        };
        for (int j = 0; j < 100; j++) {
            executor.execute(task);
        }
        try {
            // 等待所有任务完成
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Integer finalBalance = (Integer) redisTemplate.opsForValue().get(BALANCE_KEY);
        System.out.println("最终余额: " + finalBalance);
    }

    @Test
    public void getHashes() {
        Map<Object, Object> hash1 = redisTemplate.opsForHash().entries("hash1");
        hash1.forEach((key, value) -> System.out.println(key + ":" + value));
    }
    @Test
    public void getHash() {
        Object o = redisTemplate.opsForHash().get("hash1", "key1");
        System.out.println(o);
    }

    @Test
    public void setHash() {
        redisTemplate.opsForHash().put("hash1", "key2", "value2");
    }

    @Test
    public void setNx() {
        Boolean absent = redisTemplate.opsForValue().setIfAbsent("k2", "v2");
        System.out.println(absent);
        System.out.println(redisTemplate.opsForValue().setIfAbsent("k2", "v2"));
    }

    @Test
    public void setValue() {
        redisTemplate.opsForValue().set("key1", "value1", 60, TimeUnit.SECONDS);
    }

    @Test
    public void getValue() {
        System.out.println(redisTemplate.opsForValue().get("key1"));
        System.out.println(redisTemplate.getExpire("key1"));
    }

    @Test
    public void setList() {
        List<String> strings = Stream.of("1", "2", "3", "4", "5").toList();
        redisTemplate.opsForList().leftPushAll("list1", strings);
        redisTemplate.opsForList().leftPush("list1", "6");
    }

    @Test
    public void getList() {
        List<Object> list1 = redisTemplate.opsForList().range("list1", 0, 1);
        if (!CollectionUtils.isEmpty(list1)) {
            list1.forEach(item -> {
                if (item instanceof List<?> list) {
                    list.forEach(System.out::println);
                }
            });
        }
    }

    @Test
    public void setIds() {
        List<Integer> list = Arrays.asList(15985, 16991, 14983);
        redisTemplate.opsForList().leftPushAll("ids", list);
    }

    @Test
    public void readLua() {
        DefaultRedisScript<Boolean> defaultRedisScript =
                new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("lua/seckill.lua"));
        defaultRedisScript.setResultType(Boolean.class);
        Boolean exists = redisTemplate.execute(defaultRedisScript, List.of("myset1"), "v3");
        System.out.println(exists);
    }

    @Test
    public void seckill() {
        Runnable task = () -> {
            String result = orderService.seckill("1001", String.valueOf(Thread.currentThread().getId()));
            System.out.println(Thread.currentThread().getName() + " " + result);
        };
        for (int i = 0; i < 1000; i++) {
            executor.execute(task);
        }
    }

    @Test
    public void rateLimit() {
        String key = "ratelimit:127_0_0_1";
        long now = System.currentTimeMillis();
        long windowSize = 10000L;
        long windowStart = now - windowSize;
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("lua/ratelimit.lua"));
        defaultRedisScript.setResultType(Long.class);
        redisTemplate.execute(defaultRedisScript, List.of(key), now, windowStart, 100);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.opsForValue().set("seckill:stock:1001", 10);
        log.info("秒杀商品库存预热完成...");
    }



}
