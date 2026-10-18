package com.proyecto.mensajeria.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DeadLetterConsumerTest {

    private final DeadLetterConsumer consumer = new DeadLetterConsumer();

    @Test
    public void testProcesarMensajeDlqDoesNotThrow() {
        MessageProperties props = new MessageProperties();
        props.setHeader("x-first-death-reason", "rejected");
        props.setHeader("x-death", Collections.singletonList("chat-death"));
        Message message = new Message("{\"contenido\":\"error en chat\"}".getBytes(StandardCharsets.UTF_8), props);

        assertDoesNotThrow(() -> consumer.procesarMensajeDlq(message));
    }
}