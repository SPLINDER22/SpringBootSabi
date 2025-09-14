package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.TipoEjercicio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "ejercicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @Size(max = 50, message = "El grupo muscular no puede superar 50 caracteres")
    private String grupoMuscular;

    @Size(max = 50, message = "El equipo no puede superar 50 caracteres")
    private String equipo; // barra, mancuernas, peso corporal, etc.

    private String urlVideo;
    private String urlImagen;

    // Enum para visibilidad
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEjercicio tipo;

    // Si es privado, se asigna al entrenador creador
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
