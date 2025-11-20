package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.Intensidad;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistroSerieDTO {
    private Long idRegistroSerie;

    // Relación con la Serie planificada
    private Long idSerie;

    // Reps reales hechas (puede ser menor o mayor a lo planificado)
    private Long repeticionesReales;

    // Peso real usado (kg)
    private Double pesoReal;

    // Descanso real en segundos
    private Long descansoReal;

    // Intensidad real con la que realizó el ejercicio
    private Intensidad intensidadReal;

    // Fecha y hora en que el cliente realizó la serie
    private LocalDateTime fechaEjecucion;

    // Comentarios del cliente sobre esa serie
    private String comentariosCliente;

    private Boolean estado = true;
}
