-- Añadir campo verified a la tabla entrenadores si no existe
-- Esta migración asegura que todos los entrenadores tengan el campo de verificación

-- Añadir columna verified si no existe
ALTER TABLE entrenadores 
ADD COLUMN IF NOT EXISTS verified BOOLEAN NOT NULL DEFAULT FALSE;

-- Crear índice para búsquedas eficientes de entrenadores verificados
CREATE INDEX IF NOT EXISTS idx_entrenadores_verified ON entrenadores(verified);

-- Comentario de la tabla
COMMENT ON COLUMN entrenadores.verified IS 'Indica si el entrenador ha sido verificado por el administrador de SABI';
