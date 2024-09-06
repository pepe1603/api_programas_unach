package com.unach.api_pp_sc_rp.dto;

import com.unach.api_pp_sc_rp.model.enums.EstadoEvento;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventoDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaEvento;
    private Long idTipoPrograma; // ID del programa asociado
    private String estadoEvento;
}
