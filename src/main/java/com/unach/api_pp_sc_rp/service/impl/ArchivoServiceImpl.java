package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.*;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.ArchivoMapper;
import com.unach.api_pp_sc_rp.model.Archivo;
import com.unach.api_pp_sc_rp.model.Estudiante;
import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.model.TipoPrograma;
import com.unach.api_pp_sc_rp.repository.ArchivoRepository;
import com.unach.api_pp_sc_rp.repository.EstudianteRepository;
import com.unach.api_pp_sc_rp.repository.ProgramaRepository;
import com.unach.api_pp_sc_rp.repository.TipoProgramaRepository;
import com.unach.api_pp_sc_rp.service.*;
import com.unach.api_pp_sc_rp.service.export.ExportService;
import com.unach.api_pp_sc_rp.utils.GenerateUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArchivoServiceImpl implements ArchivoService {

private static final Logger logger = LoggerFactory.getLogger(ArchivoServiceImpl.class);

    private final ArchivoRepository archivoRepo;

    private final ArchivoMapper archivoMapper;

    private final FileService fileService;

    private final ProgramaRepository programaRepository;

    private final EstudianteRepository estudianteRepository;

    private final TipoProgramaRepository tipoProgramaRepository;

    private final GenerateUtils generateUtils;

    private final ExportService exportService;

    @Override
    public ArchivoDTO saveArchivo(ArchivoRequestDTO archivoRequestDTO) {
        // Validar que se haya especificado un programa
        if (archivoRequestDTO.getIdPrograma() == null) {
            throw new IllegalArgumentException("Se requiere especificar el programa relacionado para el archivo");
        }


        // Obtener el programa desde el repositorio
        Long idPrograma = archivoRequestDTO.getIdPrograma();
        Programa programaFounded = programaRepository.findById(idPrograma)
                .orElseThrow(() -> new EntityNotFoundException("El programa con ID [ " + idPrograma + " ] no existe"));


        // Generar el nombre de archivo
        String nombreArchivoGenerado;
        if (archivoRequestDTO.getNombreArchivo() == null || archivoRequestDTO.getNombreArchivo().isEmpty()) {
            nombreArchivoGenerado = this.getNameFormat("", archivoRequestDTO);
        } else {
            nombreArchivoGenerado = archivoRequestDTO.getNombreArchivo();
        }

        // Generar un nombre único si es necesario
        String uniqueFilename = fileService.getUniqueFilename(nombreArchivoGenerado);

        // Guardar el archivo en el sistema de archivos
        fileService.save(archivoRequestDTO.getFile(), uniqueFilename);

        // Obtener la ruta del archivo guardado
        String rutaArchivo = fileService.getRutaArchivo(uniqueFilename);

        // Crear y guardar el DTO del archivo en la base de datos
        ArchivoDTO newArchivo = new ArchivoDTO();
        newArchivo.setNombreArchivo(uniqueFilename);
        newArchivo.setRutaArchivo(rutaArchivo);
        newArchivo.setIdPrograma(programaFounded.getId());

        //persistir archivo


        return archivoMapper.toDTO(archivoRepo.save(archivoMapper.toEntity(newArchivo)));
    }


    @Override
    public Optional<ArchivoDTO> findByIdArchivo(Long id) {
        return
                archivoRepo.findById(id)
                        .map(archivoMapper::toDTO)
                        .or(
                                () -> {
                                    throw new EntityNotFoundException("Archvio no encontrado con ID : "+id);
                                }
                        );
    }

    @Override
    public List<ArchivoDTO> findAllArchivos() {
        List<Archivo> archivos = archivoRepo.findAll();
        return archivos
                .stream()
                .map(archivoMapper::toDTO)
                .toList();
    }

    @Override
    public List<ArchivoDTO> findArchivoByProgramaId(Long programaId) {
        List<Archivo> archivosByProgramaId = archivoRepo.findByProgramaId(programaId);
        return archivosByProgramaId
                .stream()
                .map(archivoMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteByIdArchivo(Long id) {
        Optional<ArchivoDTO> archivoExist = this.findByIdArchivo(id);

        if (!archivoExist.isPresent()){
            throw new EntityNotFoundException("No se pudo eliminar el archivo por que no fue encontrado");
        }
        fileService.delete(archivoExist.get().getNombreArchivo());
        archivoRepo.deleteById(id);
        /*
        this.findByIdArchivo(id).ifPresent( archivo -> {
            fileService.delete(archivo.getNombreArchivo()
            archivoRepo.deleteById(id);
            );

        });
        */

    }
    @Transactional
    @Override
    public ArchivoDTO updateArchivo(Long id, ArchivoRequestDTO archivoRequestDTO) {
        Archivo archivoExistente = archivoRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Archivo no encontrado en el repositorio"));

        if (archivoRequestDTO.getFile() == null){
            throw new EntityNotFoundException("Se requiere un archivo");
        }

        String nuevoNombreArchivo = archivoRequestDTO.getNombreArchivo();
        MultipartFile nuevoArchivo = archivoRequestDTO.getFile();

        if (nuevoArchivo != null) {
            // Si se proporciona un nuevo archivo, lo guardamos y actualizamos la ruta en la base de datos
            String uniqueFilename = fileService.getUniqueFilename(nuevoArchivo.getOriginalFilename());
            fileService.save(nuevoArchivo, uniqueFilename);
            archivoExistente.setRutaArchivo(fileService.getRutaArchivo(uniqueFilename));
            archivoExistente.setNombreArchivo(uniqueFilename);
        } else if (nuevoNombreArchivo != null && !nuevoNombreArchivo.isEmpty()) {
            // Si solo se proporciona un nuevo nombre, actualizamos solo el nombre en la base de datos
            archivoExistente.setNombreArchivo(fileService.getUniqueFilename(nuevoNombreArchivo));
            // Aquí se puede agregar lógica para renombrar el archivo en el sistema de archivos si es necesario
            String viejoNombreArchivo = archivoExistente.getNombreArchivo();
            fileService.updateFile(viejoNombreArchivo, nuevoNombreArchivo); // Renombrar en el sistema de archivos
        }

        // Guardar cambios en la base de datos
        return archivoMapper.toDTO(archivoRepo.save(archivoExistente));
    }



    //- Metodos aux


    private String getNameFormat(String nombreArchivo, ArchivoRequestDTO archivoRequestDTO) {
        // Obtener las iniciales del usuario
        String initials = getInitialsOfUser(archivoRequestDTO.getIdPrograma());

        // Obtener la abreviatura del programa
        String programaAbbr = getProgramaAbbr(archivoRequestDTO.getIdPrograma());

        // Obtener y formatear la fecha actual para el nombre del archivo
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()); // Usar `new Date()` en lugar de `LocalDateTime.now()`

        // Obtener la extensión del archivo
        String fileExtension = getFileExtension(Objects.requireNonNull(archivoRequestDTO.getFile().getOriginalFilename()));

        // Generar el nombre del archivo
        nombreArchivo = programaAbbr + "-" + initials + "-" + timestamp + fileExtension;

        return nombreArchivo;
    }


    // Métodos adicionales que  implementa obtener  iniciales de usauro (estudiante):

    private String getInitialsOfUser(Long idPrograma){
        Programa programaFounded= programaRepository.findById (idPrograma)
                .orElseThrow(
                        () -> new EntityNotFoundException("Programa No encontrado por ProgramaService ")
                );

        Estudiante estudianteFounded = estudianteRepository.findById(
                programaFounded.getEstudiante().getId()
                )
                .orElseThrow( () -> new EntityNotFoundException("Estudiante  no encontrado por EstudanteService"));
        String firstname = generateUtils.generateAbbr(estudianteFounded.getNombre());
        String lastname = generateUtils.generateAbbr(estudianteFounded.getApellido());

        return  firstname +lastname;
    }

    //metodosd par aobtener el prtograma abreviado --ejemplo: PP1. PP2. SC
    private String getProgramaAbbr(Long idPrograma) {
        Programa programaFounded= programaRepository.findById(idPrograma)
                .orElseThrow(
                        () -> new EntityNotFoundException("Programa No encontrado por ProgramaService ")
                );
        //abreviamos el nombre del tipo rpgrama : ejemplo: practica profesional 1 -> PP1
        TipoPrograma tipoPrograma = tipoProgramaRepository.findById(
                programaFounded.getTipoPrograma().getId())
                .orElseThrow( () -> new EntityNotFoundException("Tipo  Programa no encontrado por TipoProgramaService")
        );

        String nameTipoPrograma = tipoPrograma.getNombre();
        return generateUtils.generateAbbr(nameTipoPrograma);
    }

    //Obtener Extension de la extension del archivo
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

    @Override
    public Resource exportArchivosToCSV() {
        List<ArchivoDTO> archivos = findAllArchivos();

        if (archivos.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar pdf");
        }

        List<String> headers = List.of("ID", "Nombre", "Ruta", "ID-ProgramaAsociado");
        List<List<String>> data = archivos.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombreArchivo().toString(),
                        actividad.getRutaArchivo().toString(),
                        // Verificar si IdPrograma es null, devolver "No asignado"
                        actividad.getIdPrograma() != null ? actividad.getIdPrograma().toString() : "No asignado"
                ))
                .collect(Collectors.toList());

        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportArchivosToPDF() {
        List<ArchivoDTO> archivos = findAllArchivos();

        if (archivos.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar el PDF");
        }

        List<String> headers = List.of("ID", "Nombre", "Ruta", "ID-ProgramaAsociado");
        List<List<String>> data = archivos.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombreArchivo().toString(),
                        actividad.getRutaArchivo().toString(),
                        actividad.getIdPrograma().toString()
                ))
                .collect(Collectors.toList());

        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar el PDF.");
        }

        String title = "Reporte de Archivos";
        return exportService.exportToPDF(title, headers, data);
    }



}
