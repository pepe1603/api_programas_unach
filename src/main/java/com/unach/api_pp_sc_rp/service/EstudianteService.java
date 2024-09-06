package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.model.Estudiante;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EstudianteService {
    EstudianteDTO saveEstudiante(EstudianteDTO estudianteDTO);
    Optional<EstudianteDTO> findByIdEstudiante(Long id);

    EstudianteDTO findByMatricula(String matricula);

    List<EstudianteDTO> findAllEstudiantes();
    void deleteEstudiante (Long id);

    EstudianteDTO updateEstudiante(Long id, Map<String, Object> updates);

    Resource exportEstudiantesToCSV();

    Resource exportEstudiantesToPDF();
}
