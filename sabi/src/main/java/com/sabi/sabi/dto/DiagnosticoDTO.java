package com.sabi.sabi.dto;

import com.sabi.sabi.entity.enums.NivelExperiencia;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiagnosticoDTO {
    private Long idDiagnostico;

    private LocalDate fecha; // fecha en la que se tomó este diagnóstico

    private LocalDate proximoDiagnostico; // fecha programada/recomendada del siguiente

    private Long idCliente;

    // Campo para el objetivo del cliente al hacer el diagnóstico
    private String objetivo;

    // Obligatorios
    private Double peso;                 // en kg
    private Double estatura;             // en cm

    private NivelExperiencia nivelExperiencia;

    private String disponibilidadTiempo; // ej. "3 veces por semana, 45 min"
    private String accesoRecursos;       // ej. "casa", "gimnasio", "pesas"
    private String lesiones;
    private String condicionesMedicas;

    private String fotoFrontalUrl;
    private String fotoLateralUrl;

    // Opcionales
    private String fotoTraseraUrl;
    private Double porcentajeGrasaCorporal;
    private Double circunferenciaCintura;
    private Double circunferenciaCadera;
    private Double circunferenciaBrazo;
    private Double circunferenciaPierna;
    private Long frecuenciaCardiacaReposo; // bpm
    private String presionArterial;           // ej. "120/80"
    private Long horasSueno;               // horas promedio
    private String habitosAlimenticios;       // descripción libre

    // Campos adicionales utiles para el entrenador (OPCIONALES)
    private String preferenciasEntrenamiento;
    private String experienciaPreviaDeportes;
    private Integer diasDisponiblesSemana;
    private String horarioPreferido;
    private String limitacionesFisicas;
    private String motivacionPrincipal;
    private String nivelEstres;
    private String suplementosActuales;
    private Boolean fumador;
    private Boolean consumeAlcohol;
    private String frecuenciaAlcohol;

    private Boolean estado = true;


    public Long getHorasSueno() {
        return horasSueno;
    }

    public String getAlimentacion() {
        return habitosAlimenticios;
    }
}
