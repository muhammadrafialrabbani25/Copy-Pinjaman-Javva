# 📋 P2P Lending Platform - Masterplan
**Sprint 1 Minggu | Tim 5 Orang | Java + DDD + TDD**

---

## 1. Scope & Tech Stack

### 1.1 Fitur yang Dikembangkan

#### Borrower (Peminjam)
- Pendaftaran Borrower (nama, KTP, gaji)
- Pengajuan Loan (PENDING → VERIFIED)
- Verifikasi Data KTP (strict check)
- Pemberian Credit Score & Limit
- Masuk List Loan (deadline 5 hari funding)
- Pencairan Dana (jika fully funded)

#### Lender (Pemberi Pinjaman)
- Pendaftaran Lender (nama, saldo)
- Top Up Saldo
- Pilih Loan dari List
- Investasi Minimum 20% dari Loan Amount
- Notifikasi saat fully funded

#### Core Logic
- Validasi Limit Peminjaman (3x gaji)
- Perhitungan Credit Score (min 600)
- Perhitungan Cicilan Bulanan
- State Transition: PENDING → VERIFIED → FUNDING → FUNDED → DISBURSED
- Automatic Approval jika fully funded
- Payment Schedule Generation

### 1.2 Tech Stack

| Item | Detail |
|------|--------|
| **Language** | Java 11+ |
| **Build** | Maven |
| **Testing** | JUnit 5, Mockito 4+ |
| **Architecture** | DDD (Domain-Driven Design) |
| **Data Storage** | In-Memory HashMap (Transient) |
| **Design Patterns** | 5 GoF Design Patterns |
| **Version Control** | Git (per feature branch) |

### 1.3 Prinsip TDD

Setiap fitur mengikuti siklus:
1. **RED** → Tulis test, lihat GAGAL
2. **GREEN** → Tulis code minimal agar test PASS
3. **REFACTOR** → Improve struktur, maintain GREEN

---

## 2. Struktur Direktori DDD

