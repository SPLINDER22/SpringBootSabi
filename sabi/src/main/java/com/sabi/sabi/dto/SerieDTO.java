package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.Intensidad;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SerieDTO {
    private Long id;

    // Orden dentro del ejercicio
    private Long orden;

    // Repeticiones (ahora cadena para permitir rangos como "8-12")
    private String repeticiones;

    // Peso en kg (opcional)
    private Double peso;

    // Descanso en segundos (opcional)
    private String descanso;

    // Intensidad estandarizada
    private Intensidad intensidad;

    // Comentarios del entrenador para la serie
    private String comentarios;

    // Relación con EjercicioAsignado
    private Long idEjercicioAsignado;

    private Boolean estado = false;
}
