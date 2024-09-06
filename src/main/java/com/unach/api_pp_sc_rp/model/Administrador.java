package com.unach.api_pp_sc_rp.model;

import com.unach.api_pp_sc_rp.model.enums.Sexo;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "administradores")
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String idAdmin;//identificator unique could value  String or frase .
    @Column( nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String apellido;

    @Column(name = "correo_institucional", unique = true, nullable = false)
    private String correoInstitucional;

    @Column( nullable = false)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
