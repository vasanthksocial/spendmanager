CREATE TABLE approval_matrix (
    id              BIGSERIAL PRIMARY KEY,
    category        VARCHAR(50) NOT NULL,
    min_amount      NUMERIC(18,2) NOT NULL,
    max_amount      NUMERIC(18,2), -- NULL means no upper bound
    approval_chain  JSONB NOT NULL, -- ordered array e.g. ["JUNIOR_ENGINEER","ASSISTANT_ENGINEER","EXECUTIVE_ENGINEER"]
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_amount_range CHECK (max_amount IS NULL OR max_amount > min_amount)
);

ALTER TABLE invoice ADD COLUMN category VARCHAR(50) NOT NULL DEFAULT 'GENERAL';
ALTER TABLE invoice ADD COLUMN approval_chain JSONB NOT NULL DEFAULT '["JUNIOR_ENGINEER","ASSISTANT_ENGINEER","EXECUTIVE_ENGINEER"]'::jsonb;
ALTER TABLE invoice ADD COLUMN stage_index INT NOT NULL DEFAULT 0;