```
pinjamanjavva/
├── pom.xml
├── src/main/java/com/p2plending/
│   ├── domain/                      # Business Logic Layer
│   │   ├── borrower/
│   │   │   ├── entity/
│   │   │   │   ├── Borrower.java
│   │   │   │   ├── LoanApplication.java
│   │   │   │   ├── KTP.java
│   │   │   │   └── Payment.java
│   │   │   ├── aggregate/
│   │   │   │   └── LoanAggregate.java
│   │   │   ├── service/
│   │   │   │   ├── LoanApprovalService.java
│   │   │   │   └── PaymentScheduleService.java
│   │   │   ├── repository/
│   │   │   │   └── BorrowerRepository.java (Interface)
│   │   │   └── event/
│   │   │       └── LoanEvent.java
│   │   │
│   │   ├── lender/
│   │   │   ├── entity/
│   │   │   │   ├── Lender.java
│   │   │   │   └── Investment.java
│   │   │   ├── aggregate/
│   │   │   │   └── LenderAggregate.java
│   │   │   ├── service/
│   │   │   │   └── InvestmentService.java
│   │   │   ├── repository/
│   │   │   │   └── LenderRepository.java (Interface)
│   │   │   └── event/
│   │   │       └── InvestmentEvent.java
│   │   │
│   │   └── shared/
│   │       ├── Money.java
│   │       ├── LoanStatus.java
│   │       ├── Tenor.java
│   │       └── DomainEventPublisher.java
│   │
│   ├── application/                 # Use Cases & Workflows
│   │   ├── borrower/
│   │   │   ├── usecase/
│   │   │   │   ├── RegisterBorrowerUseCase.java
│   │   │   │   ├── ApplyLoanUseCase.java
│   │   │   │   ├── GetLoanDetailsUseCase.java
│   │   │   │   └── GetLoanListUseCase.java
│   │   │   ├── service/
│   │   │   │   └── BorrowerApplicationService.java
│   │   │   └── dto/
│   │   │       ├── RegisterBorrowerCommand.java
│   │   │       ├── ApplyLoanCommand.java
│   │   │       ├── LoanDTO.java
│   │   │       └── BorrowerDTO.java
│   │   │
│   │   ├── lender/
│   │   │   ├── usecase/
│   │   │   │   ├── RegisterLenderUseCase.java
│   │   │   │   ├── TopUpSaldoUseCase.java
│   │   │   │   ├── InvestLoanUseCase.java
│   │   │   │   └── GetAvailableLoansUseCase.java
│   │   │   ├── service/
│   │   │   │   └── LenderApplicationService.java
│   │   │   └── dto/
│   │   │       ├── RegisterLenderCommand.java
│   │   │       ├── InvestCommand.java
│   │   │       ├── LenderDTO.java
│   │   │       └── AvailableLoanDTO.java
│   │   │
│   │   └── shared/
│   │       └── ApproveLoanUseCase.java
│   │
│   ├── infrastructure/              # Technical Implementation
│   │   ├── persistence/
│   │   │   ├── SharedStorage.java (Singleton)
│   │   │   ├── InMemoryBorrowerRepository.java
│   │   │   ├── InMemoryLenderRepository.java
│   │   │   ├── InMemoryLoanRepository.java
│   │   │   └── InMemoryInvestmentRepository.java
│   │   │
│   │   └── event/
│   │       └── SimpleEventBus.java
│   │
│   └── interfaces/                  # Entry Points
│       └── cli/
│           └── LendingApp.java
│
└── src/test/java/com/p2plending/
    ├── domain/
    │   ├── borrower/
    │   │   ├── entity/ (Test files for entities)
    │   │   ├── service/ (Test files for services)
    │   │   └── aggregate/ (Test files for aggregates)
    │   │
    │   ├── lender/
    │   │   ├── entity/ (Test files for entities)
    │   │   ├── service/ (Test files for services)
    │   │   └── aggregate/ (Test files for aggregates)
    │   │
    │   └── shared/
    │       └── (Test files for shared classes)
    │
    └── application/
        ├── borrower/
        │   └── (Test files for use cases)
        ├── lender/
        │   └── (Test files for use cases)
        └── shared/
            └── (Test files for cross-context use cases)
```

---

## 3. 5 GoF Design Patterns

| Pattern | File | Tujuan |
|---------|------|--------|
| **State** | `LoanStatus.java` + `LoanAggregate.java` | Mengelola state transition loan (PENDING → VERIFIED → FUNDING → FUNDED → DISBURSED) |
| **Strategy** | `PaymentScheduleService.java` | Perhitungan cicilan dengan interface yang extensible |
| **Observer** | `DomainEventPublisher.java` + `SimpleEventBus.java` | Decoupling domain events (LoanApproved, FundingCompleted) |
| **Factory** | `LoanAggregate.create()` | Pembuatan Loan dengan validasi bisnis terpadu |
| **Repository** | `*Repository.java` interfaces | Abstraksi data access untuk testing & decoupling (HashMap only, tidak akan ganti ke DB) |

**Penjelasan Implementasi:**
- **State:** Enum `LoanStatus` + method `canTransitionTo()` untuk validasi
- **Strategy:** Interface `InterestCalculationStrategy` dengan implementasi `FixedInterestCalculation`
- **Observer:** `DomainEventPublisher` publish, `SimpleEventBus` subscribe & notify
- **Factory:** Static method `create()` dengan validasi bisnis encapsulated
- **Repository:** Interface di domain untuk Mockito testing, implementasi HashMap di infrastructure (final, tidak ada DB migration)

---

## 4. Pembagian Tugas & Timeline

### � Quick Reference untuk AI & Team

