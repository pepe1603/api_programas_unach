package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository <Administrador, Long> {

    Optional<Administrador> findByIdAdmin(String idAdmin);
    boolean existsByIdAdmin(String idAdmin);
    boolean existsByCorreoInstitucional(String Correo_institucional);
}
