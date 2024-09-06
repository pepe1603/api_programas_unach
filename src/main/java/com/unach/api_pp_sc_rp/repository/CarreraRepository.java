package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository <Carrera , Long> {
    boolean existsByNombre(String nombre);

}
