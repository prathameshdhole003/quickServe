CREATE TABLE restaurant_svc.menu_items
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id UUID          NOT NULL REFERENCES restaurant_svc.restaurants (id),
    name          VARCHAR(255)  NOT NULL,
    description   TEXT,
    price         NUMERIC(10, 2) NOT NULL,
    is_available  BOOLEAN       NOT NULL DEFAULT true
);

CREATE INDEX idx_menu_items_restaurant ON restaurant_svc.menu_items (restaurant_id);
