package com.unach.api_pp_sc_rp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgramaDTO {
    private Long id;
    private String tituloProyecto;
    private String descripcionProyecto;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer totalHoras;
    private Long idEstudiante;
    private Long idEmpresa;
    private String estadoAvance;
    private Long idTipoPrograma;
    private boolean finalizado;
}
