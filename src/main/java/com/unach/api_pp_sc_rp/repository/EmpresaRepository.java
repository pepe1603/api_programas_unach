package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository <Empresa, Long> {
}
