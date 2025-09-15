package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.Rol;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String contraseña;
    private Rol rol;
    private List<Long> idEjercicios;
    private Boolean estado = true;
}
