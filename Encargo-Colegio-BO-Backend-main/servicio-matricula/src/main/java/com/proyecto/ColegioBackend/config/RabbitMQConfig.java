package com.proyecto.ColegioBackend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REGISTRO_QUEUE = "usuario_registro_queue";
    public static final String REGISTRO_DLQ = "usuario_registro_queue.dlq";

    public static final String CHAT_QUEUE = "chat_mensajes_queue";
    public static final String CHAT_DLQ = "chat_mensajes_queue.dlq";

    public static final String DLX_EXCHANGE = "dlx.exchange";

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue registroQueue() {
        return QueueBuilder.durable(REGISTRO_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", REGISTRO_DLQ)
                .build();
    }

    @Bean
    public Queue registroDlq() {
        return QueueBuilder.durable(REGISTRO_DLQ).build();
    }

    @Bean
    public Binding registroDlqBinding(Queue registroDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(registroDlq)
                .to(dlxExchange)
                .with(REGISTRO_DLQ);
    }

    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable(CHAT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", CHAT_DLQ)
                .build();
    }

    @Bean
    public Queue chatDlq() {
        return QueueBuilder.durable(CHAT_DLQ).build();
    }

    @Bean
    public Binding chatDlqBinding(Queue chatDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(chatDlq)
                .to(dlxExchange)
                .with(CHAT_DLQ);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
