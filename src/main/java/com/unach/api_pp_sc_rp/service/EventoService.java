package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.EventoDTO;
import com.unach.api_pp_sc_rp.model.enums.EstadoEvento;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventoService {
    EventoDTO saveEvento(EventoDTO eventoDTO);
    Optional<EventoDTO> findByIdEvento(Long id);
    List<EventoDTO> findAllEventos();

    @Transactional
    List<EventoDTO> getEventosEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    EventoDTO updateEvento (Long id, Map<String, Object> updates);

    void updateEstadoEvento(Long id, String nuevoEstado);

    void deleteByIdEvento(Long id);

    Resource exportEventosToCSV();

    Resource exportEventosToPDF();
}
