# 📋 P2P Lending Platform - Masterplan
**Sprint 1 Minggu | Tim 5 Orang | Java + DDD + TDD**

---

## 1. Scope & Tech Stack

### 1.1 Fitur yang Dikembangkan

#### Borrower (Peminjam)
- Pendaftaran Borrower (nama, no telepon, alamat, KTP, selfie, gaji, pekerjaan, riwayat pinjaman)
- Pengajuan Loan (PENDING → VERIFIED)
- Verifikasi Data KTP (strict check, nama & umur harus sesuai)
- Pemberian Credit Score & Limit
- Masuk List Loan (deadline 5 hari funding, status EXPIRED jika > 5 hari)
- Pencairan Dana (jika fully funded)
- Bisa cancel aplikasi (status CANCELLED) - jika sudah ada investasi ≥20%, increment cancellation counter
- Block periode 4 bulan setelah cancel ke-3x

#### Lender (Pemberi Pinjaman)
- Pendaftaran Lender (nama, no telepon, alamat, KTP, selfie, pekerjaan, saldo awal)
- Top Up Saldo (dengan admin fee 2%)
- Pilih Loan dari List
- Investasi Minimum 20% dari Loan Amount
- Notifikasi saat fully funded
- Bisa cancel investasi sebelum fully funded

#### Core Logic
- Validasi Limit Peminjaman (3x gaji)
- Perhitungan Credit Score (min 600)
- Perhitungan Cicilan Bulanan
- State Transition: PENDING → VERIFIED → FUNDING → FUNDED → DISBURSED
- State khusus: CANCELLED (borrower/lender cancel), EXPIRED_FUNDING (waiting > 5 hari tanpa full funding)
- Automatic Approval jika fully funded
- Payment Schedule Generation
- **Admin Fee:** 2% untuk lender saat top up saldo (tidak dikembalikan saat cancel)
- **Cancellation Rules:**
  - Hanya bisa cancel saat state: PENDING, VERIFIED, FUNDING (NOT FUNDED/DISBURSED)
  - Hanya dihitung sebagai "cancel" jika investasi sudah ≥20% dari loan amount
  - Setelah cancel 3x (dengan investasi ≥20%): borrower diblok 4 bulan dari apply baru
  - Refund: kembalikan full amount ke semua lender (admin fee 2% sudah diambil saat top up)

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
| **State** | `LoanStatus.java` + `LoanAggregate.java` | Mengelola state transition loan (PENDING → VERIFIED → FUNDING → FUNDED → DISBURSED, plus CANCELLED & EXPIRED_FUNDING) |
| **Strategy** | `PaymentScheduleService.java` | Perhitungan cicilan dengan interface yang extensible |
| **Observer** | `DomainEventPublisher.java` + `SimpleEventBus.java` | Decoupling domain events (LoanApproved, FundingCompleted) |
| **Factory** | `LoanAggregate.create()` | Pembuatan Loan dengan validasi bisnis terpadu |
| **Repository** | `*Repository.java` interfaces | Abstraksi data access untuk testing & decoupling (HashMap only, tidak akan ganti ke DB) |

**Penjelasan Implementasi:**
- **State:** Enum `LoanStatus` dengan values: PENDING, VERIFIED, FUNDING, FUNDED, DISBURSED, CANCELLED, EXPIRED_FUNDING
  - Valid transitions:
    - PENDING → VERIFIED (after KTP verification)
    - VERIFIED → FUNDING (entering funding period)
    - FUNDING → FUNDED (when fully invested ≥ loan amount)
    - FUNDING → EXPIRED_FUNDING (if > 5 days waiting)
    - FUNDED → DISBURSED (when all conditions met)
    - Any state → CANCELLED (if borrower/lender cancels)
  - Method: `canTransitionTo(nextStatus)` validates allowed transitions
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

**Total: 37 Java files + Full test coverage** (+2 files for cancellation feature)

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

**Field Specification:**

| Entity | Fields | Type | Notes |
|--------|--------|------|-------|
| **Borrower** | id, nama, noTelepon, alamat, ktp, selfie, gaji, pekerjaan, riwayatPinjaman, cancellationCount, lastBlockedDate | String/Money/Integer/LocalDateTime | cancellationCount incremented when cancel ≥20% funded; lastBlockedDate set after 3x cancel |
| **Lender** | id, nama, noTelepon, alamat, ktp, selfie, pekerjaan, saldo | String/Money | Saldo mutable (topUp) |
| **LoanApplication** | id, borrowerId, amount, tenor, creditScore, status, createdDate, minInvestedPercentageReached, cancelledDate | Long/Money/Enum/boolean/LocalDateTime | status = PENDING/VERIFIED/FUNDING/FUNDED/DISBURSED/CANCELLED/EXPIRED_FUNDING; minInvestedPercentageReached tracks if 20%+ invested |
| **KTP** | nomorKTP, nama, tanggalLahir | String/LocalDate | Format: 16 digit, non-null checks |
| **Investment** | id, lenderId, loanId, amount, status | Long/Money/Enum | status = ACTIVE/CANCELLED |
| **Payment** | id, loanId, noBulan, amount, dueDate, status | Long/Money/LocalDate | status = PENDING/PAID |
| **Money** | amount, currency | BigDecimal/String | Immutable VO with equals/hashCode |
| **Tenor** | months | Integer | Valid: 1, 3, 6, 12 |
| **LoanStatus** | Values | Enum | PENDING, VERIFIED, FUNDING, FUNDED, DISBURSED, CANCELLED, EXPIRED_FUNDING |

