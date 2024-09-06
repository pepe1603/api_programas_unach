package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.EmpresaDTO;
import com.unach.api_pp_sc_rp.model.Empresa;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmpresaService {
    EmpresaDTO saveEmpresa(EmpresaDTO empresaDTO);
    Optional<EmpresaDTO> findByIdEmpresa(Long id);
    List<EmpresaDTO> findAllEmpresas();
    EmpresaDTO updateEmpresa (Long id, Map <String, Object> updates);
    void deleteByIdEmpresa(Long id);


    Resource exportEmpresasToCSV();

    Resource exportEmpresasToPDF();
}
