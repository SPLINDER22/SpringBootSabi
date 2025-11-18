package com.sabi.sabi.entity.enums;

public enum Especialidad {
    FUERZA_Y_ACONDICIONAMIENTO("Fuerza y Acondicionamiento"),
    PERDIDA_DE_PESO("Pérdida de Peso"),
    GANANCIA_MUSCULAR("Ganancia Muscular"),
    YOGA("Yoga"),
    PILATES("Pilates"),
    CROSSFIT("CrossFit"),
    ENTRENAMIENTO_FUNCIONAL("Entrenamiento Funcional"),
    CARDIO_Y_RESISTENCIA("Cardio y Resistencia"),
    DEPORTES_ESPECIFICOS("Deportes Específicos"),
    REHABILITACION("Rehabilitación"),
    NUTRICION_DEPORTIVA("Nutrición Deportiva"),
    ENTRENAMIENTO_PERSONAL("Entrenamiento Personal"),
    POWERLIFTING("Powerlifting"),
    CALISTENIA("Calistenia"),
    RUNNING("Running"),
    CICLISMO("Ciclismo"),
    NATACION("Natación"),
    BOXEO("Boxeo"),
    ARTES_MARCIALES("Artes Marciales"),
    MOVILIDAD_Y_FLEXIBILIDAD("Movilidad y Flexibilidad");

    private final String displayName;

    Especialidad(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Especialidad fromDisplayName(String displayName) {
        for (Especialidad especialidad : values()) {
            if (especialidad.displayName.equals(displayName)) {
                return especialidad;
            }
        }
        return null;
    }
}

