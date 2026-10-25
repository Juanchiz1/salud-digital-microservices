ALTER TABLE solicitud
    ADD COLUMN intentos_notificacion INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN ultimo_error          VARCHAR(500),
    ADD COLUMN proximo_intento       TIMESTAMPTZ,
    ADD COLUMN notificada_en         TIMESTAMPTZ;

CREATE INDEX ix_solicitud_notificacion ON solicitud (estado, notificada_en, proximo_intento);