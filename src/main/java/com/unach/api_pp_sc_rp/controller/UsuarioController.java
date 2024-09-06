package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.UsuarioDTO;
import com.unach.api_pp_sc_rp.model.Usuario;
import com.unach.api_pp_sc_rp.service.UsuarioService;
import org.hibernate.query.sqm.EntityTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/coordinacion_programas/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> saveUser(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO savedUser = usuarioService.saveUser(usuarioDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findUserById(@PathVariable Long id) {
        Optional<UsuarioDTO> usuarioDTO = usuarioService.findByIdUSer(id);
        return usuarioDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> findAllUsers() {
        try {

            List<Usuario> users = usuarioService.findAllUsers();

            return new ResponseEntity<>(users, HttpStatus.OK);
        }catch (EntityTypeException ex) {
            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsername(@PathVariable Long id, @RequestBody String newUsername) {
        try {
            UsuarioDTO updatedUser = usuarioService.updateUsername(id, newUsername);
            return ResponseEntity.ok(updatedUser);
        }catch (EntityTypeException ex) {
            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportToCSV() {
        Resource resource = usuarioService.exportUsuariosToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteUsuarios_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = usuarioService.exportUsuariosToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteUsuarios_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

}
