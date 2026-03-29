CREATE SCHEMA IF NOT EXISTS delivery_svc;

CREATE TABLE delivery_svc.deliveries
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID        NOT NULL UNIQUE,
    driver_id  UUID,
    status     VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_deliveries_order_id  ON delivery_svc.deliveries (order_id);
CREATE INDEX idx_deliveries_driver_id ON delivery_svc.deliveries (driver_id);
CREATE INDEX idx_deliveries_status    ON delivery_svc.deliveries (status);
