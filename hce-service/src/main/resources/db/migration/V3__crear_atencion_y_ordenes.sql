CREATE TABLE examen (
    codigo VARCHAR(10)  PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL
);

CREATE TABLE atencion (
    id                      UUID          PRIMARY KEY,
    cita_id                 UUID          NOT NULL UNIQUE REFERENCES cita (id),
    paciente_id             UUID          NOT NULL REFERENCES paciente (id),
    medico_id               UUID          NOT NULL REFERENCES medico (id),
    motivo_consulta         VARCHAR(500)  NOT NULL,
    notas                   VARCHAR(2000),
    diagnostico_codigo      VARCHAR(4)    NOT NULL,
    diagnostico_descripcion VARCHAR(255)  NOT NULL,
    fecha                   TIMESTAMPTZ   NOT NULL
);

CREATE INDEX ix_atencion_paciente ON atencion (paciente_id);

CREATE TABLE orden_examen (
    id            UUID        PRIMARY KEY,
    atencion_id   UUID        NOT NULL REFERENCES atencion (id),
    examen_codigo VARCHAR(10) NOT NULL REFERENCES examen (codigo),
    estado        VARCHAR(15) NOT NULL,
    creada_en     TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_orden_estado CHECK (estado IN ('CREADA', 'ENVIADA', 'COMPLETADA'))
);

CREATE INDEX ix_orden_atencion ON orden_examen (atencion_id);
CREATE INDEX ix_orden_estado ON orden_examen (estado);

INSERT INTO examen (codigo, nombre) VALUES
    ('902210', 'Hemograma IV, método automático'),
    ('903841', 'Glucosa en suero'),
    ('903818', 'Colesterol total'),
    ('903868', 'Triglicéridos'),
    ('907106', 'Uroanálisis');