#### Dependency Chain (Urutan Eksekusi)
```
IMAN (Hari 1-2)
  ├─ 9 Files (Entities + VO)
  └─ ✅ Push: feature/domain-entities
      │
      └──→ KEMAL (Hari 2-3) ⏳ WAITS FOR
           ├─ 7 Files (Aggregates + Services)
           └─ ✅ Push: feature/domain-aggregates-services
               │
               └──→ DANANG (Hari 3-4) ⏳ WAITS FOR
                    ├─ 11 Files (Use Cases + DTOs)
                    └─ ✅ Push: feature/application-layer
                        │
                        └──→ JAYA (Hari 4-5) ⏳ WAITS FOR
                             ├─ 6 Files (Repositories + EventBus)
                             └─ ✅ Push: feature/infrastructure-layer
                                 │
                                 └──→ RAFI (Hari 5-7) ⏳ WAITS FOR
                                      ├─ 2 Files (CLI + Integration Tests)
                                      └─ ✅ Push: feature/presentation-integration
```

#### Quick Overview Table

| Person | Periode | Files | Layer | Dependencies | Branch |
|--------|---------|-------|-------|--------------|--------|
| **IMAN** | Hari 1-2 | 9 | Domain (Entities) | None | `feature/domain-entities` |
| **KEMAL** | Hari 2-3 | 7 | Domain (Agg+Svc) | ← Iman | `feature/domain-aggregates-services` |
| **DANANG** | Hari 3-4 | 11 | Application | ← Kemal | `feature/application-layer` |
| **JAYA** | Hari 4-5 | 6 | Infrastructure | ← Danang | `feature/infrastructure-layer` |
| **RAFI** | Hari 5-7 | 2 | Interfaces+Tests | ← Jaya | `feature/presentation-integration` |

**Total: 35 Java files + Full test coverage**

---

### �👤 IMAN: Domain Layer - Entities & Value Objects

**Durasi:** Hari 1-2  
**Branch:** `feature/domain-entities`  
**Output:** 9 Java files  
**Unblocks:** ➜ ENABLES Kemal (feature/domain-aggregates-services)

**Deliverables:**

Borrower Domain (4 files):
1. `domain/borrower/entity/Borrower.java`
2. `domain/borrower/entity/LoanApplication.java`
3. `domain/borrower/entity/KTP.java` (Value Object - immutable)
4. `domain/borrower/entity/Payment.java` (Value Object)

Lender Domain (2 files):
5. `domain/lender/entity/Lender.java`
6. `domain/lender/entity/Investment.java`

Shared Domain (3 files):
7. `domain/shared/Money.java`
8. `domain/shared/Tenor.java`
9. `domain/shared/LoanStatus.java` (Enum)

**TDD Checklist:**
- [ ] Hari 1 Pagi: `KTPTest.java` → `KTP.java` (immutability, equals)
- [ ] Hari 1 Sore: `MoneyTest.java`, `TenorTest.java` → implementasi
- [ ] Hari 2 Pagi: `BorrowerTest.java`, `LenderTest.java` → implementasi
- [ ] Hari 2 Sore: `LoanApplicationTest.java`, `InvestmentTest.java`, `PaymentTest.java`
- [ ] Push ke branch

---

### 👤 KEMAL: Domain Layer - Aggregates & Domain Services

**Durasi:** Hari 2-3 (depends on Iman)  
**Branch:** `feature/domain-aggregates-services`  
**Dependency:** ⬅️ WAIT FOR `feature/domain-entities` (from Iman)  
**Output:** 7 Java files  
**Unblocks:** ➜ ENABLES Danang (feature/application-layer)

**Deliverables:**

Borrower Aggregates (1 file):
1. `domain/borrower/aggregate/LoanAggregate.java` (Aggregate Root with State Pattern)

Borrower Services (2 files):
2. `domain/borrower/service/LoanApprovalService.java` (verifyCreditScore, calculateLoanLimit, verifyKTP)
3. `domain/borrower/service/PaymentScheduleService.java` (Strategy Pattern for interest calculation)

Lender Aggregates (1 file):
4. `domain/lender/aggregate/LenderAggregate.java`

Lender Services (1 file):
5. `domain/lender/service/InvestmentService.java` (validateMinimumInvestment)

Shared Domain (2 files):
6. `domain/shared/DomainEventPublisher.java` (Observer Pattern - pub-sub)
7. `domain/shared/LoanStatus.java` (extended with state transition validation)

