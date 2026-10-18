package com.proyecto.mensajeria.config;

import com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String CHAT_QUEUE = "chat_mensajes_queue";
    public static final String CHAT_EXCHANGE = "chat_exchange";
    public static final String CHAT_ROUTING_PATTERN = "chat.#";

    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String CHAT_DLQ = "chat_mensajes_queue.dlq";

    public static final String EVENTOS_EXCHANGE = "eventos.exchange";
    public static final String TICKETS_MATRICULA_QUEUE = "tickets.matricula.queue";
    public static final String TICKETS_MATRICULA_DLQ = "tickets.matricula.queue.dlq";

    // ==================== EXCHANGES ====================

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE);
    }

    @Bean
    public TopicExchange eventosExchange() {
        return new TopicExchange(EVENTOS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ==================== COLAS PRINCIPALES (CON DLQ) ====================

    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable(CHAT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", CHAT_DLQ)
                .build();
    }

    @Bean
    public Queue ticketsMatriculaQueue() {
        return QueueBuilder.durable(TICKETS_MATRICULA_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TICKETS_MATRICULA_DLQ)
                .build();
    }

    // ==================== DEAD LETTER QUEUES ====================

    @Bean
    public Queue chatDlq() {
        return QueueBuilder.durable(CHAT_DLQ).build();
    }

    @Bean
    public Queue ticketsMatriculaDlq() {
        return QueueBuilder.durable(TICKETS_MATRICULA_DLQ).build();
    }

    // ==================== BINDINGS DE NEGOCIO ====================

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(CHAT_ROUTING_PATTERN);
    }

    @Bean
    public Binding ticketsMatriculaBinding(Queue ticketsMatriculaQueue, TopicExchange eventosExchange) {
        return BindingBuilder.bind(ticketsMatriculaQueue).to(eventosExchange).with("matricula.registrada");
    }

    // ==================== BINDINGS DE DLQ ====================

    @Bean
    public Binding chatDlqBinding(Queue chatDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(chatDlq).to(dlxExchange).with(CHAT_DLQ);
    }

    @Bean
    public Binding ticketsMatriculaDlqBinding(Queue ticketsMatriculaDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(ticketsMatriculaDlq).to(dlxExchange).with(TICKETS_MATRICULA_DLQ);
    }

    // ==================== MESSAGE CONVERTER ====================

    @Bean
    public Jackson2JsonMessageConverter messageConverter(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.proyecto.ColegioBackend.model.event.MatriculaRegistradaEvent", MatriculaRegistradaEvent.class);
        idClassMapping.put("com.proyecto.reportes.model.event.MatriculaRegistradaEvent", MatriculaRegistradaEvent.class);
        idClassMapping.put("com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent", MatriculaRegistradaEvent.class);
        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);
        return converter;
    }
}