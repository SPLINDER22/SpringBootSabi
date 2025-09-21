package com.sabi.sabi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerieViewDTO {
    private Long id;
    private Long orden;
    private Long repeticiones;
    private Double peso;
    private String descanso;
    private String intensidad; // usar name() del enum
}

