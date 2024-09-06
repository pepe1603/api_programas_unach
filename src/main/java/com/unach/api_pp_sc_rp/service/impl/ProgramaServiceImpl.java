package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.ProgramaDTO;
import com.unach.api_pp_sc_rp.events.ProgramaCreadoEvent;
import com.unach.api_pp_sc_rp.events.ProgramaEventEliminado;
import com.unach.api_pp_sc_rp.events.ProgramaEventUpdate;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.ProgramaMapper;
import com.unach.api_pp_sc_rp.model.*;
import com.unach.api_pp_sc_rp.model.enums.EstadoAvance;
import com.unach.api_pp_sc_rp.repository.*;
import com.unach.api_pp_sc_rp.service.FileService;
import com.unach.api_pp_sc_rp.service.ProgramaService;
import com.unach.api_pp_sc_rp.service.export.ExportService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.Resource;

import java.util.stream.Collectors;

@Service
public class ProgramaServiceImpl implements ProgramaService {
    private static final Logger logger = LoggerFactory.getLogger(ProgramaServiceImpl.class);

    @Autowired
    private ProgramaMapper programaMapper;
    @Autowired
    private ProgramaRepository programaRepo;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private TipoProgramaRepository tipoProgramaRepository;
    @Autowired
    private ExportService exportService;

    @Autowired
    private FileService fileService;
    @Autowired
    ArchivoRepository archivoRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;



    @Override
    public ProgramaDTO savePrograma(ProgramaDTO programaDTO) {
        if (programaDTO == null) {
            throw new IllegalArgumentException("No se ha proporcionado datos para el nuevo programa");
        }
        logger.info("Recibido DTO para guardar: {}", programaDTO);

        if (programaDTO.getTituloProyecto() == null || programaDTO.getTituloProyecto().isEmpty()) {
            throw new IllegalArgumentException("El Titulo del proyecto no puede estar vacío");
        } else if (programaDTO.getDescripcionProyecto() == null || programaDTO.getDescripcionProyecto().isEmpty()) {
            throw new IllegalArgumentException("La descripción del programa no puede estar vacío");
        } else if (programaDTO.getFechaInicio() == null) {
            throw new IllegalArgumentException("La Fecha de inicio del programa no puede estar vacío");
        } else if (programaDTO.getFechaFin() == null) {
            throw new IllegalArgumentException("La fecha de Finalización del programa no puede estar vacío");
        } else if (programaDTO.getTotalHoras() == null) {
            throw new IllegalArgumentException("El número de horas para el programa no puede estar vacío");
        } else if (programaDTO.getIdTipoPrograma() == null) {
            throw new IllegalArgumentException("El Tipo de programa no puede estar vacío");
        } else if (programaDTO.getIdEstudiante() == null) {
            throw new IllegalArgumentException("El estudiante para el programa no puede estar vacío");
        } else if (programaDTO.getIdEmpresa() == null) {
            throw new IllegalArgumentException("La empresa para el programa no puede estar vacía");
        }



        // Si no se proporciona un estado, se asume que es PENDIENTE
        EstadoAvance estadoAvance;
        if (programaDTO.getEstadoAvance() == null || programaDTO.getEstadoAvance().isEmpty()) {
            estadoAvance = EstadoAvance.PENDIENTE;
        } else {
            try {
                estadoAvance = EstadoAvance.valueOf(programaDTO.getEstadoAvance().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("El valor del estado avance debe ser [PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO]");
            }
        }
        programaDTO.setEstadoAvance(estadoAvance.name());

        // Obtención de entidades relacionadas
        TipoPrograma tipoProgramaFounded = tipoProgramaRepository.findById(programaDTO.getIdTipoPrograma())
                .orElseThrow(() -> new EntityNotFoundException("Tipo Programa no encontrado con ID: " + programaDTO.getIdTipoPrograma()));


        Estudiante estudianteFounded = estudianteRepository.findById(programaDTO.getIdEstudiante())
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + programaDTO.getIdEstudiante()));

