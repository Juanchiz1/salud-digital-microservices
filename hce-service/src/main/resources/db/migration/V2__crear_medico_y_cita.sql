CREATE TABLE medico (
    id              UUID         PRIMARY KEY,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    especialidad    VARCHAR(80)  NOT NULL,
    registro_medico VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE cita (
    id          UUID         PRIMARY KEY,
    paciente_id UUID         NOT NULL REFERENCES paciente (id),
    medico_id   UUID         NOT NULL REFERENCES medico (id),
    fecha_hora  TIMESTAMPTZ  NOT NULL,
    motivo      VARCHAR(255) NOT NULL,
    estado      VARCHAR(15)  NOT NULL,
    creado_en   TIMESTAMPTZ  NOT NULL,
    CONSTRAINT ck_cita_estado CHECK (estado IN ('PROGRAMADA', 'ATENDIDA', 'CANCELADA'))
);

CREATE UNIQUE INDEX uk_cita_medico_horario
    ON cita (medico_id, fecha_hora)
    WHERE estado <> 'CANCELADA';

CREATE INDEX ix_cita_paciente ON cita (paciente_id);

INSERT INTO medico (id, nombres, apellidos, especialidad, registro_medico) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Andrés', 'Gómez Ortiz', 'Medicina general', 'RM-23001'),
    ('22222222-2222-2222-2222-222222222222', 'Carolina', 'Díaz Mejía', 'Medicina interna', 'RM-23002');