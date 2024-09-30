package com.fedkoroma.client.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMQListenerConfig implements RabbitListenerConfigurer {
    @Bean
    public Queue userCreatedQueue() {
        return new Queue("user.created.queue");
    }

    @Bean
    public Queue userUpdatedQueue() {
        return new Queue("user.updated.queue");
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        // Дополнительная конфигурация, если необходимо
    }
}
