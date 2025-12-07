package com.sabi.sabi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoSemanaDTO {
    private Long numeroSemana;
    private Long diasCompletados;
    private Long totalDias;
    private String nombreSemana; // Opcional: por si la semana tiene un nombre
}

