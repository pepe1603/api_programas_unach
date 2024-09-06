package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.AdministradorDTO;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AdministradorService {

    AdministradorDTO saveAdministrador(AdministradorDTO administradorDTO);

    AdministradorDTO findByIdentifyAdministrador(String ID_administrador);

    Optional<AdministradorDTO> findByIdAdministrador(Long id);

    List<AdministradorDTO> findAllAdministradores();

    void deleteAdministrador(Long id);

    AdministradorDTO updateAdministrador(Long id, Map<String, Object> updates);

    Resource exportAdministradoresToCSV();

    Resource exportAdministradoresToPDF();
}
