package com.unach.api_pp_sc_rp.controller;

import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import com.unach.api_pp_sc_rp.exception.FileNotFoundException;
import com.unach.api_pp_sc_rp.exception.FileStorageException;
import com.unach.api_pp_sc_rp.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/v1/coordinacion_programas/files")
//@PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')") // Usuarios con rol ADMIN o STUDENT pueden ver eventos
public class FileDownloadController {

    private static final Logger log = LoggerFactory.getLogger(FileDownloadController.class);
    @Autowired
    private FileService fileService;
    @Autowired
    private ProgramaService programaExportService;
    @Autowired
    private EstudianteService estudianteExportService;
    @Autowired
    private CarreraService carreraExportService;
    @Autowired
    private EmpresaService empresaExportService;
    @Autowired
    private ArchivoService archivoExportService;
    @Autowired
    private AdministradorService administradorExportService;
    @Autowired
    private EventoService eventoExportService;
    @Autowired
    private TipoProgramaService tipoProgramaExportService;
    @Autowired
    private UsuarioService usuarioExportService;


    /**
     * Endpoint para descargar un archivo por su nombre.
     *uyik
     * @param filename Nombre del archivo a descargar.
     * @return ResponseEntity con el archivo o un error si no se encuentra.
     */
    @GetMapping("/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        try {
            Resource resource = fileService.load(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.NOT_FOUND);
        } catch (FileStorageException ex) {
            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            fileService.delete(filename);
            return new ResponseEntity<>(new ResponseInformative("Archivo eliminado con exito del servidor"),
                    HttpStatus.OK);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.NOT_FOUND);
        } catch (FileStorageException ex) {
            return new ResponseEntity<>(ex.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /*
    METODO SOLOACESASIBLE A ROL AADMIN
    * Metood para Descargar Csv ded registro completo del systema :
    *
    * 1.- Estudiantes.
    * 2.- Empresas
    * 3.- Carreras
    * 4.- TiposPrograga
    * 5.- Programas.
    * 6.- Archivos
    * 7.- Eventos.
    * 8.- Ususarios
    * 9.- Adminstradores
    *
    *  */
    @GetMapping("/export/all/zip")
    //@PreAuthorize("hasRole('ADMIN')") // Solo accesible para ADMIN
    public ResponseEntity<Resource> exportAllToZIP() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            log.info("Lsitando Archivcos csv...");
            // Agregar archivos CSV al ZIP
            log.warn("Obteniendo csv programas");
            addToZip(zos, "programas.csv", programaExportService.exportProgramasToCSV());
            log.warn("obteniendo csv estudiantes");
            addToZip(zos, "estudiantes.csv", estudianteExportService.exportEstudiantesToCSV());
            log.warn("obteniendo csv empresas");
            addToZip(zos, "empresas.csv", empresaExportService.exportEmpresasToCSV());
            log.warn("obteniendo csv carreras");
            addToZip(zos, "carreras.csv", carreraExportService.exportCarrerasToCSV());
            log.warn("obteniendo csv tipos_prortamas");
            addToZip(zos, "tipoprogramas.csv", tipoProgramaExportService.exportTipoProgramasToCSV());
            log.warn("obteniendo csv archivos");
            addToZip(zos, "archivos.csv", archivoExportService.exportArchivosToCSV());
            log.warn("obteniendo csv eventos");
            addToZip(zos, "eventos.csv", eventoExportService.exportEventosToCSV());
            log.warn("obteniendo csv Usuarios");
            addToZip(zos, "usuarios.csv", usuarioExportService.exportUsuariosToCSV());
            log.warn("obteniendo csv administradores");
            addToZip(zos, "administradores.csv", administradorExportService.exportAdministradoresToCSV());

            // Finalizar el ZIP
            zos.finish();
            byte[] zipBytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(zipBytes);

            // Configurar la respuesta
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"data_export.zip\"")
                    .body(resource);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addToZip(ZipOutputStream zos, String fileName, Resource resource) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        zos.write(resource.getInputStream().readAllBytes());
        zos.closeEntry();
    }




}
