package com.proyecto.mensajeria.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    public void testDlxExchangeConfigured() {
        DirectExchange dlx = config.dlxExchange();
        assertNotNull(dlx);
        assertEquals("dlx.exchange", dlx.getName());
        assertTrue(dlx.isDurable());
    }

    @Test
    public void testChatQueueHasDlxArguments() {
        Queue queue = config.chatQueue();
        assertNotNull(queue);
        assertEquals("chat_mensajes_queue", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("dlx.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("chat_mensajes_queue.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    public void testChatDlqConfigured() {
        Queue dlq = config.chatDlq();
        assertNotNull(dlq);
        assertEquals("chat_mensajes_queue.dlq", dlq.getName());
        assertTrue(dlq.isDurable());
    }

    @Test
    public void testEventosExchangeConfigured() {
        TopicExchange exchange = config.eventosExchange();
        assertNotNull(exchange);
        assertEquals("eventos.exchange", exchange.getName());
        assertTrue(exchange.isDurable());
        assertFalse(exchange.isAutoDelete());
    }

    @Test
    public void testTicketsMatriculaQueueHasDlxArguments() {
        Queue queue = config.ticketsMatriculaQueue();
        assertNotNull(queue);
        assertEquals("tickets.matricula.queue", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("dlx.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("tickets.matricula.queue.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    public void testTicketsMatriculaBinding() {
        Queue queue = config.ticketsMatriculaQueue();
        TopicExchange exchange = config.eventosExchange();
        Binding binding = config.ticketsMatriculaBinding(queue, exchange);
        assertNotNull(binding);
        assertEquals("tickets.matricula.queue", binding.getDestination());
        assertEquals("eventos.exchange", binding.getExchange());
        assertEquals("matricula.registrada", binding.getRoutingKey());
    }

    @Test
    public void testTicketsMatriculaDlqConfigured() {
        Queue dlq = config.ticketsMatriculaDlq();
        assertNotNull(dlq);
        assertEquals("tickets.matricula.queue.dlq", dlq.getName());
        assertTrue(dlq.isDurable());
    }
}