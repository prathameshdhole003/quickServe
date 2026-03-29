CREATE TABLE delivery_svc.drivers
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(255) NOT NULL,
    phone        VARCHAR(30)  NOT NULL,
    is_available BOOLEAN      NOT NULL DEFAULT true,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_drivers_available ON delivery_svc.drivers (is_available);

-- Seed some test drivers so the flow works out of the box
INSERT INTO delivery_svc.drivers (name, phone, is_available)
VALUES ('Alice Johnson', '+1-555-0101', true),
       ('Bob Smith', '+1-555-0102', true),
       ('Carol White', '+1-555-0103', true);
