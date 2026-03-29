CREATE TABLE order_svc.order_items
(
    id           UUID PRIMARY KEY    DEFAULT gen_random_uuid(),
    order_id     UUID          NOT NULL REFERENCES order_svc.orders (id),
    menu_item_id UUID          NOT NULL,
    name         VARCHAR(255)  NOT NULL,
    quantity     INT           NOT NULL CHECK (quantity > 0),
    unit_price   NUMERIC(10, 2) NOT NULL
);

CREATE INDEX idx_order_items_order_id ON order_svc.order_items (order_id);
