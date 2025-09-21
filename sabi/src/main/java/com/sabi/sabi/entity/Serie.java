package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.Intensidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "series")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Orden dentro del ejercicio
    @NotNull(message = "El orden de la serie es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
    private Long orden;

    // Repeticiones (opcional)
    @Min(value = 1, message = "Las repeticiones deben ser al menos 1")
    private Long repeticiones;

    // Peso en kg (opcional)
    @Min(value = 0, message = "El peso no puede ser negativo")
    private Double peso;

    // Descanso en segundos (opcional)
    private String descanso;

    // Intensidad estandarizada
    @Enumerated(EnumType.STRING)
    private Intensidad intensidad;

    // Comentarios del entrenador para la serie
    @Size(max = 255, message = "El comentario no puede superar 255 caracteres")
    private String comentarios;

    // Relación con EjercicioAsignado
    @ManyToOne
    @JoinColumn(name = "ejercicio_asignado_id", nullable = false)
    private EjercicioAsignado ejercicioAsignado;

    private Boolean checked = false;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
