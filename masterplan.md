# 📋 P2P Lending Platform - Masterplan
**Sprint 1 Minggu | Tim 5 Orang | Java 11+ | Maven | DDD + TDD**

---

## 1. Features

### Borrower
- Register (credit score self-declare: 1-1000, min 600)
- Apply loan (tenor: 1/3/6/12 months)
- Loan limit = 3x salary
- Cancel loan (if ≥20% invested → counter +1)
- Block 4 bulan after 3x cancel

### Lender
- Register + initial balance
- Top up (2% admin fee deducted)
- Invest (min 20% of loan)
- Get refund when cancellation

### Business Rules
- Loan amount ≤ 3x salary
- Credit score ≥ 600
- Min investment 20%
- Funding deadline 5 hari
- 2% admin fee (not returned on cancel)
- Refund full amount to lenders on cancel
- States: PENDING→VERIFIED→FUNDING→FUNDED→DISBURSED + CANCELLED + EXPIRED_FUNDING

---

## 2. Tech Stack

| Item | Detail |
|------|--------|
| Language | Java 11+ |
| Build | Maven |
| Testing | JUnit 5 + Mockito 4+ |
| Architecture | DDD (4 layers) |
| Storage | In-Memory HashMap |
| Design Patterns | 5 GoF |

---

## 3. 5 GoF Patterns

| Pattern | File | Purpose |
|---------|------|---------|
| **State** | LoanStatus + LoanAggregate | Manage state transitions |
| **Factory** | LoanAggregate.create() | Encapsulate creation + validation |
| **Strategy** | PaymentScheduleService | Extensible interest calculation |
| **Observer** | DomainEventPublisher + SimpleEventBus | Decouple events |
| **Repository** | *Repository interfaces | Abstract data access |

---

## 4. DDD Structure

```
src/main/java/com/p2plending/
├── domain/
│   ├── borrower/ (entity, aggregate, service, repository interface)
│   ├── lender/ (entity, aggregate, service, repository interface)
│   └── shared/ (Money, LoanStatus, Tenor, DomainEventPublisher)
├── application/
│   ├── borrower/ (use cases, DTOs)
│   ├── lender/ (use cases, DTOs)
│   └── shared/
├── infrastructure/
│   ├── persistence/ (repositories implementation, SharedStorage)
│   └── event/ (SimpleEventBus)
└── interfaces/
    └── cli/ (LendingApp.java)

src/test/java/com/p2plending/
├── domain/ (unit tests for entities, aggregates, services)
└── application/ (unit tests for use cases with Mockito mocks)
```

---

## 5. Team Tasks

| Person | Hari | Files | Layer | Depend |
|--------|------|-------|-------|--------|
| **IMAN** | 1-2 | 9 | Domain Entities | None |
| **KEMAL** | 2-3 | 8 | Domain Services/Agg | ← Iman |
| **DANANG** | 3-4 | 12 | Application | ← Kemal |
| **JAYA** | 4-5 | 6 | Infrastructure | ← Danang |
| **RAFI** | 5-7 | 2 | Interfaces + E2E | ← Jaya |

**Total: 37 Java files | Execution: Sequential pipeline**

---

## 6. IMAN: Domain Entities (Hari 1-2)

**9 Files:**

Borrower (4):
- Borrower.java
- LoanApplication.java
- KTP.java (immutable, 16-digit format)
- Payment.java

Lender (2):
- Lender.java
- Investment.java

Shared (3):
- Money.java (BigDecimal-based, immutable)
- Tenor.java (enum: 1,3,6,12)
- LoanStatus.java (enum: 7 states)

**Key Fields:**
- Borrower: id, nama, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore, cancellationCount, lastBlockedDate
- LoanApplication: id, borrowerId, amount, tenor, status, createdDate, minInvestedPercentageReached, cancelledDate
- Investment: id, lenderId, loanId, amount, status

**TDD Checklist:**
- [ ] Start with KTP (immutable, format validation)
- [ ] Money & Tenor (VO with equals/hashCode)
- [ ] Borrower, Lender entities
- [ ] LoanApplication, Investment, Payment
- [ ] LoanStatus enum
- [ ] Push: feature/domain-entities

---

## 7. KEMAL: Domain Services/Aggregates (Hari 2-3)

**8 Files:**

Borrower (4):
- LoanAggregate.java (ROOT - STATE + FACTORY)
- LoanApprovalService.java (verify score, calc limit)
- PaymentScheduleService.java (STRATEGY)
- LoanCancellationService.java (cancel, refund, counter, block)

Lender (2):
- LenderAggregate.java (ROOT)
- InvestmentService.java (validate min 20%)

Shared (2):
- DomainEventPublisher.java (OBSERVER interface)
- LoanStatus.java (canTransitionTo validation)

**Key Methods:**
- LoanAggregate.create(): Validate & init loan
- LoanApprovalService: verifyCreditScore, calculateLoanLimit, verifyKTP
- LoanCancellationService: cancelLoan, isBlockedFromApplying, refundInvestment
- InvestmentService: validateMinimumInvestment

**Understand:**
- **Aggregate Root:** LoanAggregate manages internal entities (Investment, Payment)
- **Service:** Stateless operations across aggregates
- **State Pattern:** LoanStatus enum with valid transitions

**TDD Checklist:**
- [ ] LoanStatus transitions validation
- [ ] LoanApprovalService (score ≥600, limit calc)
- [ ] PaymentScheduleService (interest calc)
- [ ] LoanCancellationService (refund logic)
- [ ] LoanAggregate & LenderAggregate
- [ ] DomainEventPublisher interface
- [ ] Push: feature/domain-aggregates-services

