ALTER TABLE orden_examen
    ADD COLUMN conclusion            VARCHAR(500),
    ADD COLUMN resultado_emitido_en  TIMESTAMPTZ,
    ADD COLUMN resultado_recibido_en TIMESTAMPTZ;

CREATE TABLE resultado_valor (
    id               UUID         PRIMARY KEY,
    orden_id         UUID         NOT NULL REFERENCES orden_examen (id),
    posicion         INTEGER      NOT NULL,
    analito          VARCHAR(100) NOT NULL,
    valor            VARCHAR(50)  NOT NULL,
    unidad           VARCHAR(30),
    rango_referencia VARCHAR(50),
    interpretacion   VARCHAR(1)   NOT NULL,
    CONSTRAINT ck_valor_interpretacion CHECK (interpretacion IN ('N', 'H', 'L', 'A'))
);

CREATE INDEX ix_valor_orden ON resultado_valor (orden_id);