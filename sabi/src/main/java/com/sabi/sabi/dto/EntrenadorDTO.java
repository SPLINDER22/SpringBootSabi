package com.sabi.sabi.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntrenadorDTO extends UsuarioDTO{
    private Long idUsuario;

    private String especialidades;

    private String experiencia;

    private Double calificacionPromedio;

    // Relaciones

    private List<Long> idRutinas;

    private List<Long> idSuscripciones;

    private List<Long> idCalificaciones;
}
