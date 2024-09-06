package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.TipoProgramaDTO;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.TipoProgramaMapper;
import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.model.TipoPrograma;
import com.unach.api_pp_sc_rp.repository.ProgramaRepository;
import com.unach.api_pp_sc_rp.repository.TipoProgramaRepository;
import com.unach.api_pp_sc_rp.service.TipoProgramaService;
import com.unach.api_pp_sc_rp.service.export.ExportService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TipoProgramaImpl implements TipoProgramaService {

    private static final Logger logger = LoggerFactory.getLogger(TipoProgramaImpl.class);
    @Autowired
    private TipoProgramaRepository tipoProgramaRepo;
    @Autowired
    TipoProgramaMapper tipoProgramaMapper;
    @Autowired
    private ExportService exportService;
    @Autowired
    private ProgramaRepository programaRepo;


    @Override
    public TipoProgramaDTO saveTipoPrograma(TipoProgramaDTO tipoPrograma) {
        if (tipoPrograma.getNombre() == null || tipoPrograma.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo del progrtama no puede estar vacía");
        }else if (tipoPrograma.getDescripcion() == null || tipoPrograma.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripcion del tipo del programa no puede estar vacía");
        }

        //verificar si se encuenttra registrado tipoo de programa
        if (tipoProgramaRepo.existsByNombre(tipoPrograma.getNombre())){
            throw  new IllegalArgumentException("Ya existe un Tipo de programma con el nombre [ "+tipoPrograma.getNombre()+ " ] ,Elige otro nombre");
        }

        TipoPrograma newTipoPrograma = tipoProgramaMapper.toEntity(tipoPrograma);
        return tipoProgramaMapper.toDTO(tipoProgramaRepo.save(newTipoPrograma));

    }

    @Override
    public Optional<TipoProgramaDTO> findByNombreTipoPrograma(String nombre) {

        return tipoProgramaRepo.findByNombre(nombre)
                .map(tipoProgramaMapper::toDTO)
                .or( () -> {
                   throw new EntityNotFoundException("Tipo programma no encontrado enel repositorio");
                });
    }

    @Override
    public Optional<TipoProgramaDTO> findByIdTipoPrograma(Long id) {
        return tipoProgramaRepo.findById(id)
                .map(tipoProgramaMapper::toDTO)
                .or(() -> {
                    throw new EntityNotFoundException("Tipo Programa no encontrado con ID: "+id);
                });
    }

    @Override
    public List<TipoProgramaDTO> findAllTipoPrograma() {
        List<TipoPrograma> tipoProgramas = tipoProgramaRepo.findAll();
        if (tipoProgramas.isEmpty()){
            throw new EntityNotFoundException("No se encontraron tipos de Programa en el repositorio");
        }
        return tipoProgramas
                .stream()
                .map(tipoProgramaMapper::toDTO)
                .toList();
    }
    @Transactional
    @Override
    public TipoProgramaDTO updateTipoPrograma(Long id, Map<String, Object> updates){
        TipoPrograma tipoProgramaFounded= tipoProgramaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo Programa no encontrado en el repositorio"));

        updates.forEach((key, value) -> {

            if (value == null){
                throw new IllegalArgumentException("El valor para [ "+key+" ] no puede ser nulo ");
            }
            updateField(tipoProgramaFounded, key, value);
        });

        return tipoProgramaMapper.toDTO(tipoProgramaRepo.save(tipoProgramaFounded));
    }

    private void updateField(TipoPrograma tipoPrograma, String key, Object value) {
        switch (key){
            case "nombre":
                if (value instanceof String) {
                    tipoPrograma.setNombre((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [nombre] debe ser una cadena de texto");
                }
                break;
            case "descripcion":
                if (value instanceof String) {
                    tipoPrograma.setDescripcion((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [descripcion] debe ser una cadena de texto");
                }
                break;
            default: throw new RuntimeException("Campo desconocido para actualizacion: "+key);
        }
    }

    @Override
    public void deleteByIdTipoPrograma(Long id) {
        // Buscar el TipoPrograma a eliminar
        TipoPrograma tipoPrograma = tipoProgramaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se pudo eliminar el Tipo Programa no fue encontrado"));

        // Buscar los Programas asociados a este TipoPrograma
        List<Programa> programas = programaRepo.findByTipoProgramaId(id);

        // Si hay Programas asociados, lanzar una excepción
        if (!programas.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el Tipo Programa porque hay Programas asociados. Primero elimine los Programas asociados.");
        }

        // Si no hay Programas asociados, proceder a eliminar el TipoPrograma
        tipoProgramaRepo.delete(tipoPrograma);
    }

    @Override
    public Resource exportTipoProgramasToCSV() {
        List<TipoProgramaDTO> tProgramas = findAllTipoPrograma();
        if (tProgramas.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombre", "Descripcion");
        List<List<String>> data = tProgramas.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getDescripcion().toString()

                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportTipoProgramasToPDF() {
            List<TipoProgramaDTO> tProgramas = findAllTipoPrograma();
            if (tProgramas.isEmpty()){
                throw new EntityNotFoundException("No hay datos para exportar");
            }

            List<String> headers = List.of("ID", "Nombre", "Descripcion");
            List<List<String>> data = tProgramas.stream()
                    .map(actividad -> List.of(
                            actividad.getId().toString(),
                            actividad.getDescripcion().toString()

                    ))
                    .collect(Collectors.toList());
            if (data.isEmpty()) {
                logger.warn("No hay datos para exportar el PDF.");
            }

        String title = "Reporte de Tipo Programa";
        return exportService.exportToPDF(title, headers, data);
    }
}

