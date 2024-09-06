package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository <Estudiante, Long> {
    Optional<Estudiante> findByMatricula(String matricula);
    boolean existsByCorreoInstitucional(String correo);
}
