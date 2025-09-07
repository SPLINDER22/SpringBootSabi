package com.sabi.sabi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificacionDTO {
    private Long idNotificacion;

    private Long idUsuario;

    private String titulo;

    private String mensaje;

    private LocalDateTime fechaCreacion;

    private boolean leida = false;

    private Boolean estado = true;
}
