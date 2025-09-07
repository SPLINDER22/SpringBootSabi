package com.sabi.sabi.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiaDTO {
    private Long idDia;

    private Integer numeroDia; // Ej: 1, 2, 3...

    private String descripcion; // notas opcionales (ej. "Cardio + tren inferior")

    private Long idSemana;

    private List<Long> idEjerciciosAsignados;

    private Boolean estado = true;
}
