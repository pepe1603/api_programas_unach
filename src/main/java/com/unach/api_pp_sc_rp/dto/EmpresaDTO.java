package com.unach.api_pp_sc_rp.dto;

import com.unach.api_pp_sc_rp.model.enums.Sexo;
import lombok.Data;

@Data
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String correo;
    private String nombreResponsable;
    private String apellidoResponsable;
    private String sexoResponsable;
    private String puestoResponsable;
}
