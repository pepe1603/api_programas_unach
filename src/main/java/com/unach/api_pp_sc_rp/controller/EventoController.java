package com.unach.api_pp_sc_rp.controller;


import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.dto.EventoDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coordinacion_programas/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    // Solo ADMIN puede registrar un nuevo evento
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveEvento(
            @RequestBody EventoDTO newEvento
    ){
        try {
            return new ResponseEntity<>(
                    eventoService.saveEvento(newEvento),
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
    public ResponseEntity<?> getAllEventos (){
        try {
            return new ResponseEntity<>(
                    eventoService.findAllEventos(), HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdEvento (
            @PathVariable Long id
    ){
        try {
            return new ResponseEntity<>(
                    eventoService.findByIdEvento(id)
                    ,HttpStatus.OK
            );
        }catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')") // Usuarios con rol ADMIN o STUDENT pueden ver eventos
    @GetMapping("/entreFechas")
    public ResponseEntity<?> getEventosEntreFechas(
            @RequestParam("fechaInicio") @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME )LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat( iso = DateTimeFormat.ISO.DATE_TIME )LocalDateTime fechaFin
            ) {
        try {
            List<EventoDTO> eventos = eventoService.getEventosEntreFechas(fechaInicio, fechaFin);
            return new ResponseEntity<>(eventos, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Solo ADMIN puede eliminar un evento
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEvento(
            @PathVariable Long id
    ) {
        try {
            eventoService.deleteByIdEvento(id);
            return new ResponseEntity<>(
                    new ResponseInformative("Estudiante se ha eliminado con exito"),
                    HttpStatus.NO_CONTENT
            );
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Solo ADMIN puede actualizar el estado del evento
    @PutMapping("/{id}/estadoEvento/{estadoEvento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEstadoEvento(
            @PathVariable Long id,
            @PathVariable String estadoEvento) {
        try {

            eventoService.updateEstadoEvento(id, estadoEvento);
            return new ResponseEntity<>(
                    new ResponseInformative("El estado del Evento ha sido actualizado [ "+estadoEvento+" ]"),

                    HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Solo ADMIN puede actualizar un evento con PATCH
    @PatchMapping("patch/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEvento(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            return new ResponseEntity<>(
                    eventoService.updateEvento(id, updates),
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
        Resource resource = eventoService.exportEventosToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteEventos_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = eventoService.exportEventosToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteEventos_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }



}
