package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.NivelExperiencia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "diagnosticos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha; // fecha en la que se tomó este diagnóstico

    private LocalDate proximoDiagnostico; // fecha programada/recomendada del siguiente

    // Relación con Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Obligatorios
    private Double peso;                 // en kg
    private Double estatura;             // en cm

    @Enumerated(EnumType.STRING)
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
    private Integer frecuenciaCardiacaReposo; // bpm
    private String presionArterial;           // ej. "120/80"
    private Integer horasSueno;               // horas promedio
    private String habitosAlimenticios;       // descripción libre
}
