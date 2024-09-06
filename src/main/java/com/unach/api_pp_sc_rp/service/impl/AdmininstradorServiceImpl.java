package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.AdministradorDTO;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.AdministradorMapper;
import com.unach.api_pp_sc_rp.model.Administrador;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import com.unach.api_pp_sc_rp.repository.AdminRepository;
import com.unach.api_pp_sc_rp.repository.UsuarioRepository;
import com.unach.api_pp_sc_rp.service.AdministradorService;
import com.unach.api_pp_sc_rp.service.export.ExportService;
import com.unach.api_pp_sc_rp.utils.GenerateUtils;
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
public class AdmininstradorServiceImpl implements AdministradorService {

    private static final Logger logger = LoggerFactory.getLogger(AdmininstradorServiceImpl.class);

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    UsuarioRepository usuarioRepo;
    @Autowired
    private AdministradorMapper adminMapper;

    @Autowired
    private GenerateUtils generateUtils;
    @Autowired
    private ExportService exportService;


    @Override
    public AdministradorDTO saveAdministrador(AdministradorDTO administrador) {

        //VERIFICAMOS EL IdAdmin no seas nuelo o vacio
        if (administrador.getIdAdmin() == null || administrador.getIdAdmin().isEmpty()){
            throw new IllegalArgumentException("El identiifacodr de administrador no puede estar vacio");
        }
        if (adminRepo.existsByCorreoInstitucional(administrador.getCorreoInstitucional())){
            throw new IllegalArgumentException("El correo electronico ya esta registrado, elige otro");
        }
        else if (administrador.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del Alumno no puede estar vacio");
        }
        else if (administrador.getApellido().isEmpty()) {
            throw new IllegalArgumentException("El apellido del Alumno no puede estar vacio");
        }


        // Validar el campo sexo y normalizarlo
        String sexoString = administrador.getSexo().toUpperCase();
        if (sexoString.isEmpty()) {
            throw new IllegalArgumentException("El sexo del administrador no puede estar vacío");
        }
        try {
            Sexo sexo = Sexo.valueOf(sexoString.trim());
                administrador.setSexo(sexo.name());

            } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El valor para [sexo] debe ser [ MASCULINO, FEMENINO, FENOMENO]");
        }

        String idAdminFormtaed = generateUtils.validarYFormatearIdentificador(administrador.getIdAdmin());
        // recontruimos el idAdmin con el prefijo "ID-"
        administrador.setIdAdmin(idAdminFormtaed);

        //aseguramos que no haya duplicados en el repo
        if (adminRepo.existsByIdAdmin(idAdminFormtaed)){
            throw new IllegalArgumentException("El identiifcador de administrador esta en uso, elige otro");
        }
        // Validar el número de teléfono
        String telefono = administrador.getTelefono();
        if (telefono == null || telefono.isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
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
        administrador.setTelefono(telefonoLimpio);


        //convertimos el DTO  la entidad Administrador y poner le idUsuario com null (default)
        Administrador newAdministrador = adminMapper.toEntity(administrador);
        newAdministrador.setUsuario(null);
        return  adminMapper.toDTO(adminRepo.save(newAdministrador));
    }
    @Override
    public AdministradorDTO findByIdentifyAdministrador(String ID_administrador){
        return adminMapper.toDTO(
          adminRepo.findByIdAdmin(ID_administrador)
                  .orElseThrow(
                          () -> new EntityNotFoundException("No se encontro considencias con el ID-ADMINISTRADOR :"+ ID_administrador)
                  )
        );
    }

    @Override
    public Optional<AdministradorDTO> findByIdAdministrador(Long id) {
        return adminRepo.findById(id)
                .map(adminMapper::toDTO)
                .or(() -> {
                    throw new EntityNotFoundException("Administrador no encontrado con ID: "+id);
                });
    }

