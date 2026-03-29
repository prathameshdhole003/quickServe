CREATE TABLE order_svc.idempotency_records
(
    idempotency_key VARCHAR(255) PRIMARY KEY,
    response_body   TEXT        NOT NULL,
    status_code     INT         NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_idempotency_expires ON order_svc.idempotency_records (expires_at);
