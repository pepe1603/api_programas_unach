package com.unach.api_pp_sc_rp.model;

import com.unach.api_pp_sc_rp.model.enums.EstadoEvento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "fechaEvento", nullable = false)
    private LocalDateTime fechaEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estadoEvento", nullable = false)
    private EstadoEvento estadoEvento;

    @ManyToOne
    @JoinColumn(name = "programa_id", nullable = false)
    private TipoPrograma TipoPrograma;

}
