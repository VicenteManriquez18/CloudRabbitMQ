package com.proyecto.mensajeria.repository;

import com.proyecto.mensajeria.entity.TicketMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketMatriculaRepository extends JpaRepository<TicketMatricula, Long> {
    Optional<TicketMatricula> findByMatriculaId(Long matriculaId);
    Optional<TicketMatricula> findByCodigoTicket(String codigoTicket);
}