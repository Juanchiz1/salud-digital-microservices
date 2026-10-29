CREATE TABLE usuario (
    id            UUID         PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nombre        VARCHAR(150) NOT NULL,
    rol           VARCHAR(20)  NOT NULL,
    medico_id     UUID         UNIQUE REFERENCES medico (id),
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('RECEPCION', 'MEDICO', 'GERENCIA')),
    CONSTRAINT ck_usuario_medico CHECK ((rol = 'MEDICO') = (medico_id IS NOT NULL))
);

-- Usuarios de demostración. Contraseña de todos: SaludDigital2026
INSERT INTO usuario (id, username, password_hash, nombre, rol, medico_id) VALUES
    (gen_random_uuid(), 'recepcion', '$2b$10$JEE.xdPcU/7L6nRx7uLNneHanfUO2YOpz..M2Dtu/1B9EkoLXLgy2', 'Paula Ramos',         'RECEPCION', NULL),
    (gen_random_uuid(), 'dr.gomez',  '$2b$10$JEE.xdPcU/7L6nRx7uLNneHanfUO2YOpz..M2Dtu/1B9EkoLXLgy2', 'Andrés Gómez Ortiz',  'MEDICO',    '11111111-1111-1111-1111-111111111111'),
    (gen_random_uuid(), 'dra.diaz',  '$2b$10$JEE.xdPcU/7L6nRx7uLNneHanfUO2YOpz..M2Dtu/1B9EkoLXLgy2', 'Carolina Díaz Mejía', 'MEDICO',    '22222222-2222-2222-2222-222222222222'),
    (gen_random_uuid(), 'gerencia',  '$2b$10$JEE.xdPcU/7L6nRx7uLNneHanfUO2YOpz..M2Dtu/1B9EkoLXLgy2', 'Gerencia',            'GERENCIA',  NULL);