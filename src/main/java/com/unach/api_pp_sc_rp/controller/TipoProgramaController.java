package com.unach.api_pp_sc_rp.controller;


import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.dto.TipoProgramaDTO;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.TipoProgramaService;
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
@RequestMapping("/api/v1/coordinacion_programas/tiposProgramas")
public class TipoProgramaController {

    @Autowired
    private TipoProgramaService tipoProgramaService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveTiipoPrograma(
            @RequestBody TipoProgramaDTO newTipoPrograma
    ){
        try {
            return new ResponseEntity<>(
                    tipoProgramaService.saveTipoPrograma(newTipoPrograma),
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
    public ResponseEntity<?> getAllTipoProgramas (){
        try {
            return new ResponseEntity<>(
                    tipoProgramaService.findAllTipoPrograma()
                    , HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdTipoProgramas (
            @PathVariable Long id
    ){
        try {
            return new ResponseEntity<>(
                    tipoProgramaService.findByIdTipoPrograma(id)
                    ,HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<?> getByIdTipoProgramas (
            @PathVariable String nombre
    ){
        try {
            return new ResponseEntity<>(
                    tipoProgramaService.findByNombreTipoPrograma(nombre)
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
    public ResponseEntity<?> deleteTipoPrograma(
            @PathVariable Long id
    ) {
        try {
            tipoProgramaService.deleteByIdTipoPrograma(id);
            return new ResponseEntity<>(
                    new ResponseInformative("Tipo prgroama ha sido eliminado con exito"),
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
    public ResponseEntity<?> updateTipoPrograma(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            return new ResponseEntity<>(
                    tipoProgramaService.updateTipoPrograma(id, updates),
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
        Resource resource = tipoProgramaService.exportTipoProgramasToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteTipoPrograma_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = tipoProgramaService.exportTipoProgramasToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteTipoPrograma_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }


}
