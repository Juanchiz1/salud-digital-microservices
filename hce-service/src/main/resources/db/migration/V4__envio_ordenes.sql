ALTER TABLE orden_examen
    ADD COLUMN intentos_envio  INTEGER      NOT NULL DEFAULT 0,
    ADD COLUMN ultimo_error    VARCHAR(500),
    ADD COLUMN proximo_intento TIMESTAMPTZ,
    ADD COLUMN enviada_en      TIMESTAMPTZ;

CREATE INDEX ix_orden_pendiente ON orden_examen (estado, proximo_intento);