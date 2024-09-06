package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.model.enums.EstadoAvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramaRepository extends JpaRepository <Programa , Long> {

     List<Programa> findByEstudianteId(Long estudianteId);
     List<Programa> findALlByEstadoAvance (EstadoAvance estadoAvance);
     List<Programa> findByTipoProgramaId(Long tipoProgramaId);

     @Modifying
     @Query(value = "UPDATE programa SET finalizado = TRUE WHERE id = :id_programa", nativeQuery = true)
     void markProgramaAsFinished (@Param("id_programa") Long id_programa);

     @Query(value = "SELECT * FROM programas WHERE id_empresa = :id", nativeQuery = true)
     List<Programa> findByEmpresa_Id(@Param("id") Long id);
}
