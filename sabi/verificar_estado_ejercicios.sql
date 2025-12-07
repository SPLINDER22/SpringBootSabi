-- Script para verificar y corregir el estado de los ejercicios asignados
-- Ejecutar este script en la consola H2 (http://localhost:8080/h2-console)

-- Ver el estado actual de todos los ejercicios asignados
SELECT
    ea.id_ejercicio_asignado,
    e.nombre AS nombre_ejercicio,
    ea.orden,
    ea.estado,
    d.numero_dia,
    s.numero_semana
FROM ejercicios_asignados ea
JOIN ejercicios e ON ea.ejercicio_id = e.id
JOIN dias d ON ea.dia_id = d.id
JOIN semanas s ON d.semana_id = s.id
ORDER BY s.numero_semana, d.numero_dia, ea.orden;

-- Si necesitas actualizar el estado de ejercicios específicos a completado (true):
-- UPDATE ejercicios_asignados SET estado = true WHERE id_ejercicio_asignado IN (34, 35);

-- Para ver todos los ejercicios que están marcados como completados:
-- SELECT * FROM ejercicios_asignados WHERE estado = true;

-- Para ver todos los ejercicios que están marcados como pendientes:
-- SELECT * FROM ejercicios_asignados WHERE estado = false;

