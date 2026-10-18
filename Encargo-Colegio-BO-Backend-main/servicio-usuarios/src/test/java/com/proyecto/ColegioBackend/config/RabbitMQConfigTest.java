package com.proyecto.ColegioBackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

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
    public void testRegistroQueueHasDlxArguments() {
        Queue queue = config.queue();
        assertNotNull(queue);
        assertEquals("usuario_registro_queue", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("dlx.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("usuario_registro_queue.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
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
}