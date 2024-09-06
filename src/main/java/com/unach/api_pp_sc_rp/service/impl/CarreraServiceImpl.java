package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.CarreraDTO;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.CarreraMapper;
import com.unach.api_pp_sc_rp.model.Carrera;
import com.unach.api_pp_sc_rp.repository.CarreraRepository;
import com.unach.api_pp_sc_rp.service.CarreraService;
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

public class CarreraServiceImpl implements CarreraService {

    private static final Logger logger = LoggerFactory.getLogger(CarreraServiceImpl.class);

    @Autowired
    private CarreraRepository carreraRepo;
    @Autowired
    private CarreraMapper carreraMapper;
    @Autowired
    private ExportService exportService;

    @Override
    public CarreraDTO saveCarrera(CarreraDTO carrera) {
        //no haya nombre de carreras repetidas
        if (carreraRepo.existsByNombre(carrera.getNombre())){
            logger.error("Username already exists: {}", carrera.getNombre());
            throw new IllegalArgumentException("El nombre de la carrera ya existe , elige otro ");
        }
        if (carrera == null){
            throw new IllegalArgumentException("No se ha proporcionado datos par la nueva carrera");
        }else if (carrera.getNombre().isEmpty()){
            throw new IllegalArgumentException("El nombre de la carrera no puede estar vacio");
        }
        Carrera newCarrera = carreraMapper.toEntity(carrera);

        return carreraMapper.toDTO(carreraRepo.save(newCarrera));

    }

    @Override
    public Optional<CarreraDTO> findByIdCarrera(Long id) {
        return carreraRepo.findById(id)
                .map(carreraMapper::toDTO)
                .or(() -> {
                    throw  new EntityNotFoundException("Carrera no encontrada con ID: "+id);
                })
                ;
    }

    @Override
    public List<CarreraDTO> findAllCArreraas() {
        List<Carrera> carreras = carreraRepo.findAll();
        if (carreras.isEmpty()){
            throw new EntityNotFoundException("No se encontraron estudiantes en el repositorio");
        }

        return carreras
                .stream()
                .map(carreraMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public CarreraDTO updateCarrera(Long id, Map<String, Object> updates){
        Carrera carreraFounded = carreraRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada en el repositorio"));

        updates.forEach((key, value) -> {
            if (value == null){
                throw new IllegalArgumentException("El valor para  [ "+key+" ] no puede estar vacio");
            }

            updateField(carreraFounded, key, value);
        });

        return carreraMapper.toDTO(carreraRepo.save(carreraFounded));

    }

    @Override
    public void deleteByIdCarrera(Long id) {

        if(!this.findByIdCarrera(id).isPresent()){
            throw new EntityNotFoundException("No se pudo eliminar la carrera por que no se encontro");
        }
        carreraRepo.deleteById(id);

    }

    //metod aux
    private void updateField(Carrera carrera, String key, Object value){

        switch (key){
            case "nombre":
                carrera.setNombre((String) value);
                break;
            default:
                throw new RuntimeException("Campo dedsconocido par la actualizacion: "+key);
        }
    }

    @Override
    public Resource exportCarrerasToCSV() {
        List<CarreraDTO> actividadesContables = findAllCArreraas();
        if (actividadesContables.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres");
        List<List<String>> data = actividadesContables.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombre().toString()
                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportCarrerasToPDF() {
        List<CarreraDTO> actividadesContables = findAllCArreraas();
        if (actividadesContables.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres");
        List<List<String>> data = actividadesContables.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombre().toString()
                ))
                .collect(Collectors.toList());

        String title = "Reporte de Carreras";
        return exportService.exportToPDF(title, headers, data);
    }
}
