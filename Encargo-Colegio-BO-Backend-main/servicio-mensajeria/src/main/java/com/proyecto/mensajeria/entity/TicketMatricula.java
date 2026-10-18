package com.proyecto.mensajeria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets_matricula")
public class TicketMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long matriculaId;

    @Column(nullable = false)
    private Long alumnoId;

    private Long cursoId;

    @Column(nullable = false, unique = true, length = 64)
    private String codigoTicket;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comprobanteTexto;

    @Column(nullable = false, length = 32)
    private String estado;
}