package com.unach.api_pp_sc_rp.dto;

import com.unach.api_pp_sc_rp.model.enums.Sexo;
import lombok.Data;

@Data
public class AdministradorDTO {
    private Long id;
    private String idAdmin;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String telefono;
    private String sexo;
    private Long idUsuario;
}
