package com.proyecto.mensajeria.service;

import com.proyecto.mensajeria.entity.Mensaje;
import com.proyecto.mensajeria.entity.TicketMatricula;
import com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent;
import com.proyecto.mensajeria.repository.MensajeRepository;
import com.proyecto.mensajeria.repository.TicketMatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketMatriculaServiceTest {

    @Mock
    private TicketMatriculaRepository ticketMatriculaRepository;

    @Mock
    private MensajeRepository mensajeRepository;

    private TicketMatriculaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketMatriculaServiceImpl(ticketMatriculaRepository, mensajeRepository);
    }

    @Test
    void testGenerarTicket_Exito() {
        MatriculaRegistradaEvent evento = MatriculaRegistradaEvent.builder()
                .matriculaId(15L)
                .alumnoId(33L)
                .cursoId(101L)
                .estado("CONFIRMADA")
                .build();

        when(ticketMatriculaRepository.save(any(TicketMatricula.class))).thenAnswer(invocation -> {
            TicketMatricula t = invocation.getArgument(0);
            t.setId(1L);
            return t;
        });

        TicketMatricula resultado = service.generarTicket(evento);

        assertNotNull(resultado);
        assertEquals(15L, resultado.getMatriculaId());
        assertEquals(33L, resultado.getAlumnoId());
        assertEquals(101L, resultado.getCursoId());
        assertTrue(resultado.getCodigoTicket().startsWith("TKT-MAT-15-"));
        assertTrue(resultado.getComprobanteTexto().contains("COMPROBANTE OFICIAL DE MATRICULA"));
        assertTrue(resultado.getComprobanteTexto().contains(resultado.getCodigoTicket()));

        verify(ticketMatriculaRepository, times(1)).save(any(TicketMatricula.class));
        verify(mensajeRepository, times(1)).save(any(Mensaje.class));
    }

    @Test
    void testGenerarTicket_DatosInvalidosLanzaExcepcion() {
        MatriculaRegistradaEvent eventoInvalido = new MatriculaRegistradaEvent(); // null IDs

        assertThrows(IllegalArgumentException.class, () -> service.generarTicket(eventoInvalido));
        assertThrows(IllegalArgumentException.class, () -> service.generarTicket(null));

        verifyNoInteractions(ticketMatriculaRepository);
        verifyNoInteractions(mensajeRepository);
    }
}