package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.ArchivoDTO;
import com.unach.api_pp_sc_rp.dto.ArchivoRequestDTO;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.service.ArchivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coordinacion_programas/archivos")
public class ArchivoController {

    @Autowired
    private ArchivoService archivoService;

    /**
     * Crea un nuevo archivo.
     *
     * @param file Archivo a subir.
     * @param idPrograma ID del programa asociado.
     * @param nombreArchivo Nombre opcional del archivo.
     * @return ResponseEntity con el archivo creado o un mensaje de error.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadArchivo(@RequestParam("file") MultipartFile file,
                                           @RequestParam("idPrograma") Long idPrograma,
                                           @RequestParam(value = "nombreArchivo", required = false) String nombreArchivo) {
        try {
            if (file.isEmpty() || idPrograma == null) {
                return new ResponseEntity<>("El archivo y el ID del programa son requeridos.", HttpStatus.BAD_REQUEST);
            }

            ArchivoRequestDTO archivoRequestDTO = new ArchivoRequestDTO();
            archivoRequestDTO.setFile(file);
            archivoRequestDTO.setIdPrograma(idPrograma);
            archivoRequestDTO.setNombreArchivo(nombreArchivo);

            ArchivoDTO archivoDTO = archivoService.saveArchivo(archivoRequestDTO);
            return new ResponseEntity<>(archivoDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException | EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene un archivo por su ID.
     *
     * @param id ID del archivo.
     * @return ResponseEntity con el archivo o un mensaje de error.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getArchivoById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(archivoService.findByIdArchivo(id), HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todos los archivos.
     *
     * @return ResponseEntity con la lista de archivos o un mensaje de error.
     */
    @GetMapping()
    public ResponseEntity<?> getAllArchivos() {
        try {
            List<ArchivoDTO> archivos = archivoService.findAllArchivos();
            return new ResponseEntity<>(archivos, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene archivos por el ID del programa.
     *
     * @param idPrograma ID del programa.
     * @return ResponseEntity con la lista de archivos o un mensaje de error.
     */
    @GetMapping("/programa/{idPrograma}")
    public ResponseEntity<?> getArchivosByProgramaId(@PathVariable Long idPrograma) {
        try {
            List<ArchivoDTO> archivos = archivoService.findArchivoByProgramaId(idPrograma);
            return new ResponseEntity<>(archivos, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un archivo por su ID.
     *
     * @param id ID del archivo a eliminar.
     * @return ResponseEntity con un mensaje de éxito o un mensaje de error.
     */
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteArchivo(@PathVariable Long id) {
        try {
            archivoService.deleteByIdArchivo(id);
            return new ResponseEntity<>(new ResponseInformative("Archivo eliminado con éxito"), HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Actualiza un archivo existente.
     *
     * @param id ID del archivo a actualizar.
     * @param file Archivo opcional para actualizar.
     * @param nombreArchivo Nombre opcional del archivo.
     * @return ResponseEntity con el archivo actualizado o un mensaje de error.
     */
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateArchivo(@PathVariable Long id,
                                           @RequestParam(value = "file", required = false) MultipartFile file,
                                           @RequestParam(value = "nombreArchivo", required = false) String nombreArchivo) {
        try {
            ArchivoRequestDTO archivoRequestDTO = new ArchivoRequestDTO();
            archivoRequestDTO.setFile(file);
            archivoRequestDTO.setNombreArchivo(nombreArchivo);

            ArchivoDTO archivoDTO = archivoService.updateArchivo(id, archivoRequestDTO);
            return new ResponseEntity<>(archivoDTO, HttpStatus.OK);
        } catch (EntityNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToCSV() {
        Resource resource = archivoService.exportArchivosToCSV();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteArchivos_%s.csv", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportToPDF() {
        Resource resource = archivoService.exportArchivosToPDF();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("reporteArchivos_%s.pdf", timestamp);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }
}
