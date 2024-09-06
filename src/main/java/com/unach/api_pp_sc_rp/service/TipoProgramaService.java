package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.TipoProgramaDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TipoProgramaService {
    TipoProgramaDTO saveTipoPrograma(TipoProgramaDTO tipoProgramaDTO);

    Optional<TipoProgramaDTO> findByNombreTipoPrograma(String nombre);

    Optional<TipoProgramaDTO> findByIdTipoPrograma(Long id);
    List<TipoProgramaDTO> findAllTipoPrograma();

    TipoProgramaDTO updateTipoPrograma (Long id, Map<String, Object> updates);

    void deleteByIdTipoPrograma(Long id);

    Resource exportTipoProgramasToCSV();

    Resource exportTipoProgramasToPDF();
}
