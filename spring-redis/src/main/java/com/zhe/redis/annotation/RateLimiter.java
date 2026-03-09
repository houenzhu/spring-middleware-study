package com.zhe.redis.annotation;

import com.zhe.redis.enumation.RateLimitType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    String key() default "";
    int time() default 60;
    int count() default 100;
    long rate() default 1;        // 每秒令牌数
    long permits() default 1;      // 每次消耗令牌数
    long capacity() default 1;     // 桶容量
    RateLimitType type() default RateLimitType.SLIDING_WINDOW;
}
