package com.sabi.sabi.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SemanaDTO {
    private Long idSemana;

    private Integer numeroSemana; // Ej: 1, 2, 3...

    private String descripcion; // notas opcionales del entrenador

    // Relación con Rutina
    private Long idRutina;

    // Relación con Días
    private List<Long> idDias;

    private Boolean estado = true;
}