**TDD Checklist:**
- [ ] Hari 2 Pagi: `LoanApprovalServiceTest.java` → impl
- [ ] Hari 2 Sore: `PaymentScheduleServiceTest.java`, `InvestmentServiceTest.java` → impl
- [ ] Hari 3 Pagi: `LoanAggregateTest.java`, `LenderAggregateTest.java` → impl
- [ ] Hari 3 Sore: `DomainEventPublisherTest.java` → impl
- [ ] Push ke branch

---

### 👤 DANANG: Application Layer - Use Cases & DTOs

**Durasi:** Hari 3-4 (depends on Kemal)  
**Branch:** `feature/application-layer`  
**Dependency:** ⬅️ WAIT FOR `feature/domain-aggregates-services` (from Kemal)  
**Output:** 11 Java files + Mockito-heavy testing  
**Unblocks:** ➜ ENABLES Jaya (feature/infrastructure-layer)

**Deliverables:**

Borrower Use Cases (4 files):
1. `application/borrower/usecase/RegisterBorrowerUseCase.java`
2. `application/borrower/usecase/ApplyLoanUseCase.java`
3. `application/borrower/usecase/GetLoanDetailsUseCase.java`
4. `application/borrower/usecase/GetLoanListUseCase.java`

Borrower DTO & Service (5 files):
5. `application/borrower/service/BorrowerApplicationService.java` (orchestrator)
6. `application/borrower/dto/RegisterBorrowerCommand.java`
7. `application/borrower/dto/ApplyLoanCommand.java`
8. `application/borrower/dto/LoanDTO.java`
9. `application/borrower/dto/BorrowerDTO.java`

Lender Use Cases & DTOs (4 files):
10. `application/lender/usecase/RegisterLenderUseCase.java`
11. `application/lender/usecase/TopUpSaldoUseCase.java`
12. `application/lender/usecase/InvestLoanUseCase.java` (Complex: state transition + event)
13. `application/lender/usecase/GetAvailableLoansUseCase.java`

Shared Layer (1 file):
14. `application/shared/ApproveLoanUseCase.java` (crosses Borrower & Lender context)

**Key:** Every test must mock Repository with @Mock/@InjectMocks!

**TDD Checklist:**
- [ ] Hari 3 Pagi: `RegisterBorrowerUseCaseTest.java` (mock repo) → impl
- [ ] Hari 3 Sore: `ApplyLoanUseCaseTest.java` → impl
- [ ] Hari 4 Pagi: Lender use cases → impl
- [ ] Hari 4 Sore: `InvestLoanUseCaseTest.java` (complex mocking) → impl
- [ ] Push ke branch

---

### 👤 JAYA: Infrastructure Layer & Repositories

**Durasi:** Hari 4-5 (depends on Danang)  
**Branch:** `feature/infrastructure-layer`  
**Dependency:** ⬅️ WAIT FOR `feature/application-layer` (from Danang)  
**Output:** 6 Java files  
**Unblocks:** ➜ ENABLES Rafi (feature/presentation-integration)

**Deliverables:**

Persistence Layer (5 files):
1. `infrastructure/persistence/SharedStorage.java` (Singleton - central HashMap store)
2. `infrastructure/persistence/InMemoryBorrowerRepository.java` (implements BorrowerRepository)
3. `infrastructure/persistence/InMemoryLoanRepository.java` (implements LoanRepository)
4. `infrastructure/persistence/InMemoryLenderRepository.java` (implements LenderRepository)
5. `infrastructure/persistence/InMemoryInvestmentRepository.java` (implements InvestmentRepository)

Event Layer (1 file):
6. `infrastructure/event/SimpleEventBus.java` (implements DomainEventPublisher with Observer pattern)

**TDD Checklist:**
- [ ] Hari 4 Pagi: `InMemoryBorrowerRepositoryTest.java` → impl
- [ ] Hari 4 Sore: Repository tests untuk Loan, Lender, Investment → impl
- [ ] Hari 5 Pagi: `SimpleEventBusTest.java` → impl
- [ ] Hari 5 Sore: Integration test (complete flow simulation)
- [ ] Push ke branch

