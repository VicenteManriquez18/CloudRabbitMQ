package com.proyecto.mensajeria.service;

import com.proyecto.mensajeria.entity.TicketMatricula;
import com.proyecto.mensajeria.messaging.event.MatriculaRegistradaEvent;

import java.util.Optional;

public interface TicketMatriculaService {
    TicketMatricula generarTicket(MatriculaRegistradaEvent evento);
    Optional<TicketMatricula> buscarPorMatriculaId(Long matriculaId);
    Optional<TicketMatricula> buscarPorCodigo(String codigo);
}