**TDD Checklist:**
- [ ] Hari 1 Pagi: `KTPTest.java` → `KTP.java` (immutability, equals, 16-digit format check)
- [ ] Hari 1 Sore: `MoneyTest.java`, `TenorTest.java` → implementasi (equals/hashCode, validation)
- [ ] Hari 2 Pagi: `BorrowerTest.java`, `LenderTest.java` → implementasi (fields sesuai table)
- [ ] Hari 2 Sore: `LoanApplicationTest.java`, `InvestmentTest.java`, `PaymentTest.java`, `LoanStatusTest.java`
- [ ] Push ke branch

---

### 👤 KEMAL: Domain Layer - Aggregates & Domain Services

**Durasi:** Hari 2-3 (depends on Iman)  
**Branch:** `feature/domain-aggregates-services`  
**Dependency:** ⬅️ WAIT FOR `feature/domain-entities` (from Iman)  
**Output:** 8 Java files (+1 LoanCancellationService)  
**Unblocks:** ➜ ENABLES Danang (feature/application-layer)

**Deliverables:**

Borrower Aggregates (1 file):
1. `domain/borrower/aggregate/LoanAggregate.java` (Aggregate Root with State Pattern)

Borrower Services (3 files):
2. `domain/borrower/service/LoanApprovalService.java` (verifyCreditScore, calculateLoanLimit, verifyKTP)
3. `domain/borrower/service/PaymentScheduleService.java` (Strategy Pattern for interest calculation)
4. `domain/borrower/service/LoanCancellationService.java` (NEW - handle cancellation logic, refund logic, counter increment, block period check)

Lender Aggregates (1 file):
5. `domain/lender/aggregate/LenderAggregate.java`

Lender Services (1 file):
6. `domain/lender/service/InvestmentService.java` (validateMinimumInvestment)

Shared Domain (2 files):
7. `domain/shared/DomainEventPublisher.java` (Observer Pattern - pub-sub)
8. `domain/shared/LoanStatus.java` (extended with state transition validation)

**TDD Checklist:**
- [ ] Hari 2 Pagi: `LoanStatusTest.java` → impl (state transition validation: PENDING→VERIFIED→FUNDING→FUNDED→DISBURSED, CANCELLED, EXPIRED_FUNDING)
- [ ] Hari 2 Pagi: `LoanApprovalServiceTest.java` → impl
- [ ] Hari 2 Sore: `PaymentScheduleServiceTest.java`, `InvestmentServiceTest.java`, `LoanCancellationServiceTest.java` → impl
- [ ] Hari 3 Pagi: `LoanAggregateTest.java`, `LenderAggregateTest.java` → impl
- [ ] Hari 3 Sore: `DomainEventPublisherTest.java` → impl
- [ ] Push ke branch

---

### 👤 DANANG: Application Layer - Use Cases & DTOs

**Durasi:** Hari 3-4 (depends on Kemal)  
**Branch:** `feature/application-layer`  
**Dependency:** ⬅️ WAIT FOR `feature/domain-aggregates-services` (from Kemal)  
**Output:** 12 Java files + Mockito-heavy testing (+1 CancelLoanUseCase)  
**Unblocks:** ➜ ENABLES Jaya (feature/infrastructure-layer)

**Deliverables:**

Borrower Use Cases (5 files):
1. `application/borrower/usecase/RegisterBorrowerUseCase.java`
2. `application/borrower/usecase/ApplyLoanUseCase.java` (check: borrower blocked? lastBlockedDate + 4 bulan)
3. `application/borrower/usecase/CancelLoanUseCase.java` (NEW - validate state, check 20% invested, refund, increment counter)
4. `application/borrower/usecase/GetLoanDetailsUseCase.java`
5. `application/borrower/usecase/GetLoanListUseCase.java` (display cancellationCount di borrower info)

Borrower DTO & Service (6 files):
6. `application/borrower/service/BorrowerApplicationService.java` (orchestrator)
7. `application/borrower/dto/RegisterBorrowerCommand.java`
8. `application/borrower/dto/ApplyLoanCommand.java`
9. `application/borrower/dto/CancelLoanCommand.java` (NEW - loanId, borrowerId, reason)
10. `application/borrower/dto/LoanDTO.java`
11. `application/borrower/dto/BorrowerDTO.java` (include cancellationCount)