---

### 👤 RAFI: Integration Testing & CLI

**Durasi:** Hari 5-7 (overlap + finalization)  
**Branch:** `feature/presentation-integration`  
**Dependency:** ⬅️ WAIT FOR `feature/infrastructure-layer` (from Jaya)  
**Output:** 2 Java files (1 CLI + 1 Integration Test)  
**Unblocks:** ➜ FINAL DELIVERY (merge ke main)

**Deliverables:**

Presentation Layer (1 file):
1. `interfaces/cli/LendingApp.java` (Main entry point with 8-menu CLI + manual DI wiring)

Integration Tests (1 file):
2. `test/integration/EndToEndFlowTest.java` (Complete flow: register → apply → invest → disburse)

**Scenario Test:**
1. Borrower "Sman" register (salary 10M)
2. Apply loan 30M, tenor 12
3. System verifies (credit score check)
4. Loan: PENDING → VERIFIED → FUNDING
5. Lender "Budi" register & top up 20M
6. Budi invest 6M (20% min)
7. Check full funding: total invested ≥ 30M?
8. Status: FUNDED → DISBURSED
9. Verify payment schedule: 12 monthly payments

**TDD Checklist:**
- [ ] Hari 5 Pagi: Merge all feature branches ke develop
- [ ] Hari 5-6: `EndToEndFlowTest.java` (real repos, no mocks)
- [ ] Hari 6-7: `LendingApp.java` + manual testing
- [ ] Push ke branch

---

## 5. Workflow Kolaborasi

### 5.1 Git Strategy

```bash
# Main branches
main                                # production-ready
develop                             # integration branch

# Feature branches (per person)
feature/domain-entities             (Iman)
feature/domain-aggregates-services  (Kemal)
feature/application-layer           (Danang)
feature/infrastructure-layer        (Jaya)
feature/presentation-integration    (Rafi)
```

**Workflow:**
1. Setiap orang: `git checkout -b feature/xxx`
2. Setiap hari: `git pull develop` & `git rebase` own branch
3. Hari 5: Pull request ke develop (code review)
4. Hari 7: Final merge develop → main

### 5.2 TDD Checklist

- [ ] 1. Tulis test case (RED)
- [ ] 2. Test gagal (verifikasi test valid)
- [ ] 3. Implementasi minimal (GREEN)
- [ ] 4. Test lulus 100%
- [ ] 5. Refactor jika perlu
- [ ] 6. Re-run test (GREEN tetap)
- [ ] 7. Code review self
- [ ] 8. Commit: `feat(domain): XYZ - test + impl`

### 5.3 Mockito Usage Pattern

```java
@ExtendWith(MockitoExtension.class)
class ApplyLoanUseCaseTest {
    
    @Mock
    BorrowerRepository borrowerRepository;
    
    @Mock
    LoanRepository loanRepository;
    
    @InjectMocks
    ApplyLoanUseCase useCase;
    
    @Test
    void shouldApplyLoanSuccessfully() {
        // GIVEN
        Borrower borrower = Borrower.create("sman", 10_000_000, ...);
        when(borrowerRepository.findById("sman"))
            .thenReturn(Optional.of(borrower));
        
        when(loanRepository.save(any(LoanApplication.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // WHEN
        ApplyLoanCommand cmd = new ApplyLoanCommand("sman", 30_000_000, 12);
        LoanDTO result = useCase.execute(cmd);
        
        // THEN
        assertThat(result.getStatus()).isEqualTo("VERIFIED");
        verify(borrowerRepository).findById("sman");
    }
}
```

### 5.4 Daily Standup (09:00)

Setiap hari 15 menit:
- **Iman:** "Done X, Today Y, Blocked by Z?"
- **Kemal:** "..."
- *dst*

**Focus Hari 3-4:** Review dependency antar layer untuk identify issue lebih cepat.

### 5.5 Definition of Done

**Per Task (daily):**
- ✓ Code written
- ✓ Unit test written & PASS (coverage > 80%)
- ✓ Code reviewed (self or peer)
- ✓ Mockito used for Repository (jika applicable)
- ✓ Commit pushed ke feature branch

