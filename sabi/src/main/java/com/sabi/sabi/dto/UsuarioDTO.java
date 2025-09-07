package com.sabi.sabi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String email;
    private String contraseña;
    private String rol;
    private Boolean estado = true;
}
