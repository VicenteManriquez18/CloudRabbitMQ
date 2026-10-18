package com.proyecto.ColegioBackend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Configuración para Registro de Usuarios
    public static final String REGISTRO_QUEUE = "usuario_registro_queue";
    public static final String REGISTRO_DLQ = "usuario_registro_queue.dlq";
    public static final String EXCHANGE = "usuario_exchange";
    public static final String ROUTING_KEY = "usuario_routing_key";

    // Configuración para Chat entre Apoderados y Profesores
    public static final String CHAT_QUEUE = "chat_mensajes_queue";
    public static final String CHAT_DLQ = "chat_mensajes_queue.dlq";
    public static final String CHAT_EXCHANGE = "chat_exchange";
    public static final String CHAT_ROUTING_PATTERN = "chat.#";

    // Dead Letter Exchange común
    public static final String DLX_EXCHANGE = "dlx.exchange";

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ==================== COLAS CON DLQ ====================

    @Bean
    public Queue queue() {
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
    public TopicExchange userExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange userExchange) {
        return BindingBuilder.bind(queue).to(userExchange).with(ROUTING_KEY);
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
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE);
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(CHAT_ROUTING_PATTERN);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
