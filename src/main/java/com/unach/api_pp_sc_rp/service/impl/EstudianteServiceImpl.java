package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.EstudianteDTO;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.EstudianteMapper;
import com.unach.api_pp_sc_rp.model.Carrera;
import com.unach.api_pp_sc_rp.model.Estudiante;
import com.unach.api_pp_sc_rp.model.Programa;
import com.unach.api_pp_sc_rp.model.enums.Grupo;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import com.unach.api_pp_sc_rp.repository.CarreraRepository;
import com.unach.api_pp_sc_rp.repository.EstudianteRepository;
import com.unach.api_pp_sc_rp.repository.ProgramaRepository;
import com.unach.api_pp_sc_rp.repository.UsuarioRepository;
import com.unach.api_pp_sc_rp.service.EstudianteService;
import com.unach.api_pp_sc_rp.service.ProgramaService;
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
public class EstudianteServiceImpl implements EstudianteService {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteServiceImpl.class);

    @Autowired
    private EstudianteRepository estudianteRepo;

    @Autowired
    private CarreraRepository carreraRepo;

    @Autowired
    private EstudianteMapper estudianteMapper;
    @Autowired
    private ExportService exportService;
    @Autowired
    private ProgramaRepository programaRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;


    @Override
    public EstudianteDTO saveEstudiante(EstudianteDTO estudiante) {

        if (estudiante == null) {
            throw new IllegalArgumentException("No se ha proporcionado datos para el nuevo estudiante");
        }

        if (estudiante.getMatricula() == null || estudiante.getMatricula().isEmpty()) {
            throw new IllegalArgumentException("La matricula del Alumno no puede estar vacía");
        } else if (estudiante.getCorreoInstitucional() == null || estudiante.getCorreoInstitucional().isEmpty()) {
            throw new IllegalArgumentException("El Correo Electrónico del Alumno no puede estar vacío");
        } else if (estudiante.getNombre() == null || estudiante.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del Alumno no puede estar vacío");
        } else if (estudiante.getApellido() == null || estudiante.getApellido().isEmpty()) {
            throw new IllegalArgumentException("El apellido del Alumno no puede estar vacío");
        } else if (estudiante.getSemestre() == null) {
            throw new IllegalArgumentException("El semestre del alumno no puede estar vacío");
        }
        //validar grupo
        String grupoString = estudiante.getGrupo().toUpperCase();
        if (grupoString.isEmpty()) {
            throw new IllegalArgumentException("El grupo del Alumno no puede estar vacío");
        }
        try {
            Grupo grupo = Grupo.valueOf(grupoString.trim());
            estudiante.setGrupo(grupo.name());
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El valor para [grupo] debe ser [A,B,C,D,....ect]");
        }



        String sexoString = estudiante.getSexo().toUpperCase();
        if (sexoString.isEmpty()) {
            throw new IllegalArgumentException("El sexo del Alumno no puede estar vacío");
        }
        try {
            Sexo sexo = Sexo.valueOf(sexoString.trim());
            estudiante.setSexo(sexo.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El valor para [sexo] debe ser [MASCULINO, FEMENINO, FENOMENO]");
        }

        // Validar el número de teléfono
        String telefono = estudiante.getTelefono();
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
        estudiante.setTelefono(telefonoLimpio);

        if (estudiante.getIdCarrera() == null) {
            throw new IllegalArgumentException("La carrera del Alumno no puede estar vacía");
        }

        // Validar que el correo sea único
        if (estudianteRepo.existsByCorreoInstitucional(estudiante.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado, elige otro");
        }

        Long idCarrera = estudiante.getIdCarrera();
        Carrera carreraFounded = carreraRepo.findById(idCarrera)
                .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada con ID: " + estudiante.getIdCarrera()));

        // Validación del semestre, rango de 1-9
        if (estudiante.getSemestre() < 1 || estudiante.getSemestre() > 9) {
            throw new IllegalArgumentException("El semestre debe estar entre 1 y 9");
        }

// Convertir DTO a entidad y establecer id_usuario como null (por defecto)

        Estudiante newEstudiante = estudianteMapper.toEntity(estudiante);
        newEstudiante.setCarrera(carreraFounded);
        newEstudiante.setUsuario(null);

        return estudianteMapper.toDTO(estudianteRepo.save(newEstudiante));
    }

    @Override
    public Optional<EstudianteDTO> findByIdEstudiante(Long id) {

        return estudianteRepo.findById(id)
                .map(estudianteMapper::toDTO)
                .or(() -> {
                    throw new EntityNotFoundException("Estudiante no encontrado con ID: " + id);
                });

    }
    @Override
    public EstudianteDTO findByMatricula(String matricula) {

        return estudianteMapper.toDTO(
                estudianteRepo.findByMatricula(matricula).orElseThrow(
                        () -> new EntityNotFoundException("Estudiante no encontrado conmatricula: "+matricula)
                )
        );

    }

    @Override
    public List<EstudianteDTO> findAllEstudiantes() {
        List<Estudiante> estudiantes = estudianteRepo.findAll();
        if (estudiantes.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron estudiantes en el repositorio");
        }
        return estudiantes
                .stream()
                .map(estudianteMapper::toDTO)
                .toList();
    }


    @Override
    public void deleteEstudiante(Long id) {

        Optional<Estudiante> estudianteOpt = estudianteRepo.findById(id);

        if (!estudianteOpt.isPresent()) {
            logger.warn("Buscando Estudiante antes de arrojar la exception NotFound");
            throw new EntityNotFoundException("No se pudo eliminar el estudiante porque no fue encontrado");
        }
        logger.warn("Verificando existencias del estudiante en tabla Programas");
        List<Programa> programas = programaRepo.findByEstudianteId(id);

        if (!programas.isEmpty()) {
            logger.warn("Verificando Programas ligados al estudiante antes de eliminarlo y de arrojar la exception BadRequest");
            throw new IllegalArgumentException("No se pudo eliminar el estudiante. \n Causa: Debes eliminar los programas ligados a este estudiante para poder eliminarlo...");
        }
        Estudiante estudiante = estudianteOpt.get();

        logger.warn("Verificando existencias del estudiante en tabla Usuario");

        if (estudiante.getUsuario() != null) {
            logger.warn("Verificando si aun es Usuario antes de eliminarlo y de arrojar la exception NotFound");
            throw new IllegalArgumentException("No se pudo eliminar el Estudiante. \n Causa: El estudiante es un Usuario del sistema. Debes eliminar al usuario asociado al Estudiante para poder eliminar al estudiante.");
        }



        estudianteRepo.deleteById(id);

    }

    @Transactional
    @Override
    public EstudianteDTO updateEstudiante(Long id, Map<String, Object> updates) {
        Estudiante estudianteFounded = estudianteRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado en el repositorio"));

        updates.forEach((key, value) -> {
            if (value == null) {
                throw new IllegalArgumentException("El valor para [ " + key + " ] no puede estar vacío");
            }
            updateField(estudianteFounded, key, value);
        });


        // Guardar el estudiante actualizado y convertirlo a DTO
        return estudianteMapper.toDTO(estudianteRepo.save(estudianteFounded));
    }

    @Override
    public Resource exportEstudiantesToCSV() {
        List<EstudianteDTO> estudiantes = findAllEstudiantes();
        if (estudiantes.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres");
        List<List<String>> data = estudiantes.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getMatricula(),
                        actividad.getNombre().toString(),
                        actividad.getApellido(),
                        actividad.getSexo(),
                        actividad.getGrupo(),
                        actividad.getTelefono(),
                        actividad.getCorreoInstitucional(),
                        actividad.getSemestre().toString(),
                        // Verificar si IdCarrera y IdUsuario es null, devolver "No asignado"
                        actividad.getIdCarrera() != null ? actividad.getIdCarrera().toString() : "No asignado",
                        actividad.getIdUsuario() != null ? actividad.getIdUsuario().toString() : "No asignado"
                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportEstudiantesToPDF() {
        List<EstudianteDTO> estudiantes = findAllEstudiantes();
        if (estudiantes.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Nombres");
        List<List<String>> data = estudiantes.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getMatricula(),
                        actividad.getNombre().toString(),
                        actividad.getApellido(),
                        actividad.getSexo(),
                        actividad.getGrupo(),
                        actividad.getTelefono(),
                        actividad.getCorreoInstitucional(),
                        actividad.getSemestre().toString(),
                        actividad.getIdCarrera().toString(),
                        actividad.getIdUsuario().toString()
                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar el PDF.");
        }
        String title = "Reporte de Estudiantes";
        return exportService.exportToPDF(title, headers, data);
    }

//method aux
    private void updateField(Estudiante estudiante, String key, Object value) {
        switch (key) {
            case "nombre":
                if (value instanceof String) {
                    estudiante.setNombre((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [nombre] debe ser una cadena de texto");
                }
                break;
            case "apellido":
                if (value instanceof String) {
                    estudiante.setApellido((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [apellido] debe ser una cadena de texto");
                }
                break;

            case "matricula":
                if (value instanceof String){
                    estudiante.setMatricula((String) value);
                }else {
                    throw new IllegalArgumentException("El cmapao para [matricula] debe ser una cadena de texto");
                }
                break;
            case "correoInstitucional":
                if (value instanceof String) {

                    estudiante.setCorreoInstitucional((String) value);
                } else {
                    throw new IllegalArgumentException("El valor para [correo_institucional] debe ser una cadena de texto");
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

                    estudiante.setTelefono(telefonoLimpio);
                } else {
                    throw new IllegalArgumentException("El valor para [telefono] debe ser una cadena de texto.");
                }
                break;
            case "sexo":
                if (value instanceof String) {
                    try {
                        Sexo sexo = Sexo.valueOf(
                                ((String) value).toUpperCase()
                        );
                        estudiante.setSexo(sexo);
                    }catch (IllegalArgumentException ex){
                        throw new IllegalArgumentException("El valor para sexo solo acepta valores validos (MASCULINO, FEMENINO, FENOMENO) ");
                    }
                } else {
                    throw new IllegalArgumentException("El valor para [sexo] debe ser [FEMENINO, MASCULINO. FENOMENO].");
                }
                break;
            case "semestre":
                if (value instanceof Integer) {

                    Integer semestre = (Integer) value;
                    if (semestre < 1 || semestre > 9) {
                        throw new IllegalArgumentException("El semestre debe estar entre 1 y 9");
                    }
                    estudiante.setSemestre(semestre);
                } else {
                    throw new IllegalArgumentException("El valor para [semestre] debe ser un número entero");
                }
                break;
            case "grupo":
                if (value instanceof String) {
                    try {
                        Grupo grupo = Grupo.valueOf(
                                ((String) value).toUpperCase()
                        );
                        estudiante.setGrupo(grupo);
                    }catch (IllegalArgumentException ex){
                        throw new IllegalArgumentException("El valor para sexo solo acepta valores validos [A,B,C,..etc] ");
                    }
                } else {
                    throw new IllegalArgumentException("El valor para [grupo] debe ser una cadena de texto");
                }
                break;
            case "carrera":
                if (value instanceof Integer) {
                    Long idCarrera = ((Integer) value).longValue(); // Convierte Integer a Long
                    Carrera carreraUpdated = carreraRepo.findById(idCarrera)
                            .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada con ID: " + idCarrera));
                    estudiante.setCarrera(carreraUpdated);
                } else if (value instanceof Long) {
                    Long idCarrera = (Long) value;
                    Carrera carreraUpdated = carreraRepo.findById(idCarrera)
                            .orElseThrow(() -> new EntityNotFoundException("Carrera no encontrada con ID: " + idCarrera));
                    estudiante.setCarrera(carreraUpdated);
                } else {
                    System.out.println("Tipo recibido para [carrera]: " + value.getClass().getName());
                    throw new IllegalArgumentException("El valor para [carrera] debe ser un número entero");
                }
                break;
            // Agregar más casos según los campos que desees actualizar
            default:
                throw new RuntimeException("Campo desconocido para actualización: " + key);
        }
    }


}
