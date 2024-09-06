package com.unach.api_pp_sc_rp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipos_programas")
public class TipoPrograma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false)
    private String nombre;
    private String descripcion;
}
