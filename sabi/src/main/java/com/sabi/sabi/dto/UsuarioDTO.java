package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.Departamento;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.entity.enums.Sexo;
import com.sabi.sabi.entity.enums.TipoDocumento;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String contraseña;
    private Rol rol;

    // Nuevos campos
    private Sexo sexo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;
    private Departamento departamento;
    private String ciudad;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String telefono;
    private String fotoPerfilUrl;
    private String descripcion;

    private List<Long> idEjercicios;
    private Boolean estado = true;

    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}
