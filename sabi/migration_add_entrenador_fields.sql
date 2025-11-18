-- Script de migración para agregar nuevos campos a entrenadores
-- Fecha: 2025-01-18
-- Descripción: Agregar soporte para múltiples especialidades y rango de precios

-- 1. Agregar columna para múltiples especialidades
ALTER TABLE entrenadores
ADD COLUMN IF NOT EXISTS especialidades VARCHAR(500) COMMENT 'Múltiples especialidades separadas por comas';

-- 2. Agregar columna para precio mínimo
ALTER TABLE entrenadores
ADD COLUMN IF NOT EXISTS precio_minimo DOUBLE COMMENT 'Precio mínimo por sesión en COP';

-- 3. Agregar columna para precio máximo
ALTER TABLE entrenadores
ADD COLUMN IF NOT EXISTS precio_maximo DOUBLE COMMENT 'Precio máximo por sesión en COP';

-- 4. (Opcional) Migrar datos existentes de especialidad a especialidades
-- Este comando copia el valor de 'especialidad' a 'especialidades' solo si especialidades está vacío
UPDATE entrenadores
SET especialidades = especialidad
WHERE especialidades IS NULL AND especialidad IS NOT NULL;

-- 5. Verificar los cambios
SELECT
    id,
    nombre,
    apellido,
    especialidad as especialidad_antigua,
    especialidades as especialidades_nuevas,
    precio_minimo,
    precio_maximo
FROM entrenadores
LIMIT 10;

-- Notas:
-- - La columna 'especialidad' (singular) se mantiene para compatibilidad
-- - Si usas Hibernate con ddl-auto=update, estos cambios se aplicarán automáticamente
-- - Para MySQL/MariaDB, usa este script tal cual
-- - Para PostgreSQL, cambia DOUBLE por NUMERIC
-- - Para H2 (desarrollo), no es necesario ejecutar este script

