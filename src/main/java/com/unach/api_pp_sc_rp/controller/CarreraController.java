package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.CarreraDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.CarreraService;
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
@RequestMapping("/api/v1/coordinacion_programas/carreras")
public class CarreraController {

    @Autowired
    private CarreraService carreraService;


    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveCarrera (
            @RequestBody CarreraDTO newCarrera
            ) {
        try {
            return new ResponseEntity<>(
              carreraService.saveCarrera(newCarrera),
                    HttpStatus.CREATED
            );
        }catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCarreras (){
        try {
            return new ResponseEntity<>(
              carreraService.findAllCArreraas(),
              HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdCarrera (
            @PathVariable Long id
    ){
        try {
            return new ResponseEntity<>(
                    carreraService.findByIdCarrera(id),
                    HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCarrera (
            @PathVariable Long id,
            @RequestBody Map< String, Object> updates
    ){
        try {
            return new ResponseEntity<>(
                    carreraService.updateCarrera(id, updates),
                    HttpStatus.ACCEPTED
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCarrera (
            @PathVariable Long id
    ){
        try {
            return new ResponseEntity<>(
                    new ResponseInformative("Carrera se ha elimnado con exito"),
                    HttpStatus.NO_CONTENT
            );
        }catch (EntityNotFoundException ex) {
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
        Resource resource = carreraService.exportCarrerasToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteCarreras_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = carreraService.exportCarrerasToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteCarreras_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }
}
