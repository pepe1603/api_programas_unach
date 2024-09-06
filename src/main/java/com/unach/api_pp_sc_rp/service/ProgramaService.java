package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.ProgramaDTO;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProgramaService {
    ProgramaDTO savePrograma (ProgramaDTO programaDTO);
    Optional<ProgramaDTO> findByIdPrograma (Long id);
    List<ProgramaDTO> findAllProgramas ();

    List<ProgramaDTO> findAllByEstadoAvance(String estado);

    @Transactional
    void updateProgramaAsFinilizado(Long id);

    @Transactional
    void updateEstadoPrograma(Long id, String estadoString);

    ProgramaDTO updatePrograma (Long id, Map<String, Object> updates);
    void deletePrograma (Long id);

    Resource exportProgramasToCSV();

    Resource exportProgramasToPDF();
}
