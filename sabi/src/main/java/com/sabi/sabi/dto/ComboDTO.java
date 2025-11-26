package com.sabi.sabi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComboDTO {
    private Long id;
    private String nombre;
    private Long idEjercicio;
    private String nombreEjercicio;
    private Integer cantidadSeries;
}
