package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoRepository extends JpaRepository <Archivo, Long> {
    List<Archivo> findByProgramaId(Long programaId);
    Optional<Archivo> findByNombreArchivo (String nombreArchivo);

}
