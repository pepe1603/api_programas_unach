package com.unach.api_pp_sc_rp.model;

import com.unach.api_pp_sc_rp.model.enums.Sexo;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String direccion;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String correo;
    //Datosss-del-Responsable
    @Column(nullable = false)
    private String nombreResponsable;
    @Column(nullable = false)
    private String apellidoResponsable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexoResponsable;

    @Column(nullable = false)
    private String puestoResponsable;


}

