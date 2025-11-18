package com.sabi.sabi.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class EntrenadorDTO extends UsuarioDTO{
    private Long idUsuario;

    @Deprecated // Mantener por compatibilidad
    private String especialidad;

    private String especialidades; // Múltiples especialidades separadas por comas

    private Double precioMinimo; // Precio mínimo por sesión/servicio

    private Double precioMaximo; // Precio máximo por sesión/servicio

    private Double calificacionPromedio;

    private Integer aniosExperiencia;

    private String certificaciones;

    // Relaciones

    private List<Long> idRutinas;

    private List<Long> idSuscripciones;

    private List<Long> idCalificaciones;
}
