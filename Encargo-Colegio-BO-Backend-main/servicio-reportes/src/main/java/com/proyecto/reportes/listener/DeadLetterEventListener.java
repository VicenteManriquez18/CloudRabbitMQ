package com.proyecto.reportes.listener;

import com.proyecto.reportes.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DeadLetterEventListener {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterEventListener.class);

    @RabbitListener(queues = RabbitMqConfig.MATRICULA_DLQ)
    public void handleMatriculaDlq(Message message) {
        logDlqMessage(RabbitMqConfig.MATRICULA_QUEUE, message);
    }

    @RabbitListener(queues = RabbitMqConfig.NOTA_DLQ)
    public void handleNotaDlq(Message message) {
        logDlqMessage(RabbitMqConfig.NOTA_QUEUE, message);
    }

    @RabbitListener(queues = RabbitMqConfig.ASISTENCIA_DLQ)
    public void handleAsistenciaDlq(Message message) {
        logDlqMessage(RabbitMqConfig.ASISTENCIA_QUEUE, message);
    }

    private void logDlqMessage(String originalQueue, Message message) {
        try {
            MessageProperties props = message.getMessageProperties();
            Object deathHeader = props.getHeaders().get("x-death");
            Object firstDeathReason = props.getHeaders().get("x-first-death-reason");
            String payload = message.getBody() != null ? new String(message.getBody(), StandardCharsets.UTF_8)
                    : "<vacio>";

            log.error("==================== [DLQ AUDITORIA - REPORTES] ====================");
            log.error("Cola de origen: {}", originalQueue);
            log.error("Motivo de descarte (x-first-death-reason): {}", firstDeathReason);
            log.error("Detalle x-death: {}", deathHeader);
            log.error("Contenido del mensaje: {}", payload);
            log.error("====================================================================");
        } catch (Exception ex) {
            log.error("Error al registrar mensaje de DLQ (no relanzado para no reencolar): {}", ex.getMessage());
        }
    }
}
