package com.sabi.sabi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "dias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El número de día es obligatorio")
    @Min(value = 1, message = "El número de día debe ser al menos 1")
    private Long numeroDia; // Ej: 1, 2, 3...

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion; // notas opcionales (ej. "Cardio + tren inferior")

    @NotNull(message = "Debe especificar el número de ejercicios")
    private Long numeroEjercicios;

    // Relación con Semana
    @ManyToOne
    @JoinColumn(name = "semana_id", nullable = false)
    private Semana semana;

    // Relación con Ejercicios Asignados
    @OneToMany(mappedBy = "dia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EjercicioAsignado> ejerciciosAsignados;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
