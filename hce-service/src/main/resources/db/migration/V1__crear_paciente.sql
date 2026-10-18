CREATE TABLE paciente (
    id               UUID         PRIMARY KEY,
    tipo_documento   VARCHAR(2)   NOT NULL,
    numero_documento VARCHAR(20)  NOT NULL,
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    sexo             VARCHAR(1)   NOT NULL,
    telefono         VARCHAR(20),
    email            VARCHAR(120),
    creado_en        TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_paciente_documento UNIQUE (tipo_documento, numero_documento),
    CONSTRAINT ck_paciente_sexo CHECK (sexo IN ('H', 'M', 'I'))
);