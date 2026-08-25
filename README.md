# Spend Management Platform — Vertical Slice 1

Java 21 / Spring Boot 3 backend implementing the first vertical slice of the
centralized vendor payment platform:

**Work Order → Invoice submission → JE/AE/EE approval (with amount
adjustment at every level) → Fund Demand (Division Office → Head Office)**

Everything else discussed (bank integration, statutory deductions, interest
passback, salary/pension/PF categories, budget/demand app integration) is
intentionally out of scope for this slice — the schema and service layer are
built so those slot in later without a redesign (see "What's next" below).

## Stack
- Java 21, Spring Boot 3.3 (Web, Data JPA, Validation)
- PostgreSQL via Supabase
- Flyway for schema migrations (schema is managed by Flyway, not Hibernate
  `ddl-auto`, which is set to `validate` only)
- Deployed to Render via Docker

## Project layout
```
src/main/java/com/dept/spendmgmt/
  model/        JPA entities
  repository/   Spring Data repositories
  service/      Business logic (InvoiceService, FundDemandService)
  controller/   REST endpoints
  dto/          Request payloads
  config/       Global exception handling
src/main/resources/
  application.yml
  db/migration/V1__init_core_schema.sql
```

## Core business rules implemented
- **Partial/progressive billing**: `work_order.claimed_to_date` tracks
  cumulative claims; submission is rejected if it would exceed
  `contract_value`. The claim is reserved at submission time (not just at
  final approval) so two invoices can't over-claim the same balance.
- **Adjustment at every approval level**: each JE/AE/EE action is logged as
  an append-only `invoice_approval` row (`APPROVE` / `REJECT` / `MODIFY`),
  capturing `amount_before` and `amount_after`. Nothing is overwritten —
  full audit trail. An approver can adjust the amount and approve in the
  same action, or adjust-only (`MODIFY`) and leave it for themselves to
  approve/reject afterwards.
- **Sequential 3-tier approval**: JE → AE → EE, fixed for this slice. The
  configurable-per-category approval matrix discussed earlier is not yet
  built — `UserRole.next()` is the one place that encodes the chain, so
  it's a contained change when that becomes configurable.
- **Fund demand**: a Division Office bundles one or more `APPROVED`
  invoices into a `FundDemand`; Head Office authorizes or rejects it as a
  single action. Authorization is the hook point for the future bank
  transfer step.

## Running locally
1. Create a Supabase project (or any Postgres 14+ instance).
2. Set environment variables (or a local `.env` loaded by your shell):
   ```
   DATABASE_URL=jdbc:postgresql://<host>:5432/postgres
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=<your-password>
   ```
   Use Supabase's **direct connection** (port 5432), not the pgbouncer
   pooler (6543), for now — Flyway and Hibernate work more predictably
   against it. Switch to the pooler later once you're running multiple
   instances.
3. `mvn spring-boot:run` — Flyway will apply `V1__init_core_schema.sql`
   automatically on startup.

## API (v1)
- `POST /api/invoices` — JE submits an invoice
- `POST /api/invoices/{id}/actions` — JE/AE/EE approve, reject, or modify
- `GET  /api/invoices/{id}/history` — full approval audit trail
- `GET  /api/invoices/pending?divisionOfficeId=&role=` — queue for a given
  approver role at a division office
- `POST /api/fund-demands` — Division Office raises a demand from approved
  invoices
- `POST /api/fund-demands/{id}/decision` — Head Office authorizes/rejects

There's no auth layer yet — every request carries the acting user's ID
explicitly (`actedByUserId`, `submittedByUserId`, etc.) and the service
layer checks that user's role/division office against the invoice. Wire
this up to real auth (Supabase Auth is the natural fit) before this goes
anywhere near production.

## Deploying to Render
1. Push this repo to GitHub.
2. In Render: New → Web Service → connect the repo → it will pick up
   `render.yaml` and the `Dockerfile`.
3. Set `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` in the
   Render dashboard (marked `sync: false` in render.yaml, so they're not
   committed).

## What's next (not in this slice)
- Approval matrix as configurable data (category/threshold → approver
  chain) instead of the fixed JE→AE→EE `next()` chain
- `AccountMapping` config + GL entry generation on fund demand
  authorization
- Bank transfer execution (this is the seam: `FundDemand.status =
  AUTHORIZED` is already the trigger point)
- Statutory deduction + remittance tracking
- Interest passback ledger
- Salary / Pension / PF as additional payment categories
- Auth (Supabase Auth) and role-based access control on the endpoints
- Submission checklist as configurable items, not a free-form JSON blob
