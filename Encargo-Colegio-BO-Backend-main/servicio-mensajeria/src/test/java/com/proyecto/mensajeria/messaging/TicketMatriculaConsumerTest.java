package com.proyecto.mensajeria.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.mensajeria.entity.TicketMatricula;
import com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent;
import com.proyecto.mensajeria.service.TicketMatriculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketMatriculaConsumerTest {

    @Mock
    private TicketMatriculaService ticketMatriculaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TicketMatriculaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TicketMatriculaConsumer(ticketMatriculaService, objectMapper);
    }

    @Test
    void testProcesarMatricula_Exito() throws Exception {
        MatriculaRegistradaEvent evento = MatriculaRegistradaEvent.builder()
                .matriculaId(10L)
                .alumnoId(20L)
                .cursoId(30L)
                .estado("ACTIVA")
                .build();

        TicketMatricula ticket = TicketMatricula.builder()
                .id(1L)
                .codigoTicket("TKT-MAT-10-TEST")
                .matriculaId(10L)
                .build();

        when(ticketMatriculaService.generarTicket(any(MatriculaRegistradaEvent.class))).thenReturn(ticket);

        byte[] body = objectMapper.writeValueAsBytes(evento);
        consumer.procesarMatricula(new Message(body));

        verify(ticketMatriculaService, times(1)).generarTicket(any(MatriculaRegistradaEvent.class));
    }

    @Test
    void testProcesarMatricula_ErrorRelanzaExcepcionParaReintentoYDLQ() throws Exception {
        MatriculaRegistradaEvent evento = MatriculaRegistradaEvent.builder()
                .matriculaId(99L)
                .alumnoId(100L)
                .build();

        when(ticketMatriculaService.generarTicket(any(MatriculaRegistradaEvent.class)))
                .thenThrow(new RuntimeException("Fallo en base de datos"));

        byte[] body = objectMapper.writeValueAsBytes(evento);
        assertThrows(RuntimeException.class, () -> consumer.procesarMatricula(new Message(body)));

        verify(ticketMatriculaService, times(1)).generarTicket(any(MatriculaRegistradaEvent.class));
    }
}