-- ====================================================================
-- MIGRACIÓN: Eliminar campo 'objetivo' de la tabla diagnosticos
-- ====================================================================
-- RAZÓN: El objetivo ahora se maneja exclusivamente en el PERFIL del
--        cliente (tabla usuarios, campo objetivo), no en diagnósticos.
-- ====================================================================
-- FECHA: 2025-01-26
-- ====================================================================

-- Verificar si la columna existe antes de eliminarla
SELECT
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_NAME = 'diagnosticos'
    AND COLUMN_NAME = 'objetivo';

-- Eliminar la columna objetivo de la tabla diagnosticos
ALTER TABLE diagnosticos
DROP COLUMN IF EXISTS objetivo;

-- Verificar que se eliminó correctamente
SELECT
    COLUMN_NAME,
    DATA_TYPE
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_NAME = 'diagnosticos'
ORDER BY
    ORDINAL_POSITION;

-- ====================================================================
-- NOTA IMPORTANTE:
-- ====================================================================
-- Después de ejecutar esta migración:
--
-- 1. El objetivo ya NO se guardará en la tabla 'diagnosticos'
-- 2. El objetivo se guarda y lee SOLO de 'usuarios.objetivo' (Cliente)
-- 3. Al crear un diagnóstico, el objetivo del formulario actualiza
--    automáticamente 'usuarios.objetivo'
-- 4. El dashboard lee el objetivo de 'usuarios.objetivo'
--
-- VENTAJAS:
-- ✅ Más simple - Un solo lugar central
-- ✅ Más flexible - Editable desde perfil O diagnóstico
-- ✅ Más consistente - Siempre del perfil del cliente
-- ✅ Más robusto - No depende de "diagnóstico más reciente"
-- ====================================================================

