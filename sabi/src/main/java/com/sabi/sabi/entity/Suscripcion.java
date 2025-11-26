package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "suscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "precio", nullable = true)
    private Double precio;

    // Duración en semanas de la futura rutina que asignará el entrenador
    private Integer duracionSemanas;

    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estadoSuscripcion;

    // Relación con Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relación con Entrenador
    @ManyToOne
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    // indica si el entrenador ya abrió la vista del diagnóstico del cliente para esta suscripción
    @Builder.Default
    @Column(name = "vista_diagnostico", nullable = false)
    private Boolean vistaDiagnostico = false;
}
