package com.zhe.rabbitmq.controller;

import com.zhe.rabbitmq.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 测试控制器
 * 提供 HTTP 接口用于测试消息发送
 */
@RestController
@RequestMapping("/api/message")
public class TestController {

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 发送订单创建消息
     */
    @PostMapping("/order/create")
    public String sendOrderCreateMessage() {
        String orderId = UUID.randomUUID().toString();
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("userId", "user123");
        message.put("productId", "product456");
        message.put("amount", 99.99);
        message.put("createTime", System.currentTimeMillis());

        messageProducer.sendOrderMessage(orderId, "create", message);
        return "订单创建消息发送成功，订单ID: " + orderId;
    }

    /**
     * 发送订单支付消息
     */
    @PostMapping("/order/pay")
    public String sendOrderPayMessage(@RequestParam("orderId") String orderId) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", orderId);
        message.put("payAmount", 99.99);
        message.put("payTime", System.currentTimeMillis());
        message.put("paymentMethod", "alipay");

        messageProducer.sendOrderMessage(orderId, "pay", message);
        return "订单支付消息发送成功，订单ID: " + orderId;
    }

    /**
     * 发送用户注册消息
     */
    @PostMapping("/user/register")
    public String sendUserRegisterMessage() {
        String userId = UUID.randomUUID().toString();
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("username", "testuser");
        message.put("email", "test@example.com");
        message.put("registerTime", System.currentTimeMillis());

        messageProducer.sendUserMessage(userId, "register", message);
        return "用户注册消息发送成功，用户ID: " + userId;
    }

    /**
     * 发送用户登录消息
     */
    @PostMapping("/user/login")
    public String sendUserLoginMessage(@RequestParam("userId") String userId) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("loginTime", System.currentTimeMillis());
        message.put("ip", "192.168.1.100");

        messageProducer.sendUserMessage(userId, "login", message);
        return "用户登录消息发送成功，用户ID: " + userId;
    }

    /**
     * 发送自定义路由键的消息
     */
    @PostMapping("/custom")
    public String sendCustomMessage(@RequestParam String routingKey,
                                    @RequestParam String messageContent) {
        Map<String, Object> message = new HashMap<>();
        message.put("content", messageContent);
        message.put("timestamp", System.currentTimeMillis());

        messageProducer.sendMessage(routingKey, message);
        return "自定义消息发送成功，路由键: " + routingKey;
    }

    @PostMapping("/test")
    public String sendTestMessage(@RequestParam("userId") String userId) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("address", "翠芳园");
        msg.put("customer", Arrays.asList("小李", "小红", "小吴", "小欧", "小陈", "小朱", "小炮"));
        messageProducer.sendTestMessage(userId, "meat", msg);
        return "消息发送成功";
    }
}
