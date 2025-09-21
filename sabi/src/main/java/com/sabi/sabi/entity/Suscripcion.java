package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    private String plazo; // ej. "1 mes", "3 meses"

    @Column(name = "precio", nullable = true)
    private Double precio;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

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
}
