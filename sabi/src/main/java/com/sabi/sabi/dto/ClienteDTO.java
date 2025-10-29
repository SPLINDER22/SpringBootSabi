package com.sabi.sabi.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ClienteDTO extends UsuarioDTO {

    private String objetivo;  // Objetivo de fitness del cliente

    // Relaciones solo por IDs
    private List<Long> idDiagnosticos;
    private List<Long> idRutinas;
    private List<Long> idSuscripciones;
    private List<Long> idCalificaciones;
}
