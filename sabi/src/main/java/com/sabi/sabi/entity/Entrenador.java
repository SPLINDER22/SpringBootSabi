package com.sabi.sabi.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "entrenadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Entrenador extends Usuario {

    @Deprecated // Mantener por compatibilidad pero usar especialidades
    private String especialidad;

    @Column(length = 500)
    private String especialidades; // Múltiples especialidades separadas por comas

    @Column(name = "precio_minimo")
    private Double precioMinimo; // Precio mínimo por sesión/servicio

    @Column(name = "precio_maximo")
    private Double precioMaximo; // Precio máximo por sesión/servicio

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio;

    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia;

    @Column(length = 1000)
    private String certificaciones; // Rutas de archivos PDF separadas por coma

    // Relaciones

    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rutina> rutinas;

    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Suscripcion> suscripciones;

    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calificacion> calificaciones;

    // Nueva relación: comentarios (reseñas de cliente tras finalizar rutina)
    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;
}
