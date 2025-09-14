package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.Rol;
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
    private Rol rol;
    private Boolean estado = true;
}
