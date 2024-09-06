package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.UsuarioDTO;
import com.unach.api_pp_sc_rp.exception.AuthException;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.UsuarioMapper;
import com.unach.api_pp_sc_rp.model.Usuario;
import com.unach.api_pp_sc_rp.model.enums.Role;
import com.unach.api_pp_sc_rp.repository.UsuarioRepository;
import com.unach.api_pp_sc_rp.service.UsuarioService;
import com.unach.api_pp_sc_rp.service.export.ExportService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {


    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepo;

    private final UsuarioMapper userMapper;

    private final ExportService exportService;


    @Override
    public UsuarioDTO saveUser(UsuarioDTO usuarioDTO) {

        Usuario newUser = userMapper.toEntity(usuarioDTO);

        Usuario saveUser = usuarioRepo.save(newUser);
        logger.debug("new Uuser saved : {}", newUser.getUsername());
        return userMapper.toDTO(saveUser);
    }

    @Override
    public Optional<UsuarioDTO> findByIdUSer(Long id) {
        return usuarioRepo.findById(id)
                .map(userMapper::toDTO);
    }

    @Override
    public List<Usuario> findAllUsers() {
        List<Usuario> usuarios = usuarioRepo.findAll();

        if (usuarios.isEmpty()){
            throw new EntityNotFoundException("No hay usuarios regiistrados ne el Sistema");
        }
        return usuarios;
    }

    @Override
    public void deleteUser(Long id) {
        if(this.findByIdUSer(id).isEmpty()){
            throw new UsernameNotFoundException("No se pudo eliminar el usuario por que no fue encontrado");
        }
        usuarioRepo.deleteById(id);
    }


    @Override
    public UsuarioDTO updateUsername(Long userId, String newUsername) {
        logger.debug("Finding user by username: {}", newUsername);
        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuarioRepo.existsByUsername(newUsername)) {
            logger.error("Update failed for user: {}", newUsername);
            throw new AuthException("El nombre de usuario ya está en uso");
        }
        logger.debug("Updating user: {}", usuario.getUsername());

        usuario.setUsername(newUsername);
        usuario = usuarioRepo.save(usuario);
        logger.info("User updated successfully: {}", usuario.getUsername());
        return userMapper.toDTO(usuario);
    }

    /*
    * Cambio de Nombre de Usuario: Se maneja en UsuarioService ya que es una actualización de datos generales del usuario.
    * */
    @Override
    public Optional< Usuario> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return usuarioRepo.findByUsername(username);

    }

    /**
     * # 4 -- metodo usado por  notificationService para eventos
     * Función: Este servicio se encarga de obtener los correos electrónicos de los usuarios desde la base de datos.
     * Se puede filtrar los correos por rol (por ejemplo, solo los estudiantes) o cualquier otro criterio que se necesit..
     *
     * */

    @Override
    public List<String> getAllEmails() {
        return usuarioRepo.findAll()
                .stream()
                .map(usuario -> usuario.getEmail())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllAdminEmails() {
        // Obtén la lista de usuarios con el rol ADMIN
        List<Usuario> admins = usuarioRepo.findByRoles(Role.ADMIN);
        logger.info("lista de usuarios obetenida parar envios de correos electronicos: \n|-=> {}", admins);

        // Extrae y retorna los correos electrónicos de estos usuarios
        return admins.stream()
                .map(Usuario::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public Resource exportUsuariosToCSV() {

        List<Usuario> usuarios = findAllUsers();
        if (usuarios.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }


        List<String> headers = List.of("Username", "Password", "Email", "Rol");
        List<List<String>> data = usuarios.stream()
                .map(actividad -> List.of(
                        actividad.getUsername().toString(),
                        actividad.getPassword().toString(),
                        actividad.getEmail().toString(),
                        actividad.getRoles().toString()
                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportUsuariosToPDF() {
        List<Usuario> usuarios = findAllUsers();
        if (usuarios.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }


        List<String> headers = List.of("Username", "Password", "Email", "Rol");
        List<List<String>> data = usuarios.stream()
                .map(actividad -> List.of(
                        actividad.getUsername().toString(),
                        actividad.getPassword().toString(),
                        actividad.getEmail().toString(),
                        actividad.getRoles().toString()
                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al PDF.");
        }

        String title = "Reporte de Usuarios";
        return exportService.exportToPDF(title, headers, data);
    }



}
