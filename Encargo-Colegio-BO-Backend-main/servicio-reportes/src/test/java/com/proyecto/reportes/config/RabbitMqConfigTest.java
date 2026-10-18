package com.proyecto.reportes.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitMqConfigTest {

    private final RabbitMqConfig config = new RabbitMqConfig();

    @Test
    public void testDlxExchangeConfigured() {
        DirectExchange dlx = config.dlxExchange();
        assertNotNull(dlx);
        assertEquals("dlx.exchange", dlx.getName());
        assertTrue(dlx.isDurable());
    }

    @Test
    public void testMatriculaQueueHasDlxArguments() {
        Queue queue = config.matriculaQueue();
        assertNotNull(queue);
        assertEquals("eventos.matricula.queue", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("dlx.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("eventos.matricula.queue.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    public void testNotaQueueHasDlxArguments() {
        Queue queue = config.notaQueue();
        assertNotNull(queue);
        assertEquals("eventos.nota.queue", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("dlx.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("eventos.nota.queue.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    public void testAsistenciaQueueHasDlxArguments() {
        Queue queue = config.asistenciaQueue();
        assertNotNull(queue);
        assertEquals("eventos.asistencia.queue", queue.getName());
        assertTrue(queue.isDurable());
        assertEquals("dlx.exchange", queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals("eventos.asistencia.queue.dlq", queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    public void testDlqQueuesExistAndDurable() {
        Queue matDlq = config.matriculaDlq();
        Queue notaDlq = config.notaDlq();
        Queue asistDlq = config.asistenciaDlq();

        assertNotNull(matDlq);
        assertEquals("eventos.matricula.queue.dlq", matDlq.getName());
        assertTrue(matDlq.isDurable());

        assertNotNull(notaDlq);
        assertEquals("eventos.nota.queue.dlq", notaDlq.getName());
        assertTrue(notaDlq.isDurable());

        assertNotNull(asistDlq);
        assertEquals("eventos.asistencia.queue.dlq", asistDlq.getName());
        assertTrue(asistDlq.isDurable());
    }
}