    @Override
    public List<AdministradorDTO> findAllAdministradores() {
        List<Administrador> administradores = adminRepo.findAll();
        if (administradores.isEmpty()){
            throw new EntityNotFoundException("No se encontraron administradores en el repositorio");
        }
        return administradores
                .stream()
                .map(adminMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteAdministrador(Long id) {
        if(!this.findByIdAdministrador(id).isPresent()){
            throw new EntityNotFoundException("No se pudo eliminar el administrador por que no fue encontrado");
        }
        adminRepo.deleteById(id);

    }

    @Transactional
    @Override
    public AdministradorDTO updateAdministrador(Long id, Map<String, Object> updates) {
        Administrador administradorFounded = adminRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrador no encontrado en el repositorio"));

        updates.forEach((key, value) -> {

            if (value == null) {
                throw new IllegalArgumentException("El valor para [ " + key + " ] no puede ser nulo");
            }

            updateField(administradorFounded, key, value);
        });

        // Guardar el administrador actualizado y convertir a DTO
        return adminMapper.toDTO(adminRepo.save(administradorFounded));
    }

    @Override
    public Resource exportAdministradoresToCSV() {
        List<AdministradorDTO> actividadesContables = findAllAdministradores();

        if (actividadesContables.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres", "Apellidos", "Sexo", "Telefono", "CorreoInstitucional", "Clave-Administrador", "ID-Usuario");
        List<List<String>> data = actividadesContables.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombre().toString(),
                        actividad.getApellido().toString(),
                        actividad.getSexo().toString(),
                        actividad.getTelefono().toString(),
                        actividad.getCorreoInstitucional().toString(),
                        actividad.getIdAdmin().toString(),
                        // Verificar si IdUsuario es null, devolver "No asignado"
                        actividad.getIdUsuario() != null ? actividad.getIdUsuario().toString() : "No asignado",
                        actividad.getIdUsuario().toString()
                ))
                .collect(Collectors.toList());

        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportAdministradoresToPDF() {
        List<AdministradorDTO> actividadesContables = findAllAdministradores();
        if (actividadesContables.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres", "Apellidos", "Sexo", "Telefono", "CorreoInstitucional", "Clave-Administrador", "ID-Usuario");
        List<List<String>> data = actividadesContables.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getNombre().toString(),
                        actividad.getApellido().toString(),
                        actividad.getSexo().toString(),
                        actividad.getTelefono().toString(),
                        actividad.getCorreoInstitucional().toString(),
                        actividad.getIdAdmin().toString(),
                        actividad.getIdUsuario().toString()
                ))
                .collect(Collectors.toList());

        String title = "Reporte de Actividades Contables";
        return exportService.exportToPDF(title, headers, data);
    }
    //metohod aux
    private void updateField(Administrador administrador, String key, Object value){
        switch (key) {
            case "nombre":
                if (value instanceof String){
                    administrador.setNombre((String) value);
                }else {
                    throw new IllegalArgumentException("El valor para [nombre] debe ser una cadena de texto");
                }
                break;
            case "apellido":
                if (value instanceof String) {
                    administrador.setApellido((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [apellido] debe ser una cadena de texto");
                }
                break;
            case "idAdmin":
                if (value instanceof String) {
                    String idAdminFormated = generateUtils.validarYFormatearIdentificador((String) value);
                    // Verifica si el nuevo identificador ya está en uso
                    if (!administrador.getIdAdmin().equals(idAdminFormated) && adminRepo.existsByIdAdmin(idAdminFormated)) {
                        throw new IllegalArgumentException("El identificador de administrador ya está en uso.");
                    }

                    administrador.setIdAdmin((idAdminFormated));
                } else {
                    throw new IllegalArgumentException("El valor para [apellido] debe ser una cadena de texto");
                }
                break;
            case "correoInstitucional":
                if (value instanceof String) {
                    administrador.setCorreoInstitucional((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correoInstitucional] debe ser una cadena de texto");
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
                        throw new IllegalArgumentException("El número de teléfono debe tener exactamente 10 dígitos.");
                    }

                    administrador.setTelefono(telefonoLimpio);
                } else {
                    throw new IllegalArgumentException("El valor para [telefono] debe ser una cadena de texto.");
                }
                break;
            case "sexo":
                String sexoString = ((String) value).toUpperCase();
                if (sexoString instanceof String) {
                    try {
                        Sexo sexo = Sexo.valueOf(sexoString);
                        administrador.setSexo(sexo);
                    }catch (IllegalArgumentException ex){
                        throw new IllegalArgumentException("El valor para sexo solo acepta valores validos (MASCULINO, FENOMENO, OTRO) ");
                    }
                } else {
                    throw new IllegalArgumentException("El valor para [sexo] debe ser de tipo Sexo");
                }
                break;
            // Agregar más casos según los campos que desees actualizar
            default:
                throw new RuntimeException("Campo desconocido para actualización: " + key);
        }
    }

}
