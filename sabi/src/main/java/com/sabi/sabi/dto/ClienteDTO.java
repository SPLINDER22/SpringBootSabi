package com.sabi.sabi.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClienteDTO extends UsuarioDTO {


    private String objetivos;

    private LocalDate fechaNacimiento;

    private String sexo;

    private String telefono;

    private String ciudad;

    // Relaciones solo por IDs
    private List<Long> idDiagnosticos;
    private List<Long> idRutinas;
    private List<Long> idSuscripciones;
    private List<Long> idCalificaciones;
}
