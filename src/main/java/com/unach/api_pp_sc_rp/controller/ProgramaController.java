package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.dto.ProgramaDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.EstudianteService;
import com.unach.api_pp_sc_rp.service.ProgramaService;
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
@RequestMapping("/api/v1/coordinacion_programas/programas")
public class ProgramaController {

    @Autowired
    private ProgramaService programaService;


    // Crear un nuevo programa
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')") // Usuarios con rol ADMIN o STUDENT pueden registrar
    public ResponseEntity<?> savePrograma(@RequestBody ProgramaDTO newPrograma) {
        try {
            return new ResponseEntity<>(programaService.savePrograma(newPrograma), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener todos los programas
    @GetMapping()
    public ResponseEntity<?> getAllProgramas() {
        try {
            return new ResponseEntity<>(programaService.findAllProgramas(), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener un programa por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdPrograma(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(programaService.findByIdPrograma(id), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Obtener programas por estado de avance
    @GetMapping("/estado/{estadoAvance}")
    public ResponseEntity<?> getByEstadoAvance(@PathVariable String estadoAvance) {
        try {
            return new ResponseEntity<>(programaService.findAllByEstadoAvance(estadoAvance), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar un programa por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrograma(@PathVariable Long id) {
        try {
            programaService.deletePrograma(id);
            return new ResponseEntity<>(new ResponseInformative("Programa se ha eliminado con éxito"), HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar un programa
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePrograma(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            return new ResponseEntity<>(programaService.updatePrograma(id, updates), HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Actualizar un programa como finalizado
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<?> updateProgramaAsFinalizado(@PathVariable Long id) {
        try {
            programaService.updateProgramaAsFinilizado(id);
            return new ResponseEntity<>(new ResponseInformative("Programa marcado como finalizado con éxito"), HttpStatus.ACCEPTED);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Actualizar el estado de un programa
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> updateEstadoPrograma(@PathVariable Long id, @RequestParam String estado) {
        try {
            programaService.updateEstadoPrograma(id, estado);
            return new ResponseEntity<>(new ResponseInformative("Estado del programa actualizado con éxito"), HttpStatus.ACCEPTED);
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
        Resource resource = programaService.exportProgramasToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteProgramas_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = programaService.exportProgramasToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteProgramas_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }
}
