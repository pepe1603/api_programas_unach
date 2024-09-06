package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.ArchivoDTO;
import com.unach.api_pp_sc_rp.dto.ArchivoRequestDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ArchivoService {
    ArchivoDTO saveArchivo(ArchivoRequestDTO archivoRequestDTO);
    Optional<ArchivoDTO> findByIdArchivo(Long id);

    List<ArchivoDTO> findAllArchivos();

    List<ArchivoDTO> findArchivoByProgramaId(Long programaId);
    void deleteByIdArchivo(Long id);


    ArchivoDTO updateArchivo(Long id, ArchivoRequestDTO archivoRequestDTO);

    Resource exportArchivosToCSV();

    Resource exportArchivosToPDF();
}
