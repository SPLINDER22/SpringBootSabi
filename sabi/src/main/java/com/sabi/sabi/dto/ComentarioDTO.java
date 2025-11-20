package com.sabi.sabi.dto;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ComentarioDTO {
    private Long idComentario;
    @NotBlank
    private String texto;
    @Min(0) @Max(5)
    private Double calificacion; // 0.0 - 5.0
    private LocalDateTime fechaCreacion;
    private Long idCliente;
    private Long idEntrenador;
    private Long idRutina;
    private Boolean estado = true;
}

