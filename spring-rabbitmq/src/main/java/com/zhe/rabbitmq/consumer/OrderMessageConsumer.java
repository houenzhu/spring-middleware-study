package com.zhe.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 修复后的订单消息消费者
 * 添加消息去重和确认状态管理
 */
@Slf4j
@Component
public class OrderMessageConsumer {

    // 用于跟踪已经处理的消息投递标签
    private final Map<Long, Boolean> processedDeliveryTags = new ConcurrentHashMap<>();

    @RabbitListener(queues = "${app.rabbitmq.order-queue}")
    public void handleOrderMessage(
            Map<String, Object> message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey,
            @Header(AmqpHeaders.MESSAGE_ID) String messageId) throws IOException {

        // 检查是否已经处理过该投递标签
        if (processedDeliveryTags.containsKey(deliveryTag)) {
            log.warn("消息已处理，跳过重复处理 - 投递标签: {}, 消息ID: {}", deliveryTag, messageId);
            return;
        }

        log.info("开始处理订单消息 - 投递标签: {}, 消息ID: {}, 路由键: {}",
                deliveryTag, messageId, routingKey);

        try {
            // 标记为正在处理
            processedDeliveryTags.put(deliveryTag, false);

            String[] routingKeyParts = routingKey.split("\\.");
            String action = routingKeyParts[1];

            switch (action) {
                case "create":
                    processOrderCreate(message);
                    break;
                case "update":
                    processOrderUpdate(message);
                    break;
                case "delete":
                    processOrderDelete(message);
                    break;
                case "pay":
                    processOrderPay(message);
                    break;
                default:
                    log.warn("未知的订单动作: {}", action);
                    break;
            }

            log.info("消息处理成功");

        } catch (Exception e) {
            log.error("处理消息失败", e);
            // 在自动确认模式下，异常会导致消息重新入队
            throw new RuntimeException("处理失败，消息将重新入队", e);
        }
    }

    // 业务处理方法保持不变...
    private void processOrderCreate(Map<String, Object> message) {
        String orderId = (String) message.get("orderId");
        log.info("处理订单创建 - 订单ID: {}", orderId);
        // 模拟业务处理时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processOrderUpdate(Map<String, Object> message) {
        String orderId = (String) message.get("orderId");
        log.info("处理订单更新 - 订单ID: {}", orderId);
    }

    private void processOrderDelete(Map<String, Object> message) {
        String orderId = (String) message.get("orderId");
        log.info("处理订单删除 - 订单ID: {}", orderId);
    }

    private void processOrderPay(Map<String, Object> message) {
        String orderId = (String) message.get("orderId");
        Double amount = (Double) message.get("amount");
        log.info("处理订单支付 - 订单ID: {}, 金额: {}", orderId, amount);
    }
}
