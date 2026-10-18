package com.proyecto.reportes.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String DLX_EXCHANGE = "dlx.exchange";

    public static final String MATRICULA_QUEUE = "eventos.matricula.queue";
    public static final String MATRICULA_DLQ = "eventos.matricula.queue.dlq";

    public static final String NOTA_QUEUE = "eventos.nota.queue";
    public static final String NOTA_DLQ = "eventos.nota.queue.dlq";

    public static final String ASISTENCIA_QUEUE = "eventos.asistencia.queue";
    public static final String ASISTENCIA_DLQ = "eventos.asistencia.queue.dlq";

    // ==================== EXCHANGES ====================

    @Bean
    public TopicExchange eventoExchange() {
        return new TopicExchange("eventos.exchange", true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ==================== QUEUES CON DLQ ====================

    @Bean
    public Queue matriculaQueue() {
        return QueueBuilder.durable(MATRICULA_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", MATRICULA_DLQ)
                .build();
    }

    @Bean
    public Queue matriculaDlq() {
        return QueueBuilder.durable(MATRICULA_DLQ).build();
    }

    @Bean
    public Queue notaQueue() {
        return QueueBuilder.durable(NOTA_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", NOTA_DLQ)
                .build();
    }

    @Bean
    public Queue notaDlq() {
        return QueueBuilder.durable(NOTA_DLQ).build();
    }

    @Bean
    public Queue asistenciaQueue() {
        return QueueBuilder.durable(ASISTENCIA_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ASISTENCIA_DLQ)
                .build();
    }

    @Bean
    public Queue asistenciaDlq() {
        return QueueBuilder.durable(ASISTENCIA_DLQ).build();
    }

    // ==================== BINDINGS DE NEGOCIO ====================

    @Bean
    public Binding matriculaBinding(Queue matriculaQueue, TopicExchange eventoExchange) {
        return BindingBuilder.bind(matriculaQueue)
                .to(eventoExchange)
                .with("matricula.registrada");
    }

    @Bean
    public Binding notaBinding(Queue notaQueue, TopicExchange eventoExchange) {
        return BindingBuilder.bind(notaQueue)
                .to(eventoExchange)
                .with("nota.generada");
    }

    @Bean
    public Binding asistenciaBinding(Queue asistenciaQueue, TopicExchange eventoExchange) {
        return BindingBuilder.bind(asistenciaQueue)
                .to(eventoExchange)
                .with("asistencia.registrada");
    }

    // ==================== BINDINGS DE DLQ ====================

    @Bean
    public Binding matriculaDlqBinding(Queue matriculaDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(matriculaDlq)
                .to(dlxExchange)
                .with(MATRICULA_DLQ);
    }

    @Bean
    public Binding notaDlqBinding(Queue notaDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(notaDlq)
                .to(dlxExchange)
                .with(NOTA_DLQ);
    }

    @Bean
    public Binding asistenciaDlqBinding(Queue asistenciaDlq, DirectExchange dlxExchange) {
        return BindingBuilder.bind(asistenciaDlq)
                .to(dlxExchange)
                .with(ASISTENCIA_DLQ);
    }
}