**Per Module:**
- ✓ All tests PASS
- ✓ No compilation error
- ✓ Refactored
- ✓ Merged ke develop

**Per Project (Day 7):**
- ✓ All tests PASS (>85% coverage)
- ✓ CLI app runnable
- ✓ Integration test succeed
- ✓ Documentation complete
- ✓ Final merge develop → main

---

## 6. Timeline Ringkas

| Hari | Iman | Kemal | Danang | Jaya | Rafi |
|------|------|-------|--------|------|------|
| 1-2 | Entities + VO | - | - | - | Setup Maven + doc |
| 2-3 | Push | Aggregates + Services | - | - | Review |
| 3-4 | Review | Push | Use Cases + DTOs | - | Prepare |
| 4-5 | Assist | Assist | Push | Repositories + Bus | Review |
| 5-6 | Final test | Final test | Final test | Push | E2E + CLI |
| 7 | Merge | Merge | Merge | Merge | Final check |

---

## 7. Peringatan Kritis

### JANGAN LUPAKAN:

1. **SETIAP fitur dimulai dengan TEST, bukan code**
   - RED → GREEN → REFACTOR

2. **Repository INTERFACE di domain, IMPLEMENTASI di infrastructure**
   - Jangan mix-up letak file

3. **Use Case wajib mock Repository di test, gunakan Mockito @Mock**
   - Hindari akses real storage dalam unit test

4. **Domain layer TIDAK BOLEH import dari application/infrastructure**
   - Dependency: presentation → application → domain ← infrastructure

5. **Scope HANYA sampai pencairan + notifikasi**
   - TIDAK perlu: payment transaction, repayment, penalty, dll

6. **In-Memory HashMap FINAL (tidak akan ganti ke DB)**
   - Repository pattern hanya untuk testing & decoupling, bukan untuk future migration
   - HashMap cukup untuk sprint ini, fokus pada domain logic

7. **5 GoF Pattern WAJIB ada, tapi jangan force**
   - Natural placement is key

8. **Test setiap hari, jangan kumpul di hari terakhir**
   - TDD cycle harus konsisten setiap 1-2 jam coding

9. **Git commit SERING (min 1x per use case/entity)**
   - Avoid merge hell

10. **Deploy = main branch, verified dengan CLI**
    - No partial builds

---

## 8. Deliverable Akhir (Hari 7 EOD)

**Folder Structure:**
- ✓ `domain/` (entities, aggregates, services, repositories interface, events)
- ✓ `application/` (use cases, services, DTOs)
- ✓ `infrastructure/` (repositories impl, event bus)
- ✓ `interfaces/cli/` (LendingApp.java entry point)
- ✓ Test folder mirror main dengan full coverage

**Artifacts:**
- ✓ `pom.xml` (JUnit + Mockito)
- ✓ Semua class sesuai struktur
- ✓ Semua test PASS
- ✓ Git history clean (meaningful commits)
- ✓ `masterplan.md` updated
- ✓ `ARCHITECTURE.md` (detailed explanation)

**Demo:**
- ✓ CLI berjalan: register → apply → invest → disburse
- ✓ Test results (coverage > 85%)
- ✓ 5 pattern placement explained
- ✓ DDD layers explained

---

## 9. Backup: Hubungi Dosen

Jika ada blocker:
- **Architectural block:** Konsultasi dosen → Kemal/Rafi coordinate
- **Scope creep:** Dosen approval → freeze scope
- **Time pressure:** Prioritize core flow (register, apply, invest, disburse)

---

## Key Success Factors

1. **Parallelism** → 5 orang bekerja 5 task berbeda → selesai dalam 1 minggu
2. **Clear dependency** → Layer per layer, interface-first design
3. **TDD consistency** → Test tulis dulu, code mengikuti
4. **Mockito mastery** → Repository mock di setiap use case test
5. **Communication** → Daily standup, issue raising cepat

---

**Good luck, tim! 🚀**
