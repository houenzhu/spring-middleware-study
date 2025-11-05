package com.zhe.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        // 构建
    }
}
