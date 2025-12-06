-- Migración para actualizar el estado de los ejercicios asignados
-- Los ejercicios que tienen todas sus series completadas deberían marcarse como completados

-- Actualizar ejercicios como completados si todas sus series tienen registros
UPDATE ejercicios_asignados ea
SET estado = true
WHERE ea.id_ejercicio_asignado IN (
    SELECT ea2.id_ejercicio_asignado
    FROM ejercicios_asignados ea2
    WHERE (
        SELECT COUNT(DISTINCT s.id)
        FROM series s
        WHERE s.ejercicio_asignado_id = ea2.id_ejercicio_asignado
    ) = (
        SELECT COUNT(DISTINCT rs.serie_id)
        FROM registros_series rs
        JOIN series s2 ON rs.serie_id = s2.id
        WHERE s2.ejercicio_asignado_id = ea2.id_ejercicio_asignado
    )
    AND (
        SELECT COUNT(DISTINCT s.id)
        FROM series s
        WHERE s.ejercicio_asignado_id = ea2.id_ejercicio_asignado
    ) > 0
);

-- Verificar el resultado
SELECT
    ea.id_ejercicio_asignado,
    e.nombre AS nombre_ejercicio,
    ea.estado,
    COUNT(DISTINCT s.id) as total_series,
    COUNT(DISTINCT rs.serie_id) as series_completadas
FROM ejercicios_asignados ea
JOIN ejercicios e ON ea.ejercicio_id = e.id
LEFT JOIN series s ON s.ejercicio_asignado_id = ea.id_ejercicio_asignado
LEFT JOIN registros_series rs ON rs.serie_id = s.id
GROUP BY ea.id_ejercicio_asignado, e.nombre, ea.estado;

