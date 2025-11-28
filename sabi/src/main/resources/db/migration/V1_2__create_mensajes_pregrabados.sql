-- Migración: Crear tabla de mensajes pregrabados para entrenadores
-- Fecha: 28/11/2025
-- Descripción: Los entrenadores podrán crear mensajes pregrabados reutilizables
--              con variables personalizables para comunicarse con sus clientes

CREATE TABLE IF NOT EXISTS mensajes_pregrabados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entrenador_id BIGINT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_actualizacion DATETIME,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT fk_mensaje_entrenador 
        FOREIGN KEY (entrenador_id) 
        REFERENCES entrenadores(id)
        ON DELETE CASCADE,
    
    INDEX idx_entrenador_activo (entrenador_id, activo),
    INDEX idx_fecha_creacion (fecha_creacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Comentarios de tabla
ALTER TABLE mensajes_pregrabados 
    COMMENT = 'Mensajes pregrabados por entrenadores para comunicación con clientes';
