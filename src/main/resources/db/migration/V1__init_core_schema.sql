-- Core schema for vertical slice: WorkOrder -> Invoice -> Approval (JE/AE/EE) -> FundDemand
-- Head Office / Division Office structure, statutory + interest passback tables are stubbed
-- for later milestones and intentionally left out of V1.

CREATE TABLE division_office (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(20) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE app_user (
    id                  BIGSERIAL PRIMARY KEY,
    full_name           VARCHAR(200) NOT NULL,
    email               VARCHAR(200) NOT NULL UNIQUE,
    role                VARCHAR(30) NOT NULL, -- JUNIOR_ENGINEER, ASSISTANT_ENGINEER, EXECUTIVE_ENGINEER, HEAD_OFFICE
    division_office_id  BIGINT REFERENCES division_office(id),
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_role CHECK (role IN ('JUNIOR_ENGINEER','ASSISTANT_ENGINEER','EXECUTIVE_ENGINEER','HEAD_OFFICE')),
    -- HEAD_OFFICE users are not tied to a division office
    CONSTRAINT chk_role_division CHECK (
        (role = 'HEAD_OFFICE' AND division_office_id IS NULL)
        OR (role <> 'HEAD_OFFICE' AND division_office_id IS NOT NULL)
    )
);

CREATE TABLE work_order (
    id                  BIGSERIAL PRIMARY KEY,
    work_order_no       VARCHAR(50) NOT NULL UNIQUE,
    division_office_id  BIGINT NOT NULL REFERENCES division_office(id),
    vendor_name         VARCHAR(200) NOT NULL,
    scheme_type         VARCHAR(30) NOT NULL, -- CENTRAL_SPONSORED, STATE_SPONSORED, OTHER
    contract_value      NUMERIC(18,2) NOT NULL CHECK (contract_value > 0),
    budget_allocated    NUMERIC(18,2) NOT NULL DEFAULT 0 CHECK (budget_allocated >= 0),
    claimed_to_date     NUMERIC(18,2) NOT NULL DEFAULT 0 CHECK (claimed_to_date >= 0),
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, CLOSED, CANCELLED
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_scheme_type CHECK (scheme_type IN ('CENTRAL_SPONSORED','STATE_SPONSORED','OTHER')),
    CONSTRAINT chk_wo_status CHECK (status IN ('ACTIVE','CLOSED','CANCELLED'))
);

CREATE TABLE invoice (
    id                  BIGSERIAL PRIMARY KEY,
    invoice_no          VARCHAR(50) NOT NULL,
    work_order_id       BIGINT NOT NULL REFERENCES work_order(id),
    division_office_id  BIGINT NOT NULL REFERENCES division_office(id),
    submitted_by        BIGINT NOT NULL REFERENCES app_user(id),
    invoice_date        DATE NOT NULL,
    claimed_amount      NUMERIC(18,2) NOT NULL CHECK (claimed_amount > 0),
    current_amount      NUMERIC(18,2) NOT NULL CHECK (current_amount > 0), -- may be adjusted by approvers
    invoice_file_url    TEXT NOT NULL,
    checklist_json       JSONB NOT NULL DEFAULT '{}'::jsonb,
    status              VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    current_stage       VARCHAR(30) NOT NULL DEFAULT 'JUNIOR_ENGINEER',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_invoice_status CHECK (status IN
        ('SUBMITTED','UNDER_REVIEW','APPROVED','REJECTED','FUND_DEMAND_RAISED','FUND_DEMAND_AUTHORIZED')),
    CONSTRAINT chk_invoice_stage CHECK (current_stage IN
        ('JUNIOR_ENGINEER','ASSISTANT_ENGINEER','EXECUTIVE_ENGINEER','COMPLETED'))
);

CREATE INDEX idx_invoice_work_order ON invoice(work_order_id);
CREATE INDEX idx_invoice_division_office ON invoice(division_office_id);
CREATE INDEX idx_invoice_status ON invoice(status);

-- Every approve / reject / modify action at every level - append only, never overwritten.
CREATE TABLE invoice_approval (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT NOT NULL REFERENCES invoice(id),
    stage           VARCHAR(30) NOT NULL, -- JUNIOR_ENGINEER, ASSISTANT_ENGINEER, EXECUTIVE_ENGINEER
    acted_by        BIGINT NOT NULL REFERENCES app_user(id),
    action          VARCHAR(20) NOT NULL, -- APPROVE, REJECT, MODIFY
    amount_before   NUMERIC(18,2) NOT NULL,
    amount_after    NUMERIC(18,2) NOT NULL,
    remarks         TEXT,
    acted_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_stage CHECK (stage IN ('JUNIOR_ENGINEER','ASSISTANT_ENGINEER','EXECUTIVE_ENGINEER')),
    CONSTRAINT chk_action CHECK (action IN ('APPROVE','REJECT','MODIFY'))
);

CREATE INDEX idx_invoice_approval_invoice ON invoice_approval(invoice_id);

CREATE TABLE fund_demand (
    id                  BIGSERIAL PRIMARY KEY,
    division_office_id  BIGINT NOT NULL REFERENCES division_office(id),
    raised_by           BIGINT NOT NULL REFERENCES app_user(id),
    total_amount        NUMERIC(18,2) NOT NULL CHECK (total_amount > 0),
    status              VARCHAR(20) NOT NULL DEFAULT 'RAISED', -- RAISED, AUTHORIZED, REJECTED
    raised_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    authorized_by       BIGINT REFERENCES app_user(id),
    authorized_at       TIMESTAMPTZ,
    remarks              TEXT,
    CONSTRAINT chk_fd_status CHECK (status IN ('RAISED','AUTHORIZED','REJECTED'))
);

CREATE TABLE fund_demand_invoice (
    fund_demand_id  BIGINT NOT NULL REFERENCES fund_demand(id),
    invoice_id      BIGINT NOT NULL REFERENCES invoice(id),
    PRIMARY KEY (fund_demand_id, invoice_id)
);
