package com.proyecto.ColegioBackend.messaging;

import com.proyecto.ColegioBackend.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DeadLetterConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.REGISTRO_DLQ)
    public void procesarRegistroDlq(Message message) {
        try {
            MessageProperties props = message.getMessageProperties();
            Object deathHeader = props.getHeaders().get("x-death");
            Object firstDeathReason = props.getHeaders().get("x-first-death-reason");
            String payload = message.getBody() != null ? new String(message.getBody(), StandardCharsets.UTF_8) : "<vacio>";

            log.error("==================== [DLQ AUDITORIA - MATRICULA] ====================");
            log.error("Cola de origen: {}", RabbitMQConfig.REGISTRO_QUEUE);
            log.error("Motivo de descarte (x-first-death-reason): {}", firstDeathReason);
            log.error("Detalle x-death: {}", deathHeader);
            log.error("Contenido del mensaje: {}", payload);
            log.error("=====================================================================");
        } catch (Exception ex) {
            log.error("Error al registrar mensaje en DLQ de matricula: {}", ex.getMessage());
        }
    }
}
