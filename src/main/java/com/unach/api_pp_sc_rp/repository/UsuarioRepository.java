package com.unach.api_pp_sc_rp.repository;

import com.unach.api_pp_sc_rp.model.Usuario;
import com.unach.api_pp_sc_rp.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByUsername( String username);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRoles(Role role);//obtener todos los ususarios con rol

    boolean existsByEmail(String email);
}
