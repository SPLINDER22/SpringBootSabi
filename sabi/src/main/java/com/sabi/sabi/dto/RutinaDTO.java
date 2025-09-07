package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.EstadoRutina;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RutinaDTO {
    private Long idRutina;

    private String nombre;

    private String objetivo; // Ej: "Hipertrofia", "Resistencia"

    private String descripcion;

    private LocalDate fechaCreacion;

    private EstadoRutina estadoRutina; // ACTIVA o FINALIZADA

    private Integer numeroSemanas;

    // Relación con Cliente
    private Long idCliente;

    // Relación con Entrenador
    private Long idEntrenador;

    // Relación con Semanas
    private List<Long> idSemanas;

    private Boolean estado = true;
}
