package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.CarreraDTO;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CarreraService {
    CarreraDTO saveCarrera(CarreraDTO carreraDTO);
    Optional<CarreraDTO> findByIdCarrera(Long id);
    List<CarreraDTO> findAllCArreraas();

    @Transactional
    CarreraDTO updateCarrera (Long id, Map<String, Object> updates);

    void deleteByIdCarrera(Long id);

    Resource exportCarrerasToCSV();

    Resource exportCarrerasToPDF();
}
