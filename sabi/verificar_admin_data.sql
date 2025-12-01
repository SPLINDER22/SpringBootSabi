-- Script de verificación y prueba para funcionalidades del admin
-- Ejecutar en H2 Console o MySQL

-- 1. Ver todos los entrenadores
SELECT id, nombre, email, verified, certificaciones, estado
FROM usuarios
WHERE rol = 'ENTRENADOR'
ORDER BY id;

-- 2. Ver entrenadores con certificaciones
SELECT id, nombre, email, verified,
       CASE
           WHEN certificaciones IS NULL OR certificaciones = '' THEN 'Sin certificaciones'
           ELSE 'Tiene certificaciones'
       END as tiene_certs
FROM usuarios
WHERE rol = 'ENTRENADOR'
ORDER BY verified, id;

-- 3. Entrenadores pendientes de verificación con certificaciones
SELECT id, nombre, email, certificaciones
FROM usuarios
WHERE rol = 'ENTRENADOR'
  AND verified = false
  AND certificaciones IS NOT NULL
  AND TRIM(certificaciones) <> '';

-- 4. Ver estados de todos los usuarios admin
SELECT id, nombre, email, rol, estado, verified
FROM usuarios
WHERE rol IN ('ADMIN', 'ENTRENADOR')
ORDER BY rol, id;

