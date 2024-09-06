package com.unach.api_pp_sc_rp.model;

import com.unach.api_pp_sc_rp.model.enums.EstadoAvance;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "programas")
public class Programa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo_proyecto")
    private String tituloProyecto;
    @Column(name = "descripcion_proyecto", columnDefinition = "TEXT")
    private String descripcionProyecto;

    private LocalDateTime fecha_inicio;
    private LocalDateTime fecha_fin;

    @Column(name = "total_horas", nullable = false)
    private Integer totalHoras;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "estado_avance")
    private EstadoAvance estadoAvance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_tipo_programa", nullable = false)
    private TipoPrograma tipoPrograma;

    private boolean finalizado;

}
