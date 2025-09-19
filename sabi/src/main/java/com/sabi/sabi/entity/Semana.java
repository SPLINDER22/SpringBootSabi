package com.sabi.sabi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "semanas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El número de semana es obligatorio")
    @Min(value = 1, message = "El número de semana debe ser al menos 1")
    private Long numeroSemana; // Ej: 1, 2, 3...

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion; // notas opcionales del entrenador

    private Long numeroDias;

    // Relación con Rutina
    @ManyToOne
    @JoinColumn(name = "rutina_id", nullable = false)
    private Rutina rutina;

    // Relación con Días
    @OneToMany(mappedBy = "semana", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dia> dias;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
