package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.EmpresaDTO;
import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.EmpresaService;
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
@RequestMapping("/api/v1/coordinacion_programas/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveEmpresa(
            @RequestBody EmpresaDTO newEmpresa
    ){
        try {
            return new ResponseEntity<>(
                    empresaService.saveEmpresa(newEmpresa),
                    HttpStatus.CREATED);
        }catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping()
    public ResponseEntity<?> getAllEmpresas (){
        try {
            return new ResponseEntity<>(
                    empresaService.findAllEmpresas(), HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdEmpresa (
            @PathVariable Long id
    ){
        try {
            return new ResponseEntity<>(
                    empresaService.findByIdEmpresa(id)
                    ,HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmpresa(
            @PathVariable Long id
    ) {
        try {
            empresaService.deleteByIdEmpresa(id);
            return new ResponseEntity<>(
                    new ResponseInformative("Empresa se ha eliminado con exito"),
                    HttpStatus.NO_CONTENT
            );
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmpresa(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            return new ResponseEntity<>(
                    empresaService.updateEmpresa(id, updates),
                    HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToCSV() {
        Resource resource = empresaService.exportEmpresasToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteEmpresas_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = empresaService.exportEmpresasToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reportEmpresas_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

}
