package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.EmpresaDTO;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.EmpresaMapper;
import com.unach.api_pp_sc_rp.model.Empresa;
import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import com.unach.api_pp_sc_rp.repository.EmpresaRepository;
import com.unach.api_pp_sc_rp.repository.ProgramaRepository;
import com.unach.api_pp_sc_rp.service.EmpresaService;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EmpresaServiceImpl implements EmpresaService{
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private EmpresaMapper empresaMapper;
    @Autowired
    private EmpresaRepository empresaRepo;
    @Autowired
    private ProgramaRepository programaRepo;
    @Autowired
    private ExportService exportService;


    @Override
    public EmpresaDTO saveEmpresa(EmpresaDTO empresa) {
        if (empresa == null) {
            throw new IllegalArgumentException("No se ha proporcionado datos para el nuevo estudiante");
        }
        if (empresa.getNombre() == null || empresa.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacía");
        } else if (empresa.getDireccion() == null || empresa.getDireccion().isEmpty()) {
            throw new IllegalArgumentException("La direccion de la empresa no puede estar vacía");
        } else if (empresa.getCorreo() == null || empresa.getCorreo().isEmpty()) {
            throw new IllegalArgumentException("La direccion de la empresa no puede estar vacía");
        } else if (empresa.getNombreResponsable() == null || empresa.getNombreResponsable().isEmpty()) {
            throw new IllegalArgumentException("El nombre del responssable de la empresa no puede estar vacía");
        }
        else if (empresa.getPuestoResponsable() == null || empresa.getPuestoResponsable().isEmpty()) {
            throw new IllegalArgumentException("El puesto del responssable de la empresa no puede estar vacía");
        }
        else if (empresa.getApellidoResponsable() == null || empresa.getApellidoResponsable().isEmpty()) {
            throw new IllegalArgumentException("El apellidod del responssable de la empresa no puede estar vacía");
        }

        String sexoString = empresa.getSexoResponsable().toUpperCase();
        if (sexoString.isEmpty()) {
            throw new IllegalArgumentException("El sexo del responsable no puede estar vacío");
        }
        try {
            Sexo sexo = Sexo.valueOf(sexoString.trim());
            empresa.setSexoResponsable(sexo.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El valor para [sexo] debe ser [MASCULINO, FEMENINO, FENOMENO]");
        }

        // Validar el número de teléfono
        String telefono = empresa.getTelefono();
        if (telefono == null || telefono.isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono de la empresa no puede estar vacío");
        }

        // Crear un patrón para encontrar caracteres no numéricos
        Pattern pattern = Pattern.compile("\\D");
        Matcher matcher = pattern.matcher(telefono);

        // Reemplazar todos los caracteres no numéricos con una cadena vacía
        String telefonoLimpio = matcher.replaceAll("");

        // Verificar que el teléfono tenga exactamente 10 dígitos
        if (telefonoLimpio.length() != 10) {
            throw new IllegalArgumentException("El número de teléfono debe tener exactamente 10 dígitos.");
        }

        // Actualizar el número de teléfono en el DTO
        empresa.setTelefono(telefonoLimpio);


        Empresa newEmpresa = empresaMapper.toEntity(empresa);
        return empresaMapper.toDTO(empresaRepo.save(newEmpresa));
    }

    @Override
    public Optional<EmpresaDTO> findByIdEmpresa(Long id) {
        return empresaRepo.findById(id)
                .map(empresaMapper::toDTO)
                .or(() -> {
                    throw new EntityNotFoundException("Empresa no encontrada con ID: " + id);
                });
    }

    @Override
    public List<EmpresaDTO> findAllEmpresas() {
        List<Empresa> empresas = empresaRepo.findAll();
        if (empresas.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron empresas en el repositorio");
        }
        return empresaRepo.findAll()
                .stream()
                .map(empresaMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public EmpresaDTO updateEmpresa (Long id, Map<String, Object> updates) {
        Empresa empresaFounded = this.empresaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada en ele repositorio"));
        updates.forEach((key, value) -> {
            if (value == null) {
                throw new IllegalArgumentException("El campo para [ "+ key + " ] no puede ser nulo");
            }
            updateField(empresaFounded, key, value);
        });

        return empresaMapper.toDTO(empresaRepo.save(empresaFounded));
    }

    private void updateField(Empresa empresa, String key, Object value) {

        switch (key) {
            case "nombre":
                if (value instanceof String) {
                    empresa.setNombre((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [nombre] debe ser una cadena de texto");
                }
                break;
            case "telefono":
                if (value instanceof String) {
                    String telefono = (String) value;
                    // Crear un patrón para encontrar caracteres no numéricos
                    Pattern pattern = Pattern.compile("\\D");
                    Matcher matcher = pattern.matcher(telefono);

                    // Reemplazar todos los caracteres no numéricos con una cadena vacía
                    String telefonoLimpio = matcher.replaceAll("");

                    // Validar que el teléfono tenga exactamente 10 dígitos
                    if (!telefono.matches("\\d{10}")) {
                        throw new IllegalArgumentException("El número de teléfono de la empresa debe tener exactamente 10 dígitos.");
                    }

                    empresa.setTelefono(telefonoLimpio);
                } else {
                    throw new IllegalArgumentException("El valor para [telefono] debe ser una cadena de texto.");
                }
                break;
            case "correo":
                if (value instanceof String) {
                    empresa.setCorreo((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correo_institucional] debe ser una cadena de texto");
                }
                break;
            case "direccion":
                if (value instanceof String) {
                    empresa.setDireccion((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correo_institucional] debe ser una cadena de texto");
                }
                break;
            case "apellidoResponsable":
                if (value instanceof String) {
                    empresa.setApellidoResponsable((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correo_institucional] debe ser una cadena de texto");
                }
                break;
            case "nombreResponsable":
                if (value instanceof String) {
                    empresa.setNombreResponsable((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correo_institucional] debe ser una cadena de texto");
                }
                break;
            case "puestoResponsable":
                if (value instanceof String) {
                    empresa.setPuestoResponsable((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correo_institucional] debe ser una cadena de texto");
                }
                break;
            case "sexoResponsable":
                if (value instanceof String) {
                    try {
                        Sexo sexo = Sexo.valueOf(
                                ((String) value).toUpperCase()
                        );
                        empresa.setSexoResponsable(sexo);
                    }catch (IllegalArgumentException ex){
                        throw new IllegalArgumentException("El valor para sexo solo acepta valores validos (MASCULINO, FEMENINO, FENOMENO) ");
                    }
                } else {
                    throw new IllegalArgumentException("El valor para [sexo] debe ser [FEMENINO, MASCULINO. FENOMENO].");
                }
                break;
            default:
                throw new IllegalArgumentException("Campo desconocido para actualización: " + key);
        }
    }


    @Override
    public void deleteByIdEmpresa(Long id) {
        if (!this.findByIdEmpresa(id).isPresent()){
            throw new EntityNotFoundException("No se pudo eliminar la empresa porque no fue encontrado");
        }
logger.info("Verificando PRogramas asociados a la empresa..");
        List<Programa> programas = programaRepo.findByEmpresa_Id(id);

        for (Programa programa: programas){
            logger.warn("Eliminando Registros de las empresa: "+programa.getEmpresa().getNombre()+" a null para todos los Programas Asociados");
            programa.setEmpresa(null);
            programaRepo.save(programa);
        }

        empresaRepo.deleteById(id);


    }

    @Override
    public Resource exportEmpresasToCSV() {
        List<EmpresaDTO> empresas = findAllEmpresas();

        if (empresas.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres", "Direccion", "Correo", "Telefono", "NombreResponsable", "ApellidosResponsable", "SexoResponsable", "PuestoResponsable");
        List<List<String>> data = empresas.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombre().toString(),
                        actividad.getDireccion().toString(),
                        actividad.getCorreo().toString(),
                        actividad.getTelefono().toString(),
                        actividad.getNombreResponsable().toString(),
                        actividad.getApellidoResponsable().toString(),
                        actividad.getSexoResponsable().toString(),
                        actividad.getPuestoResponsable().toString()
                ))
                .collect(Collectors.toList());

        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportEmpresasToPDF() {
        List<EmpresaDTO> empresas = findAllEmpresas();

        if (empresas.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres", "Direccion", "Correo", "Telefono", "NombreResponsable", "ApellidosResponsable", "SexoResponsable", "PuestoResponsable");
        List<List<String>> data = empresas.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombre().toString(),
                        actividad.getDireccion().toString(),
                        actividad.getCorreo().toString(),
                        actividad.getTelefono().toString(),
                        actividad.getNombreResponsable().toString(),
                        actividad.getApellidoResponsable().toString(),
                        actividad.getSexoResponsable().toString(),
                        actividad.getPuestoResponsable().toString()
                ))
                .collect(Collectors.toList());

        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }
        String title = "Reporte de Empresas";
        return exportService.exportToPDF(title, headers, data);
    }

}
