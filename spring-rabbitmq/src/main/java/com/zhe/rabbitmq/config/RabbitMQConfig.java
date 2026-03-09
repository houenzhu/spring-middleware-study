package com.zhe.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 配置主题交换机、队列、绑定关系等
 */
@Configuration
public class RabbitMQConfig {
    // 从配置文件中读取交换机名称
    @Value("${app.rabbitmq.topic-exchange}")
    private String topicExchange;

    // 从配置文件中读取队列名称
    @Value("${app.rabbitmq.order-queue}")
    private String orderQueue;

    @Value("${app.rabbitmq.user-queue}")
    private String userQueue;

    @Value("${app.rabbitmq.test-queue}")
    private String testQueue;

    // 从配置文件中读取路由键
    @Value("${app.rabbitmq.order-routing-key}")
    private String orderRoutingKey;

    @Value("${app.rabbitmq.user-routing-key}")
    private String userRoutingKey;

    @Value("${app.rabbitmq.test-routing-key}")
    private String testRoutingKey;

    @Bean
    public TopicExchange topicExchange() {
        // 参数说明：
        // 1. 交换机名称
        // 2. 是否持久化（重启后交换机仍然存在）
        // 3. 当没有队列绑定到交换机时，是否自动删除交换机
        return new TopicExchange(topicExchange, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueue, true, false, false);
    }

    @Bean
    public Queue userQueue() {
        // 参数说明：
        // 1. 队列名称
        // 2. 是否持久化（重启后队列仍然存在）
        // 3. 是否排他（仅限此连接使用）
        // 4. 当没有消费者时是否自动删除队列
        return new Queue(userQueue, true, false, false);
    }

    @Bean
    public Queue testQueue() {
        return new Queue(testQueue, true, false, false);
    }

    /**
     * 绑定订单队列到主题交换机
     * 使用路由键 "order.*" 匹配所有以 "order." 开头的消息
     * 例如：order.create, order.update, order.delete 等
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(topicExchange())
                .with(orderRoutingKey);
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(topicExchange())
                .with(userRoutingKey);
    }

    @Bean
    public Binding testBinding() {
       return BindingBuilder.bind(testQueue())
               .to(topicExchange())
               .with(testRoutingKey);
    }

    /**
     * 配置 JSON 消息转换器
     * 将 Java 对象自动转换为 JSON 格式进行传输
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置 RabbitTemplate
     * 用于发送消息到 RabbitMQ
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置消息转换器
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        // 设置消息发送确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送到交换机成功: " + correlationData);
            } else {
                System.out.println("消息发送到交换机失败: " + cause);
            }
        });
        // 设置消息返回回调（当消息无法路由到队列时调用）
        rabbitTemplate.setReturnsCallback(returned -> {
            System.out.println("消息无法路由到队列: " + returned.getMessage() +
                    ", 路由键: " + returned.getRoutingKey() +
                    ", 回复码: " + returned.getReplyCode() +
                    ", 回复文本: " + returned.getReplyText());
        });
        return rabbitTemplate;
    }

    /**
     * 配置消费者容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

}
