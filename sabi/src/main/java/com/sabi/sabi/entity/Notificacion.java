package com.sabi.sabi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Usuario (puede ser Cliente o Entrenador)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El título de la notificación es obligatorio")
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotBlank(message = "El mensaje de la notificación es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @NotNull(message = "La fecha de creación es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean leida = false;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