Lender Use Cases & DTOs (4 files):
12. `application/lender/usecase/RegisterLenderUseCase.java`
13. `application/lender/usecase/TopUpSaldoUseCase.java` (kalkulasi 2% admin fee)
14. `application/lender/usecase/InvestLoanUseCase.java` (Complex: state transition + event)
15. `application/lender/usecase/GetAvailableLoansUseCase.java` (display borrower cancellationCount)

Shared Layer (1 file):
16. `application/shared/ApproveLoanUseCase.java` (crosses Borrower & Lender context)

**Key:** Every test must mock Repository with @Mock/@InjectMocks!

**TDD Checklist:**
- [ ] Hari 3 Pagi: `RegisterBorrowerUseCaseTest.java` (mock repo) → impl
- [ ] Hari 3 Sore: `ApplyLoanUseCaseTest.java` (check block period), `CancelLoanUseCaseTest.java` (check refund logic) → impl
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
5. Lender "Budi" register & top up 20M (bayar 2% = 400K, saldo = 19.6M)
6. Budi invest 6M (20% min) → loan now 20% funded
7. **CANCEL TEST:** Sman cancel loan → Budi refund 6M → saldo = 25.6M (19.6 + 6)
8. Verify: Sman.cancellationCount = 1
9. Verify: loan status = CANCELLED
10. Lender "Citra" register & top up 20M
11. Citra invest 6M (new loan attempt)
12. Full funding: total ≥ 30M?
13. Status: FUNDED → DISBURSED
14. Verify payment schedule: 12 monthly payments

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

## 7. Fee Structure

| Fee Type | Value | Actor | When | Notes |
|----------|-------|-------|------|-------|
| **Admin Fee** | 2% | Lender | Top Up Saldo | Dipotong dari amount yang di-top up, NOT refunded saat cancel |
| **Cancellation Penalty** | - | Borrower | After 3x cancel with ≥20% funded | 4 bulan block period dari apply baru |
| **Borrower Fee** | - | Borrower | - | TBD (belum dalam scope) |

**Contoh:**
- Lender top up Rp 10.000.000
- Admin fee 2% = Rp 200.000
- Saldo final = Rp 9.800.000

---

## 9. Loan Cancellation Feature

### **Business Rules:**

1. **Cancellable States:**
   - PENDING, VERIFIED, FUNDING (dapat dibatalkan)
   - FUNDED, DISBURSED (TIDAK dapat dibatalkan)

2. **Cancellation Count Trigger:**
   - Hanya dihitung jika investasi sudah ≥20% dari loan amount
   - Di bawah 20% = tidak increment counter

3. **Counter Limit & Blocking:**
   - Cancel 1x = normal
   - Cancel 2x = warning (display di borrower profile)
   - Cancel 3x = **BLOCKED 4 bulan** dari apply baru
   - Block period: `lastBlockedDate + 4 months`

4. **Refund Mechanism:**
   - Get semua investment untuk loan ini
   - Refund FULL amount ke setiap lender
   - Admin fee 2% sudah diambil saat top up (tidak dikembalikan)
   - Update investment.status = CANCELLED
   - Update loan.status = CANCELLED

5. **Display in CLI:**
   - Loan list: tampilkan `cancellationCount` di borrower info
   - Example: "Borrower: Sman (Cancelled 2x)"
   - Prevent apply jika dalam block period

### **Example Scenario:**

```
Transaction 1:
- Borrower Sman apply 30M
- Lender Budi invest 6M (20%)
- Sman CANCEL → Budi refund 6M
- Sman.cancellationCount = 1

Transaction 2:
- Sman apply 30M again
- Lender Citra invest 8M (26%)
- Sman CANCEL → Citra refund 8M
- Sman.cancellationCount = 2

Transaction 3:
- Sman apply 30M again
- Lender Dina invest 10M (33%)
- Sman CANCEL → Dina refund 10M
- Sman.cancellationCount = 3
- Sman.lastBlockedDate = NOW
- Next apply attempt blocked untuk 4 bulan
```

### **Events:**

- `LoanCancelledEvent`: loanId, borrowerId, cancelledDate, totalRefunded, affectedLenders

---

## 10. Peringatan Kritis

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
   - IN-SCOPE: Register (with fields), Apply, Verify, Invest, Disburse, CANCELLED status, EXPIRED_FUNDING
   - TIDAK perlu: File upload/storage, payment transaction, repayment, penalty, face recognition, dll

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

## 12. Deliverable Akhir (Hari 7 EOD)

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

## 11. Backup: Hubungi Dosen

Jika ada blocker:
- **Architectural block:** Konsultasi dosen → Kemal/Rafi coordinate
- **Scope creep:** Dosen approval → freeze scope
- **Time pressure:** Prioritize core flow (register, apply, invest, disburse)

---

## 13. Key Success Factors

1. **Parallelism** → 5 orang bekerja 5 task berbeda → selesai dalam 1 minggu
2. **Clear dependency** → Layer per layer, interface-first design
3. **TDD consistency** → Test tulis dulu, code mengikuti
4. **Mockito mastery** → Repository mock di setiap use case test
5. **Communication** → Daily standup, issue raising cepat

---

**Good luck, tim! 🚀**
