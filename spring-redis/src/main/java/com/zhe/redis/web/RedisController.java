package com.zhe.redis.web;

import com.zhe.redis.BlobService;
import com.zhe.redis.OrderService;
import com.zhe.redis.annotation.RateLimiter;
import com.zhe.redis.domain.Department;
import com.zhe.redis.enumation.RateLimitType;
import com.zhe.redis.service.DepartmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version 1.0
 * @Author 朱厚恩
 */

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderService orderService;
    private final BlobService blobService;
    private final DepartmentService departmentService;

    @GetMapping("/get")
    public Integer set(@RequestParam("key") String key) {
        return (Integer) redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/stock")
    public ResponseEntity<String> stock(@RequestParam("orderId") String orderId) {
        return orderService.stock(orderId)
                ? ResponseEntity.ok("库存扣除成功")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("库存不足");
    }

    @GetMapping("/watch/{bid}")
    public void watch(@PathVariable("bid") String bid) {
        blobService.watch(bid);
    }

    @GetMapping("/getCount/{bid}")
    public Integer getCount(@PathVariable("bid") String bid) {
        return blobService.count(bid);
    }

    @GetMapping("/getDepartment")
    public List<Department> departments() {
        List<Object> ids = redisTemplate.opsForList().range("ids", 0, 1);
        List<Department> departments = null;
        if (!CollectionUtils.isEmpty(ids)) {
            Object key = ids.get(0);
            if (key instanceof List<?> list) {
                List<Integer> list1 = list.stream().map(id -> Integer.parseInt(String.valueOf(id))).toList();
                departments = departmentService.listByIds(list1);
            }
        }
        return departments;
    }

    @GetMapping("/setSession")
    public void setSession(HttpServletRequest request) {
        request.getSession().setAttribute("cart", "123");
        redisTemplate.opsForHash().put("session", request.getSession().getId(), request.getSession().getAttribute("cart"));
    }

    @GetMapping("/hello")
    @RateLimiter(key = "rate:limiter:", type = RateLimitType.TOKEN_BUCKET)
    public String hello() {
        return "hello redis";
    }
}
