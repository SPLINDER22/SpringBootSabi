package com.sabi.sabi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 1000)
    private String texto; // contenido del comentario

    @Min(0)
    @Max(5)
    private Double calificacion; // estrellas 0.0 a 5.0 (permitimos decimales)

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entrenador_id")
    private Entrenador entrenador;

    @ManyToOne(optional = true)
    @JoinColumn(name = "rutina_id")
    private Rutina rutina; // rutina finalizada que genera el comentario

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }
}

