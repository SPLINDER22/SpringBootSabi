package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.EstadoRutina;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rutinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la rutina es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El objetivo de la rutina es obligatorio")
    @Size(max = 100, message = "El objetivo no puede superar 100 caracteres")
    private String objetivo; // Ej: "Hipertrofia", "Resistencia"

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate fechaCreacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoRutina estadoRutina; // ACTIVA o FINALIZADA

    @NotNull(message = "Debe especificar el número de semanas")
    private Long numeroSemanas;

    // Relación con Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relación con Entrenador
    @ManyToOne
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Entrenador entrenador;

    // Relación con Semanas
    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Semana> semanas;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
