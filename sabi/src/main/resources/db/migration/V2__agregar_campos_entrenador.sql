-- Agregar campos de años de experiencia y certificaciones a la tabla de entrenadores
ALTER TABLE entrenadores
ADD COLUMN anios_experiencia INT DEFAULT 0,
ADD COLUMN certificaciones VARCHAR(500);

-- Actualizar los registros existentes con valores por defecto
UPDATE entrenadores
SET anios_experiencia = 0
WHERE anios_experiencia IS NULL;

