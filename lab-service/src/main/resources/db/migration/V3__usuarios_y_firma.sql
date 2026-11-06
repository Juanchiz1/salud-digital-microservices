CREATE TABLE usuario (
    id            UUID         PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    nombre        VARCHAR(150) NOT NULL,
    rol           VARCHAR(20)  NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('BACTERIOLOGO'))
);

-- Usuario de demostración. Contraseña: SaludDigital2026
INSERT INTO usuario (id, username, password_hash, nombre, rol) VALUES
    (gen_random_uuid(), 'bacteriologo', '$2b$10$JEE.xdPcU/7L6nRx7uLNneHanfUO2YOpz..M2Dtu/1B9EkoLXLgy2',
     'Marta Cárdenas', 'BACTERIOLOGO');

ALTER TABLE solicitud ADD COLUMN registrado_por VARCHAR(50);
