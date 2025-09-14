package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.TipoEjercicio;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EjercicioDTO {
    private Long idEjercicio;

    private String nombre;

    private String descripcion;

    private String grupoMuscular;

    private String equipo; // barra, mancuernas, peso corporal, etc.

    private String urlVideo;
    private String urlImagen;

    // Enum para visibilidad
    private TipoEjercicio tipo;

    // Si es privado, se asigna al entrenador creador
    private Long idUsuario;

    private Boolean estado = true;
}
