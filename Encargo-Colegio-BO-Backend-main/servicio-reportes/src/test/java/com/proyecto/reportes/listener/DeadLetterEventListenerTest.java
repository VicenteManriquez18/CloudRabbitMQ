package com.proyecto.reportes.listener;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DeadLetterEventListenerTest {

    private final DeadLetterEventListener listener = new DeadLetterEventListener();

    @Test
    public void testHandleMatriculaDlqDoesNotThrow() {
        MessageProperties props = new MessageProperties();
        props.setHeader("x-first-death-reason", "rejected");
        props.setHeader("x-death", Collections.singletonList("matricula-death-detail"));
        Message message = new Message("{\"matriculaId\":123}".getBytes(StandardCharsets.UTF_8), props);

        assertDoesNotThrow(() -> listener.handleMatriculaDlq(message));
    }

    @Test
    public void testHandleNotaDlqDoesNotThrow() {
        MessageProperties props = new MessageProperties();
        props.setHeader("x-first-death-reason", "expired");
        Message message = new Message("{\"notaId\":456}".getBytes(StandardCharsets.UTF_8), props);

        assertDoesNotThrow(() -> listener.handleNotaDlq(message));
    }

    @Test
    public void testHandleAsistenciaDlqDoesNotThrow() {
        MessageProperties props = new MessageProperties();
        props.setHeader("x-first-death-reason", "maxlen");
        Message message = new Message("{\"asistenciaId\":789}".getBytes(StandardCharsets.UTF_8), props);

        assertDoesNotThrow(() -> listener.handleAsistenciaDlq(message));
    }
}