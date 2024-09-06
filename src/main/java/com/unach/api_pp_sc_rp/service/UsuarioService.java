package com.unach.api_pp_sc_rp.service;

import com.unach.api_pp_sc_rp.dto.UsuarioDTO;
import com.unach.api_pp_sc_rp.model.Usuario;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UsuarioService {
    UsuarioDTO saveUser(UsuarioDTO usuarioDTO);
    Optional<UsuarioDTO> findByIdUSer(Long id);
    List<Usuario> findAllUsers();
    void deleteUser(Long id);
    UsuarioDTO updateUsername (Long userId, String newUsername);
    Optional<Usuario> findByUsername(String username);

    List<String> getAllEmails();


    List<String> getAllAdminEmails();

    Resource exportUsuariosToCSV();

    Resource exportUsuariosToPDF();
}
