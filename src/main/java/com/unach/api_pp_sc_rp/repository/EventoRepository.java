package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    // Encuentra eventos entre dos fechas
    List<Evento> findByFechaEventoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

}
