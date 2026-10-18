package com.proyecto.mensajeria.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.mensajeria.config.RabbitMQConfig;
import com.proyecto.mensajeria.entity.TicketMatricula;
import com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent;
import com.proyecto.mensajeria.service.TicketMatriculaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TicketMatriculaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketMatriculaConsumer.class);

    private final TicketMatriculaService ticketMatriculaService;
    private final ObjectMapper objectMapper;

    public TicketMatriculaConsumer(TicketMatriculaService ticketMatriculaService, ObjectMapper objectMapper) {
        this.ticketMatriculaService = ticketMatriculaService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.TICKETS_MATRICULA_QUEUE)
    public void procesarMatricula(Message message) {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("Consumidor 'tickets.matricula.queue' recibió mensaje raw: {}", json);
        try {
            MatriculaRegistradaEvent evento = objectMapper.readValue(json, MatriculaRegistradaEvent.class);
            TicketMatricula ticket = ticketMatriculaService.generarTicket(evento);
            log.info("Ticket #{} generado y persistido exitosamente.", ticket.getCodigoTicket());
        } catch (Exception e) {
            log.error("Fallo al procesar evento para ticket de matrícula (activando reintento y DLQ): {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}