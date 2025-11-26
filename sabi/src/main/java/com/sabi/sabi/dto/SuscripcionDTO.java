package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.EstadoSuscripcion;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SuscripcionDTO {
    private Long idSuscripcion;

    private Double precio;

    private Integer duracionSemanas;

    private EstadoSuscripcion estadoSuscripcion;

    // Relación con Cliente
    private Long idCliente;

    // Relación con Entrenador
    private Long idEntrenador;

    private Boolean estado = true;

    // Nuevo: flag para habilitar cotización tras ver diagnóstico
    private Boolean vistaDiagnostico = false;
}
