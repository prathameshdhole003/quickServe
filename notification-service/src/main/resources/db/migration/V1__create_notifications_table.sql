CREATE SCHEMA IF NOT EXISTS notification_svc;

CREATE TABLE notification_svc.notifications
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id UUID        NOT NULL,
    order_id     UUID        NOT NULL,
    type         VARCHAR(50) NOT NULL,
    content      TEXT        NOT NULL,
    status       VARCHAR(20) NOT NULL,
    sent_at      TIMESTAMPTZ,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_order_id     ON notification_svc.notifications (order_id);
CREATE INDEX idx_notifications_recipient_id ON notification_svc.notifications (recipient_id);
