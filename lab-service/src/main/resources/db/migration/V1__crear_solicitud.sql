CREATE TABLE solicitud (
    id               UUID         PRIMARY KEY,
    orden_externa_id UUID         NOT NULL UNIQUE,
    tipo_documento   VARCHAR(2)   NOT NULL,
    numero_documento VARCHAR(20)  NOT NULL,
    paciente_nombre  VARCHAR(200) NOT NULL,
    sexo             VARCHAR(1)   NOT NULL,
    fecha_nacimiento DATE         NOT NULL,
    examen_codigo    VARCHAR(10)  NOT NULL,
    examen_nombre    VARCHAR(150) NOT NULL,
    estado           VARCHAR(20)  NOT NULL,
    conclusion       VARCHAR(500),
    recibida_en      TIMESTAMPTZ  NOT NULL,
    resultado_en     TIMESTAMPTZ,
    CONSTRAINT ck_solicitud_estado CHECK (estado IN ('RECIBIDA', 'RESULTADO_LISTO'))
);

CREATE INDEX ix_solicitud_estado ON solicitud (estado);

CREATE TABLE resultado_item (
    id               UUID         PRIMARY KEY,
    solicitud_id     UUID         NOT NULL REFERENCES solicitud (id),
    posicion         INTEGER      NOT NULL,
    analito          VARCHAR(100) NOT NULL,
    valor            VARCHAR(50)  NOT NULL,
    unidad           VARCHAR(30),
    rango_referencia VARCHAR(50),
    interpretacion   VARCHAR(1)   NOT NULL,
    CONSTRAINT ck_item_interpretacion CHECK (interpretacion IN ('N', 'H', 'L', 'A'))
);

CREATE INDEX ix_item_solicitud ON resultado_item (solicitud_id);