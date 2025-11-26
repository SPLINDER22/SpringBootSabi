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

    // Campo objetivo: copia del objetivo del cliente en este momento (historial)
    // Se guarda para recordar cuál era la meta del cliente cuando hizo este diagnóstico
    @Column(length = 500)
    private String objetivo;

    // Obligatorios
    private Double peso;                 // en kg
    private Double estatura;             // en cm

    @Enumerated(EnumType.STRING)
    private NivelExperiencia nivelExperiencia;

    private String disponibilidadTiempo; // ej. "3 veces por semana, 45 min"
    private String accesoRecursos;       // ej. "casa", "gimnasio", "pesas"
    private String lesiones;
    private String condicionesMedicas;


    // Opcionales
    private String fotoFrontalUrl;
    private String fotoLateralUrl;
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

    // Campos adicionales utiles para el entrenador (NUEVOS - OPCIONALES)
    @Column(length = 500)
    private String preferenciasEntrenamiento; // ej. "Prefiero ejercicios funcionales, no me gusta correr"

    @Column(length = 500)
    private String experienciaPreviaDeportes; // ej. "Jugue futbol 5 anos, natacion 2 anos"

    private Integer diasDisponiblesSemana; // ej. 3, 4, 5

    @Column(length = 200)
    private String horarioPreferido; // ej. "Mananas 6am-8am" o "Tardes 5pm-7pm"

    @Column(length = 500)
    private String limitacionesFisicas; // ej. "No puedo hacer sentadillas profundas por problema de rodilla"

    @Column(length = 500)
    private String motivacionPrincipal; // ej. "Mejorar salud, verme mejor, competir"

    private String nivelEstres; // ej. "BAJO", "MEDIO", "ALTO"

    @Column(length = 500)
    private String suplementosActuales; // ej. "Proteina whey, creatina"

    private Boolean fumador; // true/false

    private Boolean consumeAlcohol; // true/false

    @Column(length = 200)
    private String frecuenciaAlcohol; // ej. "1 vez por semana", "Nunca", "2-3 veces/semana"

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
