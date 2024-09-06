package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.AdministradorDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coordinacion_programas/administradores")
@PreAuthorize("hasRole('ADMIN')")
public class AdministradorController {

    @Autowired
    private AdministradorService adminisService;

    /*crea un nuevo administrador*/
    @PostMapping("/register")
    public ResponseEntity<?> saveAdministrador(
            @RequestBody AdministradorDTO newadministrador
            ){
        try {
            return new ResponseEntity<>(
                    adminisService.saveAdministrador(newadministrador),
                    HttpStatus.CREATED
            );
        }catch (IllegalArgumentException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todos los archivos.
     *
     * @return ResponseEntity con la lista de administradores o un mensaje de error.
     */
    @GetMapping()
    public ResponseEntity<?> getAllAdminstradores (){
        try {
            return new ResponseEntity<>(
                    adminisService.findAllAdministradores(),
                    HttpStatus.OK
            );
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un archivo por su ID.
     *
     * @Pathvariable id ID del archivo.
     * @return ResponseEntity con l entidad administrador o un mensaje de error.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdAdministrador (
            @PathVariable Long id
    ){
        try {
            return new ResponseEntity<>(
                    adminisService.findByIdAdministrador(id),
                    HttpStatus.OK
            );
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un archivo por su ID.
     *
     * @pathvariable identifyAdministrador ID del archivo.
     * @return ResponseEntity con el entidad administrador o un mensaje de error.
     */
    @GetMapping("/identify/{identifyAdministrador}")
    public ResponseEntity<?> getByIdentifyAdministrador (
            @PathVariable String identifyAdministrador
    ){
        try {
            return new ResponseEntity<>(
                    adminisService.findByIdentifyAdministrador(identifyAdministrador),
                    HttpStatus.OK
            );
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Actualiza todos los campos de un administrador o
     * solo los que se requieran por su ID.
     *
     * @patchvariable id ID del administrador.
     * @return ResponseEntity con el administrador actualizado o un mensaje de error.
     */
    @PatchMapping("patch/{id}")
    public ResponseEntity<?> updateAdministrador (
            @PathVariable Long id,
            @RequestBody Map<String , Object> updates
            ){
        try {
            return new ResponseEntity<>(
                    adminisService.updateAdministrador(id, updates),
                    HttpStatus.ACCEPTED
            );
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }catch (IllegalArgumentException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un administrador por su ID.
     *
     * @pathvariable id ID del archivo a eliminar.
     * @return ResponseEntity con un mensaje de éxito (STATUS 204) o un mensaje de error.
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteAdministrador (
            @PathVariable Long id
    ){
        try {
            adminisService.deleteAdministrador(id);
            return new ResponseEntity<>(
                    new ResponseInformative("Administrador se ha elimnado con exito"),
                    HttpStatus.NO_CONTENT
            );
        }catch (EntityNotFoundException ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportToCSV() {
        Resource resource = adminisService.exportAdministradoresToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteAdministradores_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = adminisService.exportAdministradoresToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteAdminstradores_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

}
