package com.zhe.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 用户消息消费者
 * 负责消费用户相关的消息
 */
@Slf4j
@Component
public class UserMessageConsumer {

    /**
     * 监听用户队列，处理所有用户相关的消息
     */
    @RabbitListener(queues = "${app.rabbitmq.user-queue}")
    public void handleUserMessage(
            Map<String, Object> message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) throws IOException {

        log.info("收到用户消息 - 路由键: {}, 消息: {}", routingKey, message);

        try {
            // 解析路由键，获取动作类型
            String[] routingKeyParts = routingKey.split("\\.");
            String action = routingKeyParts[1];

            switch (action) {
                case "register":
                    processUserRegister(message);
                    break;
                case "login":
                    processUserLogin(message);
                    break;
                case "update":
                    processUserUpdate(message);
                    break;
                case "delete":
                    processUserDelete(message);
                    break;
                default:
                    log.warn("未知的用户动作: {}", action);
                    break;
            }

            log.info("用户消息处理完成并确认 - 路由键: {}", routingKey);

        } catch (Exception e) {
            log.error("处理用户消息失败 - 路由键: {}, 错误: {}", routingKey, e.getMessage());
        }
    }

    /**
     * 处理用户注册逻辑
     */
    private void processUserRegister(Map<String, Object> message) {
        String userId = (String) message.get("userId");
        String username = (String) message.get("username");
        log.info("处理用户注册 - 用户ID: {}, 用户名: {}", userId, username);
        // 用户注册业务逻辑，比如：
        // 1. 发送欢迎邮件
        // 2. 初始化用户资料
        // 3. 记录注册日志等
    }

    /**
     * 处理用户登录逻辑
     */
    private void processUserLogin(Map<String, Object> message) {
        String userId = (String) message.get("userId");
        String loginTime = (String) message.get("loginTime");
        log.info("处理用户登录 - 用户ID: {}, 登录时间: {}", userId, loginTime);
        // 用户登录业务逻辑
    }

    /**
     * 处理用户信息更新逻辑
     */
    private void processUserUpdate(Map<String, Object> message) {
        String userId = (String) message.get("userId");
        log.info("处理用户信息更新 - 用户ID: {}", userId);
        // 用户信息更新业务逻辑
    }

    /**
     * 处理用户删除逻辑
     */
    private void processUserDelete(Map<String, Object> message) {
        String userId = (String) message.get("userId");
        log.info("处理用户删除 - 用户ID: {}", userId);
        // 用户删除业务逻辑
    }
}
