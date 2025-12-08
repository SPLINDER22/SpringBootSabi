-- ========================================
-- VERIFICAR 3 ENTRENADORES ADICIONALES
-- ========================================
-- Este script verifica los 3 primeros entrenadores pendientes de verificación

-- 1. Ver entrenadores pendientes de verificación (con certificaciones)
SELECT
    id,
    nombre,
    apellido,
    email,
    certificaciones,
    especialidad,
    anios_experiencia,
    verified
FROM entrenadores
WHERE verified = 0
  AND certificaciones IS NOT NULL
  AND certificaciones != ''
ORDER BY id
LIMIT 3;

-- 2. Verificar los 3 primeros entrenadores pendientes
UPDATE entrenadores
SET verified = 1
WHERE id IN (
    SELECT id FROM (
        SELECT id
        FROM entrenadores
        WHERE verified = 0
          AND certificaciones IS NOT NULL
          AND certificaciones != ''
        ORDER BY id
        LIMIT 3
    ) AS temp
);

-- 3. Verificar el resultado
SELECT
    '✅ ENTRENADORES VERIFICADOS' AS '',
    id,
    CONCAT(nombre, ' ', apellido) AS 'Nombre Completo',
    email,
    especialidad,
    CASE
        WHEN verified = 1 THEN '✅ VERIFICADO'
        ELSE '⏳ PENDIENTE'
    END AS 'Estado'
FROM entrenadores
WHERE verified = 1
ORDER BY id;

-- 4. Resumen actualizado
SELECT
    '📊 RESUMEN ACTUALIZADO' AS '',
    SUM(CASE WHEN verified = 1 THEN 1 ELSE 0 END) AS 'Verificados',
    SUM(CASE WHEN verified = 0 THEN 1 ELSE 0 END) AS 'Pendientes',
    COUNT(*) AS 'Total'
FROM entrenadores;