        Empresa empresaFounded = empresaRepository.findById(programaDTO.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con ID: " + programaDTO.getIdEmpresa()));

        // Mapeo de DTO a entidad
        Programa newPrograma = programaMapper.toEntity(programaDTO);
        newPrograma.setTipoPrograma(tipoProgramaFounded);
        newPrograma.setFinalizado(false); // Campo finalizado false por defecto
        newPrograma.setEstudiante(estudianteFounded);
        newPrograma.setEmpresa(empresaFounded);
        logger.info("Entidades mapeadas: {}", newPrograma);
        // Verificación y logging de fechas antes de guardar
        logger.info("\nFecha Inicio: {}", newPrograma.getFecha_inicio());
        logger.info("\nFecha Fin: {}", newPrograma.getFecha_fin());
        // Guardar y retornar el nuevo programa

        ProgramaDTO savedPrograma = programaMapper.toDTO(
                programaRepo.save(newPrograma)
        );

        eventPublisher.publishEvent(new ProgramaCreadoEvent(this, programaDTO.getTituloProyecto()));

        return savedPrograma;
    }

    @Override
    public Optional<ProgramaDTO> findByIdPrograma(Long id) {
        return programaRepo.findById(id)
                .map(programaMapper::toDTO)
                .or(() -> {
                    throw new EntityNotFoundException("Programa no encontrado con ID: "+id);
                });
    }

