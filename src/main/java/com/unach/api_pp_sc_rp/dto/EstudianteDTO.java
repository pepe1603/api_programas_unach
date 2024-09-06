package com.unach.api_pp_sc_rp.dto;

import com.unach.api_pp_sc_rp.model.enums.Sexo;
import lombok.Data;

@Data
public class EstudianteDTO {
    private Long id;
    private String matricula;
    private String nombre;
    private String apellido;
    private String sexo;
    private String correoInstitucional;
    private String telefono;
    private Integer semestre;
    private String grupo;
    private Long idCarrera;
    private Long idUsuario;
}

