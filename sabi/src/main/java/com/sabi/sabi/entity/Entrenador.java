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

    private String especialidad;

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
}
