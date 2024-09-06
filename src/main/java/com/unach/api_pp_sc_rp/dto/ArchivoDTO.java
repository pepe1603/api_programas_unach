package com.unach.api_pp_sc_rp.dto;

import lombok.Data;

@Data
public class ArchivoDTO {
    private Long id;
    private String nombreArchivo;
    private String rutaArchivo;
    private Long idPrograma;
}
