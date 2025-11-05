package com.zhe.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * 消息生产者
 * 负责向 RabbitMQ 发送消息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.topic-exchange}")
    private String topicExchange;

    /**
     * 发送订单消息
     * @param orderId 订单ID
     * @param action 订单动作（create, update, delete 等）
     * @param message 消息内容
     */
    public void sendOrderMessage(String orderId, String action, Object message) {
        // 构建路由键：order.{action}
        String routingKey = "order." + action;
        rabbitTemplate.convertAndSend(topicExchange, routingKey, message, msg -> {
            // 设置消息ID
            msg.getMessageProperties().setMessageId(UUID.randomUUID().toString().replace("-", ""));
            // 设置消息时间戳
            msg.getMessageProperties().setTimestamp(new Date());
            return msg;
        });
        log.info("发送订单消息成功 - 路由键: {}, 订单ID: {}, 消息: {}",
                routingKey, orderId, message);
    }

    /**
     * 发送用户消息
     * @param userId 用户ID
     * @param action 用户动作（create, update, delete 等）
     * @param message 消息内容
     */
    public void sendUserMessage(String userId, String action, Object message) {
        String routingKey = "user:" + action;
        rabbitTemplate.convertAndSend(topicExchange, routingKey, message, msg -> {
            // 设置消息ID
            msg.getMessageProperties().setMessageId(UUID.randomUUID().toString().replace("-", ""));
            // 设置消息时间戳
            msg.getMessageProperties().setTimestamp(new Date());
            return msg;
        });
        log.info("发送用户消息成功 - 路由键: {}, 用户ID: {}, 消息: {}",
                routingKey, userId, message);
    }

    /**
     * 发送通用消息
     * @param routingKey 路由键
     * @param message 消息内容
     */
    public void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(topicExchange, routingKey, message, msg -> {
            // 设置消息ID
            msg.getMessageProperties().setMessageId(UUID.randomUUID().toString().replace("-", ""));
            // 设置消息时间戳
            msg.getMessageProperties().setTimestamp(new Date());
            return msg;
        });
        log.info("发送通用消息成功 - 路由键: {}, 消息: {}", routingKey, message);
    }
}
