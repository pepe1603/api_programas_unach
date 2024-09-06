package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.dto.TipoProgramaDTO;
import com.unach.api_pp_sc_rp.model.TipoPrograma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoProgramaRepository extends JpaRepository <TipoPrograma, Long> {

    boolean existsByNombre(String nombre);
    Optional<TipoPrograma> findByNombre(String nombre);

}
