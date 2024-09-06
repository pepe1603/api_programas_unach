package com.unach.api_pp_sc_rp.model;

import com.unach.api_pp_sc_rp.model.enums.Grupo;
import com.unach.api_pp_sc_rp.model.enums.Sexo;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "estudiantes")
public class Estudiante{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String matricula;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    @Column(name = "correo_institucional", unique = true, nullable = false)
    private String correoInstitucional;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private Integer semestre;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
