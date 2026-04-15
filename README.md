PG Management API : 

| Java, Spring Boot, Spring Security, JWT, MySQL, Multi-Tenancy, Swagger UI

Built a multi-tenant SaaS REST API serving multiple PG owners with complete data isolation using shared-schema multi-tenancy, enforced via JWT-based owner identity resolution (prevents IDOR attacks)

Automated monthly rent generation with Spring cron scheduler (runs 1st of every month at 6 AM) using idempotent record creation protected by application-level checks and a DB UNIQUE constraint on (tenant_id, month, year)

Implemented penalty calculator using BigDecimal arithmetic (5% of base rent) applied by a daily scheduler to overdue records, with point-in-time rent snapshots to preserve accurate financial history

Designed room occupancy as a transactional state machine — occupancy count and status (AVAILABLE/FULLY_OCCUPIED/UNDER_MAINTENANCE) updated atomically under @Transactional to prevent inconsistency on concurrent writes

Modelled complaint lifecycle as a Finite State Machine (OPEN → IN_PROGRESS → RESOLVED → CLOSED) with validated transitions and a scheduler that auto-closes RESOLVED complaints after 7 days
