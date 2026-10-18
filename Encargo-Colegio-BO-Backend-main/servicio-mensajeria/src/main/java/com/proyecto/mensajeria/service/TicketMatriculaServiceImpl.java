package com.proyecto.mensajeria.service;

import com.proyecto.mensajeria.entity.Mensaje;
import com.proyecto.mensajeria.entity.TicketMatricula;
import com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent;
import com.proyecto.mensajeria.repository.MensajeRepository;
import com.proyecto.mensajeria.repository.TicketMatriculaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketMatriculaServiceImpl implements TicketMatriculaService {

    private static final Logger log = LoggerFactory.getLogger(TicketMatriculaServiceImpl.class);

    private final TicketMatriculaRepository ticketMatriculaRepository;
    private final MensajeRepository mensajeRepository;

    public TicketMatriculaServiceImpl(TicketMatriculaRepository ticketMatriculaRepository,
                                      MensajeRepository mensajeRepository) {
        this.ticketMatriculaRepository = ticketMatriculaRepository;
        this.mensajeRepository = mensajeRepository;
    }

    @Override
    @Transactional
    public TicketMatricula generarTicket(MatriculaRegistradaEvent evento) {
        if (evento == null || evento.getMatriculaId() == null || evento.getAlumnoId() == null) {
            throw new IllegalArgumentException("El evento de matrícula no contiene datos válidos (matriculaId o alumnoId nulos)");
        }

        log.info("Iniciando generación de comprobante para matrícula ID: {}", evento.getMatriculaId());

        LocalDateTime ahora = LocalDateTime.now();
        String codigoTicket = String.format("TKT-MAT-%d-%s",
                evento.getMatriculaId(),
                UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        String comprobanteTexto = construirComprobanteTexto(evento, codigoTicket, ahora);

        TicketMatricula ticket = TicketMatricula.builder()
                .matriculaId(evento.getMatriculaId())
                .alumnoId(evento.getAlumnoId())
                .cursoId(evento.getCursoId())
                .codigoTicket(codigoTicket)
                .fechaEmision(ahora)
                .comprobanteTexto(comprobanteTexto)
                .estado("EMITIDO")
                .build();

        TicketMatricula guardado = ticketMatriculaRepository.save(ticket);
        log.info("Ticket de matrícula guardado con éxito: ID {}, Código: {}", guardado.getId(), guardado.getCodigoTicket());

        // Generar notificación para el apoderado / estudiante
        try {
            Mensaje notificacion = Mensaje.builder()
                    .remitenteId(0L)
                    .remitenteNombre("Sistema Central de Matrículas")
                    .remitenteRol("ADMIN")
                    .destinatarioId(evento.getAlumnoId())
                    .destinatarioNombre("Apoderado / Alumno #" + evento.getAlumnoId())
                    .destinatarioRol("APODERADO")
                    .contenido(String.format("Estimado apoderado: Se ha emitido el comprobante oficial de matrícula %s para el estudiante #%d en el curso %s. Estado: CONFIRMADO.",
                            codigoTicket,
                            evento.getAlumnoId(),
                            evento.getCursoId() != null ? String.valueOf(evento.getCursoId()) : "General"))
                    .fechaEnvio(ahora)
                    .build();

            mensajeRepository.save(notificacion);
            log.info("Notificación para apoderado creada exitosamente en bandeja de mensajería.");
        } catch (Exception ex) {
            log.warn("No se pudo crear la notificación de mensaje: {}", ex.getMessage());
        }

        return guardado;
    }

    @Override
    public Optional<TicketMatricula> buscarPorMatriculaId(Long matriculaId) {
        return ticketMatriculaRepository.findByMatriculaId(matriculaId);
    }

    @Override
    public Optional<TicketMatricula> buscarPorCodigo(String codigo) {
        return ticketMatriculaRepository.findByCodigoTicket(codigo);
    }

    private String construirComprobanteTexto(MatriculaRegistradaEvent evento, String codigoTicket, LocalDateTime fecha) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "==================================================\n" +
               "        COMPROBANTE OFICIAL DE MATRICULA          \n" +
               "                COLEGIO BO CLOUD                  \n" +
               "==================================================\n" +
               "Nro. Ticket   : " + codigoTicket + "\n" +
               "Matrícula ID  : " + evento.getMatriculaId() + "\n" +
               "Alumno ID     : " + evento.getAlumnoId() + "\n" +
               "Curso ID      : " + (evento.getCursoId() != null ? evento.getCursoId() : "N/A") + "\n" +
               "Fecha Emisión : " + fecha.format(fmt) + "\n" +
               "Estado        : " + (evento.getEstado() != null ? evento.getEstado() : "ACTIVA") + "\n" +
               "Firma Digital : " + UUID.randomUUID() + "\n" +
               "==================================================\n" +
               "Este comprobante acredita la inscripción oficial. \n" +
               "==================================================";
    }
}