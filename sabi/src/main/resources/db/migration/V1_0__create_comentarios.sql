CREATE TABLE comentarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    texto VARCHAR(1000) NOT NULL,
    calificacion DOUBLE NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT NOT NULL,
    entrenador_id BIGINT NOT NULL,
    rutina_id BIGINT NULL,
    estado BIT NOT NULL DEFAULT 1,
    CONSTRAINT fk_comentario_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_comentario_entrenador FOREIGN KEY (entrenador_id) REFERENCES entrenadores(id),
    CONSTRAINT fk_comentario_rutina FOREIGN KEY (rutina_id) REFERENCES rutinas(id)
);

-- Índices auxiliares
CREATE INDEX idx_comentarios_entrenador ON comentarios(entrenador_id);
CREATE INDEX idx_comentarios_cliente ON comentarios(cliente_id);
CREATE INDEX idx_comentarios_rutina ON comentarios(rutina_id);

-- Evitar duplicado de comentario por cliente+rutina
CREATE UNIQUE INDEX ux_comentario_cliente_rutina ON comentarios(cliente_id, rutina_id);