    @Override
    public List<ProgramaDTO> findAllProgramas() {
        List<Programa> programas = programaRepo.findAll();
        if (programas.isEmpty()){
            throw new EntityNotFoundException("No se encontraron programas en el repositorio");
        }
        return programas
                .stream()
                .map(programaMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProgramaDTO> findAllByEstadoAvance(String estado){
        try {
            EstadoAvance estadoAvance = EstadoAvance.valueOf(estado.trim().toUpperCase());

            List<ProgramaDTO> programas = this.programaRepo.findALlByEstadoAvance(estadoAvance)
                    .stream().map(programaMapper::toDTO)
                    .toList();

            if (programas.isEmpty()){
                throw new EntityNotFoundException("NO hay progrmagas ene le repositorio");
            }
            return programas;
         }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("LE estado Avance debe ser [PENDIENTE, EN_PROGRESO, COMPLETAOD,...etc]");
        }
    }
    @Transactional
    @Override
    public void updateProgramaAsFinilizado(Long id) {
        Programa programaFounded = programaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Programa no encontrado con ID: " + id));

        if (programaFounded.getEstadoAvance() == EstadoAvance.EN_PROGRESO) {
            programaFounded.setFinalizado(true);
            programaFounded.setEstadoAvance(EstadoAvance.COMPLETADO);
            programaRepo.save(programaFounded);
        } else if (programaFounded.getEstadoAvance() == EstadoAvance.COMPLETADO) {
            throw new IllegalArgumentException("El programa ya ha sido marcado como COMPLETADO");
        } else {
            throw new IllegalArgumentException("El programa no está en EN_PROGRESO y no puede marcarse como COMPLETADO");
        }

        eventPublisher.publishEvent(new ProgramaEventUpdate(this,
                programaFounded.getTituloProyecto()+" \n \nel programa se ha marcado como [ FINALIZADO ] "
        ));

    }

    @Transactional
    @Override
    public void updateEstadoPrograma(Long id, String estadoString) {
        // Recuperar el programa por ID
        Programa programaFounded = programaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El programa no se encuentra en el repositorio"));

        // Convertir el estado proporcionado en un enum
        EstadoAvance nuevoEstado;
        try {
            nuevoEstado = EstadoAvance.valueOf(estadoString.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("El valor del estado debe ser [PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO]");
        }

        // Verificar y actualizar el estado según la transición permitida

        switch (programaFounded.getEstadoAvance()) {
            case PENDIENTE:
                if (nuevoEstado == EstadoAvance.EN_PROGRESO) {
                    programaFounded.setEstadoAvance(nuevoEstado);
                } else if (nuevoEstado == EstadoAvance.CANCELADO) {
                    programaFounded.setEstadoAvance(nuevoEstado);
                    programaFounded.setFinalizado(false); // No se marca como finalizado
                } else {
                    throw new IllegalArgumentException("El programa está en estado PENDIENTE y solo puede ser marcado como EN_PROGRESO o CANCELADO");
                }
                break;

            case EN_PROGRESO:
                if (nuevoEstado == EstadoAvance.COMPLETADO) {
                    programaFounded.setEstadoAvance(nuevoEstado);
                    programaFounded.setFinalizado(true); // Marcar como finalizado
                } else if (nuevoEstado == EstadoAvance.CANCELADO) {
                    programaFounded.setEstadoAvance(nuevoEstado);
                    programaFounded.setFinalizado(false); // No se marca como finalizado
                } else {
                    throw new IllegalArgumentException("El programa está en estado EN_PROGRESO y solo puede ser marcado como COMPLETADO o CANCELADO");
                }
                break;

            case COMPLETADO:
                throw new IllegalArgumentException("El programa ya está en estado COMPLETADO y no puede cambiarse a otro estado");

            case CANCELADO:
                throw new IllegalArgumentException("El programa está en estado CANCELADO y no puede cambiarse a otro estado");

            default:
                throw new RuntimeException("Estado desconocido para el programa: " + programaFounded.getEstadoAvance());
        }


        // Guardar los cambios en el repositorio
        programaRepo.save(programaFounded);

        eventPublisher.publishEvent(new ProgramaEventUpdate(this,
                programaFounded.getTituloProyecto()+" [ ESTADO AVANCE ] "
        ));

    }



    @Override
    public ProgramaDTO updatePrograma(Long id, Map<String, Object> updates) {
        Programa programaFounded = programaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Programa no encontrado en el repositorio"));

        updates.forEach((key, value) ->  {
            if (value == null){
                throw new IllegalArgumentException("El valor para [ "+key+" ] no puede ser nulo");
            }
            updateField( programaFounded, key, value);


        });
        //guadar
        ProgramaDTO savedPrograma = programaMapper.toDTO(programaRepo.save(programaFounded));
        eventPublisher.publishEvent(new ProgramaEventUpdate(this,savedPrograma.getTituloProyecto() +"\n\n." +
                "Cambios realizados: \nDedcripcion del prorgama" +savedPrograma.getDescripcionProyecto()+
                "\nFechaInicio: "+savedPrograma.getFechaInicio()+
                "\nFecha Inicio: "+savedPrograma+
                "\n Mas Detalles revisa los cambios en la pataforma..."));
        return savedPrograma;


    }
    private void updateField(Programa programa, String key, Object value) {
        switch (key) {
            case "tituloProyecto":
                programa.setTituloProyecto((String) value);
                break;
            case "descripcionProyecto":
                programa.setDescripcionProyecto((String) value);
                break;
            case "fechaInicio":
                if (value instanceof String) {
                    programa.setFecha_inicio(LocalDateTime.parse((String) value));
                } else {
                    throw new IllegalArgumentException("El valor para [fechaInicio] debe ser una cadena de texto en formato ISO 8601");
                }
                break;
            case "fechaFin":
                if (value instanceof String) {
                    programa.setFecha_fin(LocalDateTime.parse((String) value));
                } else {
                    throw new IllegalArgumentException("El valor para [fechaFin] debe ser una cadena de texto en formato ISO 8601");
                }
                break;
            case "totalHoras":
                if (value instanceof Integer) {
                    programa.setTotalHoras((Integer) value);
                } else {
                    throw new IllegalArgumentException("El valor para [totalHoras] debe ser un número entero");
                }
                break;
            case "estudiante":
                if (value instanceof Integer) {
                    Long idEstudiante = ((Integer) value).longValue();//conversion a long
                    Estudiante estudianteUpdated = estudianteRepository.findById(idEstudiante)
                            .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado por EstudianteService"));
                    programa.setEstudiante(estudianteUpdated);
                } else if (value instanceof Long) {
                    Estudiante estudianteUpdated = estudianteRepository.findById((Long) value)
                            .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado por EstudianteService"));
                    programa.setEstudiante(estudianteUpdated);
                } else {
                    throw new IllegalArgumentException("El valor para [estudiante] debe ser un número entero");
                }
                break;
            case "empresa":
                if (value instanceof Integer) {
                    Long idEmpresa = ((Integer) value).longValue();
                    Empresa empresaUpdated = empresaRepository.findById(idEmpresa)
                            .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada por EmpresaService"));
                    programa.setEmpresa(empresaUpdated);
                }else if (value instanceof Long) {
                    Empresa empresaUpdated = empresaRepository.findById((Long) value)
                            .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada por EmpresaService"));
                    programa.setEmpresa(empresaUpdated);
                }
                else {
                    throw new IllegalArgumentException("El valor para [empresa] debe ser un número entero");
                }
                break;
            case "tipoPrograma":
                if (value instanceof Integer) {
                    Long idTprograma = ((Integer) value).longValue();
                    TipoPrograma tipoProgramaUpdated = tipoProgramaRepository.findById(idTprograma)
                            .orElseThrow(() -> new EntityNotFoundException("Tipo Programa no encontrado por TipoProgramaService"));
                    programa.setTipoPrograma(tipoProgramaUpdated);
                }else
                if (value instanceof Long) {
                    TipoPrograma tipoProgramaUpdated = tipoProgramaRepository.findById((Long) value)
                            .orElseThrow(() -> new EntityNotFoundException("Tipo Programa no encontrado por TipoProgramaService"));
                    programa.setTipoPrograma(tipoProgramaUpdated);
                } else {
                    throw new IllegalArgumentException("El valor para [tipoPrograma] debe ser un número entero");
                }
                break;
            default:
                throw new RuntimeException("Campo desconocido para actualización: " + key);
        }
    }



    @Override
    public void deletePrograma(Long id) {
        Programa programa = programaRepo.findById(id).orElseThrow( () ->
            new EntityNotFoundException("No se pudo eliminar el programa porque no fue encontrado")
        );

        //obener todos losa rchivos asociados a ese prgorama
        List<Archivo> archivos = archivoRepository.findByProgramaId(programa.getId());

        logger.info("Lista def archivos Encontrado: {}", archivos);

        //Eliminar ficiamente acada archivos  en la carpeta uploads
        for (Archivo archivo : archivos){
            fileService.delete(archivo.getNombreArchivo());
            logger.info("\nArchivo elimnado: {}", archivo.getNombreArchivo());
        }
        //elimniar losa rchivos de la base de datos
        archivoRepository.deleteAll(archivos);
        //finalm,ente , elimnaiiar el programa
        programaRepo.deleteById(id);

        eventPublisher.publishEvent(new ProgramaEventEliminado(this,
                programa.getTituloProyecto()
        ));

    }
    @Override
    public Resource exportProgramasToCSV() {
        List<ProgramaDTO> programas = findAllProgramas();
        if (programas.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "TituloProyecto", "DescripcionProyecto", "FechaInicio", "FechaFin", "EstadoAvance", "TotalHoras", "MatriculaEstudiante","Emproesa", "Id-TipoProgramaAsociado");
        List<List<String>> data = programas.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getTituloProyecto().toString(),
                        actividad.getDescripcionProyecto(),
                        actividad.getFechaInicio().toString(),
                        actividad.getFechaFin().toString(),
                        actividad.getEstadoAvance().toString(),
                        actividad.getTotalHoras().toString(),
                        // Verificar si IdEstudiante es null, devolver "No asignado" o "Eliminado"
                        actividad.getIdEstudiante() != null ? actividad.getIdEstudiante().toString() : "No asignado",
                        // Verificar si IdEmpresa yIdTipoPrograma es null, devolver "No asignado" o "Eliminado"
                        actividad.getIdEmpresa() != null ? actividad.getIdEmpresa().toString() : "No asignado",
                        actividad.getIdTipoPrograma() != null ? actividad.getIdTipoPrograma().toString() : "No asignado"

                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportProgramasToPDF() {
        List<ProgramaDTO> programas = findAllProgramas();
        if (programas.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "TituloProyecto", "DescripcionProyecto", "FechaInicio", "FechaFin", "EstadoAvance", "TotalHoras", "MatriculaEstudiante","Emproesa", "Id-TipoProgramaAsociado");
        List<List<String>> data = programas.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getTituloProyecto().toString(),
                        actividad.getDescripcionProyecto(),
                        actividad.getFechaInicio().toString(),
                        actividad.getFechaFin().toString(),
                        actividad.getEstadoAvance().toString(),
                        actividad.getTotalHoras().toString(),
                        actividad.getIdEstudiante().toString(),
                        actividad.getIdEmpresa().toString(),
                        actividad.getIdTipoPrograma().toString()

                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al PDF.");
        }

        String title = "Reporte de Programas";
        return exportService.exportToPDF(title, headers, data);
    }
}
