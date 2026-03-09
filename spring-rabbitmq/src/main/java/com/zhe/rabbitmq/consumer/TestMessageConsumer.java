package com.zhe.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class TestMessageConsumer {

    @RabbitListener(queues = "${app.rabbitmq.test-queue}")
    public void handleTestMessage(Map<String, Object> message,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                  @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey,
                                  @Header(AmqpHeaders.MESSAGE_ID) String messageId) {
        // 模拟业务延迟
        try {
            Thread.sleep(200);
            log.info("收到测试消息 - 测试消息: {}, tag = {}, routingKey = {}", message.get("customer"), deliveryTag, routingKey);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "${app.rabbitmq.test-queue}")
    public void handleSeckillMessage(Map<String, Object> message) {
        try {
            Thread.sleep(200);
            log.info("{} 购买，库存还剩: {}", Thread.currentThread().getName(), message.get("stock"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
