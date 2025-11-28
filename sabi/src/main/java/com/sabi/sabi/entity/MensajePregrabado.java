package com.sabi.sabi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_pregrabados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajePregrabado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;

    @Column(nullable = false, length = 100)
    private String titulo; // Ej: "Descuento especial", "Aviso de cancelación"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido; // El mensaje pregrabado con posibles variables {NOMBRE}, {DESCUENTO}, etc.

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
