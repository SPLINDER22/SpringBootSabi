-- Script para verificar el estado de verificación de entrenadores
-- Este script muestra todos los entrenadores con su estado de verificación

SELECT 
    id,
    nombre,
    apellido,
    email,
    verified AS 'Verificado',
    CASE 
        WHEN verified = 1 THEN '✅ VERIFICADO'
        ELSE '⏳ PENDIENTE'
    END AS 'Estado Verificación',
    CASE 
        WHEN certificaciones IS NOT NULL AND certificaciones != '' THEN '✓ Tiene certificaciones'
        ELSE '✗ Sin certificaciones'
    END AS 'Certificaciones',
    estado AS 'Activo'
FROM entrenadores
ORDER BY verified DESC, nombre;

-- Contar verificados vs pendientes
SELECT 
    '📊 RESUMEN' AS '',
    SUM(CASE WHEN verified = 1 THEN 1 ELSE 0 END) AS 'Entrenadores Verificados',
    SUM(CASE WHEN verified = 0 THEN 1 ELSE 0 END) AS 'Entrenadores Pendientes',
    SUM(CASE WHEN verified = 0 AND certificaciones IS NOT NULL AND certificaciones != '' THEN 1 ELSE 0 END) AS 'Candidatos a Verificar',
    COUNT(*) AS 'Total Entrenadores'
FROM entrenadores;
