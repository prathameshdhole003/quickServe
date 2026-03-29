CREATE SCHEMA IF NOT EXISTS restaurant_svc;

CREATE TABLE restaurant_svc.restaurants
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name             VARCHAR(255) NOT NULL,
    owner_id         UUID         NOT NULL,
    address          TEXT         NOT NULL,
    is_open          BOOLEAN      NOT NULL DEFAULT false,
    avg_prep_minutes INT          NOT NULL DEFAULT 30,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now()
);
