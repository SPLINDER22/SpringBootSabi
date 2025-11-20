package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.Intensidad;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_series")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la Serie planificada
    @ManyToOne
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    // Reps reales hechas (puede ser menor o mayor a lo planificado)
    @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
    private Long repeticionesReales;

    // Peso real usado (kg)
    @Min(value = 0, message = "El peso no puede ser negativo")
    private Double pesoReal;

    // Descanso real en segundos
    @Min(value = 0, message = "El descanso no puede ser negativo")
    private Long descansoReal;

    // Intensidad real con la que realizó el ejercicio
    @Enumerated(EnumType.STRING)
    private Intensidad intensidadReal;

    // Fecha y hora en que el cliente realizó la serie
    @NotNull(message = "La fecha de ejecución es obligatoria")
    private LocalDateTime fechaEjecucion;

    // Comentarios del cliente sobre esa serie
    @Size(max = 255, message = "El comentario no puede superar 255 caracteres")
    private String comentariosCliente;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}
