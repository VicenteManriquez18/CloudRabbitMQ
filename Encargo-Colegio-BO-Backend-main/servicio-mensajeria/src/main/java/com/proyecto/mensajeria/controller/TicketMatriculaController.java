package com.proyecto.mensajeria.controller;

import com.proyecto.mensajeria.entity.TicketMatricula;
import com.proyecto.mensajeria.service.TicketMatriculaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mensajes/tickets")
public class TicketMatriculaController {

    private final TicketMatriculaService ticketMatriculaService;

    public TicketMatriculaController(TicketMatriculaService ticketMatriculaService) {
        this.ticketMatriculaService = ticketMatriculaService;
    }

    @GetMapping("/matricula/{matriculaId}")
    public ResponseEntity<TicketMatricula> obtenerPorMatricula(@PathVariable Long matriculaId) {
        return ticketMatriculaService.buscarPorMatriculaId(matriculaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<TicketMatricula> obtenerPorCodigo(@PathVariable String codigo) {
        return ticketMatriculaService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}