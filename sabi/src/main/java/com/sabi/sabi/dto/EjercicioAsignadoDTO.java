package com.sabi.sabi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EjercicioAsignadoDTO {
    private Long idEjercicioAsignado;

    private Long orden; // para organizar el día

    private String comentarios; // notas adicionales del entrenador

    private Long numeroSeries;

    // Relación con Día
    private Long idDia;

    // Relación con Ejercicio (tabla maestra)
    private Long idEjercicio;

    private String nombreEjercicio;

    // Relación con Series planificadas
    private List<Long> idSeries;

    // URL opcional del video subido por el cliente
    private String urlVideoCliente;

    private Boolean estado = false;
}
