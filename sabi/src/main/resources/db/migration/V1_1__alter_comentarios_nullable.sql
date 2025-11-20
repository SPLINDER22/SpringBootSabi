-- Permitir valores NULL en texto y calificacion para comentarios opcionales al finalizar rutina
-- MySQL / MariaDB syntax
ALTER TABLE comentarios MODIFY texto VARCHAR(1000) NULL;
ALTER TABLE comentarios MODIFY calificacion DOUBLE NULL;