---

## 8. DANANG: Application Layer (Hari 3-4)

**12 Files + Mockito:**

Borrower (5 use cases + 6 DTOs):
- RegisterBorrowerUseCase + Command/DTO
- ApplyLoanUseCase + Command/DTO (check block period)
- CancelLoanUseCase + Command (NEW)
- GetLoanDetailsUseCase
- GetLoanListUseCase (display cancellationCount)

Lender (4 use cases + DTOs):
- RegisterLenderUseCase + Command/DTO
- TopUpSaldoUseCase (calc 2% fee)
- InvestLoanUseCase (complex: state + event)
- GetAvailableLoansUseCase

Shared:
- ApproveLoanUseCase

**Mockito Pattern:**
- @Mock BorrowerRepository
- @Mock LoanRepository
- @InjectMocks ApplyLoanUseCase
- when(repo.findById(...)).thenReturn(...)
- verify(repo).save(any(...))

**TDD Checklist:**
- [ ] RegisterBorrowerUseCase (test + mock repo)
- [ ] ApplyLoanUseCase (check block, mock repos)
- [ ] CancelLoanUseCase (verify refund logic)
- [ ] Lender use cases (TopUp, Invest)
- [ ] Push: feature/application-layer

---

## 9. JAYA: Infrastructure (Hari 4-5)

**6 Files:**

Repositories (5):
- SharedStorage.java (Singleton HashMap)
- InMemoryBorrowerRepository.java
- InMemoryLoanRepository.java
- InMemoryLenderRepository.java
- InMemoryInvestmentRepository.java

Event:
- SimpleEventBus.java (implements DomainEventPublisher)

**TDD Checklist:**
- [ ] Repositories (CRUD operations, find by ID)
- [ ] SimpleEventBus (subscribe, publish)
- [ ] Integration test (complete flow)
- [ ] Push: feature/infrastructure-layer

---

## 10. RAFI: Interfaces + E2E (Hari 5-7)

**2 Files - Hardcoded Demo:**

**LendingApp.java:**
- Manual DI (no Spring)
- 3 scenarios hardcoded:
  1. Happy path: IMAN apply 30M → BUDI invest 6M
  2. Cancellation: IMAN cancel → BUDI refund → counter = 1
  3. Expired: KEMAL apply 15M → 6 days → EXPIRED_FUNDING
- Console output with emoji + details

**EndToEndFlowTest.java:**
- Test all 3 scenarios
- No mocks (real repositories)
- Verify: refund amount, counter increment, status transitions

**TDD Checklist:**
- [ ] Write test cases (RED)
- [ ] Hardcoded scenarios (GREEN)
- [ ] Verify output format
- [ ] All edge cases covered
- [ ] Push: feature/presentation-integration

---

## 11. Git Strategy

```
main (production)
  ↑ merge Day 7
develop (integration)
  ↑ PR Day 5
feature/domain-entities (Iman)
feature/domain-aggregates-services (Kemal)
feature/application-layer (Danang)
feature/infrastructure-layer (Jaya)
feature/presentation-integration (Rafi)
```

---

## 12. TDD Workflow (Every File)

1. Write test (RED)
2. Verify test fails
3. Write code (GREEN)
4. Test passes
5. Refactor
6. Commit: `feat(layer): desc - test + impl`

---

## 13. Definition of Done

**Daily:**
- [ ] Code written + tested
- [ ] Coverage >80%
- [ ] Self-reviewed
- [ ] Committed

**Per Module:**
- [ ] All tests PASS
- [ ] No compilation errors
- [ ] Merged to develop

**Final (Day 7):**
- [ ] All tests PASS (>85% coverage)
- [ ] CLI runnable
- [ ] E2E test success
- [ ] Git history clean
- [ ] Final merge to main

---

## 14. Critical Rules ⚠️

1. **RED→GREEN→REFACTOR every time** (mandatory)
2. **Repository INTERFACE in domain, IMPL in infrastructure** (not reversed!)
3. **Domain NEVER imports application/infrastructure** (dependency down only)
4. **Mockito ONLY in application layer** (not in domain)
5. **HashMap is FINAL** (no DB migration)
6. **5 GoF patterns MUST exist** (natural placement)
7. **Test DAILY** (don't batch at end)
8. **Commit FREQUENT** (min 1x per file)
9. **Aggregate = object with state** (ROOT manages internals)
10. **Service = stateless operations** (functions across aggregates)

---

## 15. Timeline

| Hari | Iman | Kemal | Danang | Jaya | Rafi |
|------|------|-------|--------|------|------|
| 1-2 | Entities | - | - | - | Setup |
| 2-3 | Push | Services | - | - | Review |
| 3-4 | Review | Push | Use Cases | - | Prepare |
| 4-5 | Assist | Assist | Push | Infrastructure | Review |
| 5-6 | Test | Test | Test | Push | E2E+CLI |
| 7 | Merge | Merge | Merge | Merge | Final |

**Dependency: IMAN→KEMAL→DANANG→JAYA→RAFI (sequential)**

---

## 16. Execution Checklist

**Pre-Sprint:**
- [ ] Maven pom.xml setup (JUnit 5, Mockito)
- [ ] Git initialized (main, develop branches)
- [ ] Feature branches created (per person)

**During Sprint:**
- [ ] Daily standup (09:00, 15 min)
- [ ] Code review (daily)
- [ ] Tests GREEN (daily)
- [ ] Commits meaningful

**Post-Sprint:**
- [ ] All PRs merged
- [ ] E2E test success
- [ ] Coverage report >85%
- [ ] Documentation complete
- [ ] Final demo ready

---

**Good luck! 🚀**
