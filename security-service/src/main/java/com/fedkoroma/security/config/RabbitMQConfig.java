package com.fedkoroma.security.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // email
    @Value("${rabbitmq.queue.email.name}")
    private String emailQueue;

    @Value("${rabbitmq.exchange.email.name}")
    private String emailExchange;

    @Value("${rabbitmq.binding.email.name}")
    private String emailRoutingKey;

    //user Event-Driven Created
    @Value("${rabbitmq.queue.user.created.name}")
    private String userCreatedQueue;

    @Value("${rabbitmq.exchange.user.name}")
    private String userExchange;

    @Value("${rabbitmq.binding.user.created.name}")
    private String userCreatedRoutingKey;

    //user Event-Driven Updated
    @Value("${rabbitmq.queue.user.updated.name}")
    private String userUpdatedQueue;

    @Value("${rabbitmq.binding.user.updated.name}")
    private String userUpdatedRoutingKey;

    @Bean
    public Queue emailQueue() {
        return new Queue(emailQueue);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(emailExchange);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(emailExchange())
                .with(emailRoutingKey);
    }

    // Очередь для событий создания пользователя
    @Bean
    public Queue userCreatedQueue() {
        return new Queue(userCreatedQueue);
    }

    // Обменник для пользователей
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(userExchange);
    }

    // Биндинг для очереди создания пользователя
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder.bind(userCreatedQueue())
                .to(userExchange())
                .with(userCreatedRoutingKey);
    }

    // Очередь для событий обновления пользователя
    @Bean
    public Queue userUpdatedQueue() {
        return new Queue(userUpdatedQueue);
    }

    // Биндинг для очереди обновления пользователя
    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder.bind(userUpdatedQueue())
                .to(userExchange())
                .with(userUpdatedRoutingKey);
    }


    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
