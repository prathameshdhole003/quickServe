CREATE SCHEMA IF NOT EXISTS order_svc;

CREATE TABLE order_svc.orders
(
    id               UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    customer_id      UUID        NOT NULL,
    restaurant_id    UUID        NOT NULL,
    status           VARCHAR(30) NOT NULL,
    total_amount     NUMERIC(10, 2) NOT NULL,
    delivery_address TEXT        NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_customer_id   ON order_svc.orders (customer_id);
CREATE INDEX idx_orders_restaurant_id ON order_svc.orders (restaurant_id);
CREATE INDEX idx_orders_status        ON order_svc.orders (status);
