package com.sabi.sabi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalificacionDTO {
    private Long idCalificacion;

    private int estrellas; // rango 1–5

    private String comentario;

    private Long idCliente;

    private Long idEntrenador;

    private Boolean estado = true;
}
