package com.zhe.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.zhe.rabbitmq.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @version 1.0
 * @Author 朱厚恩
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessageConsumer {
    private final MessageProducer messageProducer;

    @Value("${app.rabbitmq.order-queue}")
    private String orderQueue;


    /**
     * 监听订单队列，处理所有订单相关的消息
     *
     * @param message 消息内容
     * @param channel RabbitMQ 通道
     * @param deliveryTag 消息投递标签
     * @param routingKey 路由键
     * @throws IOException 异常
     */
    public void handleOrderMessage(
            Map<String, Object> message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey
    ) throws IOException {

    }

}
