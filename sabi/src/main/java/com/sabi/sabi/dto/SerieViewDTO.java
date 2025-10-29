package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.Intensidad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerieViewDTO {
    private Long id;
    private Long orden;
    private String repeticiones;
    private Double peso;
    private String descanso;

    private Intensidad intensidad; // usar name() del enum
}
