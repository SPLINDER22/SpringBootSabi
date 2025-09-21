package com.sabi.sabi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "ejercicios_asignados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EjercicioAsignado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjercicioAsignado;

    @NotNull(message = "El orden del ejercicio es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
    private Long orden; // para organizar el día

    @Size(max = 255, message = "El comentario no puede superar 255 caracteres")
    private String comentarios; // notas adicionales del entrenador

    @NotNull(message = "Debe especificar el número de series")
    private Long numeroSeries;

    // Relación con Día
    @ManyToOne
    @JoinColumn(name = "dia_id", nullable = false)
    private Dia dia;

    // Relación con Ejercicio (tabla maestra)
    @ManyToOne
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private Ejercicio ejercicio;

    // Relación con Series planificadas
    @OneToMany(mappedBy = "ejercicioAsignado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Serie> series;

    // URL opcional del video subido por el cliente
    @Size(max = 500, message = "La URL del video no puede superar 500 caracteres")
    private String urlVideoCliente;

    private Boolean checked = false;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
