# 📋 P2P Lending Platform - Masterplan
**Deadline: 1 Juni 2026 | 5 Hari | Tim 5 Orang | Java 11+ | Maven | DDD + TDD**

---

## ⚠️ PENTING: PHASE 1 SUDAH SELESAI - JANGAN DIUBAH!

```
🔒 PHASE 1 (Hari 1-3): DISBURSEMENT - ✅ COMPLETE & DEPLOYED
   ├─ Semua file sudah ada di src/main/java
   ├─ Semua test sudah PASSING (37 files)
   ├─ Merged ke main branch
   ├─ JANGAN DIUBAH / JANGAN DIMODIFIKASI
   └─ AI: HANYA BACA, TIDAK PERLU IMPLEMENTASI

🟡 PHASE 2 (Hari 3-5): REPAYMENT - ⏳ TO START THIS SPRINT
   ├─ Payment entity & services
   ├─ Make payment use case
   ├─ Repayment tests
   └─ AI: IMPLEMENTASI HANYA INI!
```

---

## ⏰ STATUS: 4 Hari Tersisa (Selesai Hari 1 - Mulai Hari 5!)

---

## 1. Features (SIMPLIFIED)

### 🔒 PHASE 1 (Hari 1-3): Disbursement - COMPLETE ✅ DO NOT MODIFY

**Status: SUDAH SELESAI - 37 FILES SIAP DEPLOY**

#### Borrower
- Register (credit score: min 600) ✅
- Apply loan (tenor: 3/6 bulan) ✅
- Cancel loan (if funded < 20% atau < 3 hari) ✅

#### Lender
- Register + initial balance ✅
- Invest (min 20% of loan) ✅

#### Business Rules
- Loan amount ≤ 3x salary ✅
- Credit score ≥ 600 ✅
- Min investment 20% ✅
- Funding deadline 5 hari ✅
- States: PENDING→FUNDING→FUNDED→DISBURSED / CANCELLED / EXPIRED_FUNDING ✅

**⚠️ PHASE 1 JANGAN DIRUBAH! Semua sudah tested & passed.**

---

### 🟡 PHASE 2 (Hari 3-5): Repayment - TO IMPLEMENT NOW
#### Borrower
- **Make monthly payment** (simple: pokok + bunga 3%)
- **View payment status** (PENDING/PAID/OVERDUE)

#### Lender
- **View payment received** (dari cicilan borrower)

#### Repayment Rules
- Cicilan per bulan = Pokok + Bunga (3% × pokok)
- Payment auto-generate saat DISBURSED
- Status: PENDING → PAID (on time) / OVERDUE (telat)
- Denda HANYA jika telat > 30 hari = pokok × 1%
- Loan complete = semua payment PAID

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

## 3. 5 GoF Patterns (SIMPLIFIED)

| Pattern | File | Purpose |
|---------|------|---------|
| **State** | LoanStatus enum | Manage state transitions (PENDING→DISBURSED) |
| **Factory** | LoanAggregate.create() | Create loan with validation |
| **Strategy** | InterestCalculator (3% bunga) | Simple interest calc |
| **Repository** | *Repository interfaces | Abstract data access |
| **Aggregate** | LoanAggregate, RepaymentAggregate | DDD domain objects |

---

## 4. DDD Structure (LEAN)

```
src/main/java/com/p2plending/
├── domain/
│   ├── borrower/ (Borrower, LoanApplication, Payment)
│   ├── lender/ (Lender, Investment)
│   ├── aggregate/ (LoanAggregate, RepaymentAggregate)
│   ├── service/ (LoanService, RepaymentService)
│   └── shared/ (Money, LoanStatus, PaymentStatus)
├── application/
│   ├── borrower/ (RegisterBorrowerUseCase, ApplyLoanUseCase, MakePaymentUseCase)
│   ├── lender/ (RegisterLenderUseCase, InvestLoanUseCase)
│   └── shared/ (ApproveLoanUseCase, DisburseUseCase)
├── infrastructure/
│   └── persistence/ (repositories, SharedStorage)
└── interfaces/
    └── cli/ (LendingApp.java)
```

---

## 5. Team Tasks - PHASE 1 & 2

**⚠️ PHASE 1 (Hari 1-3): SUDAH SELESAI - TIDAK PERLU IMPLEMENTASI**

| Person | Days | PHASE 1 (COMPLETE ✅) |
|--------|------|-------------|
| **IMAN** | 1-2 | ✅ 5 Domain Entities (DONE) |
| **KEMAL** | 2-3 | ✅ 4 Domain Services (DONE) |
| **DANANG** | 2-4 | ✅ 3 Use Cases (DONE) |
| **JAYA** | 3-4 | ✅ 3 Repositories (DONE) |
| **RAFI** | 2-5 | ✅ 1 E2E Test Phase 1 (DONE) |

---

**🟡 PHASE 2 (Hari 3-5): IMPLEMENTASI DIMULAI SEKARANG**

| Person | Days | PHASE 2 (TO DO) | Files |
|--------|------|-------------|-------|
| **IMAN** | 3-4 | Payment entities & enums | 2 files |
| **KEMAL** | 3-4 | Repayment services | 2 files |
| **DANANG** | 4-5 | Payment use cases | 2 files |
| **JAYA** | 4-5 | Payment repository | 1 file |
| **RAFI** | 4-5 | Repayment E2E test | 1 file |

**TOTAL PHASE 2: 8 files | TO IMPLEMENT NOW**

---

## 6. IMAN: Domain Entities (Hari 1-2) - 5 FILES

⚠️ **PHASE 1 - SUDAH SELESAI, JANGAN DIUBAH!**

**PHASE 2 IMPLEMENTATION (Hari 3-4):**

**Entities to ADD:**
1. **Payment.java** - monthNumber, dueDate, amount, status, paidDate, denda
2. **PaymentStatus.java** - enum: PENDING, PAID, OVERDUE

**Key Requirements:**
- dueDate = disbursedDate + (monthNumber × 30 days)
- status default = PENDING
- denda calculated automatically (1% × pokok jika >30 hari telat)

**TDD Checklist:**
- [ ] Payment entity test
- [ ] PaymentStatus enum test
- [ ] dueDate calculation test
- [ ] All tests GREEN

---

## 7. KEMAL: Domain Services (Hari 2-3) - 7 FILES

⚠️ **PHASE 1 (4 files) - SUDAH SELESAI, JANGAN DIUBAH!**
   - LoanService.java ✅
   - InvestmentService.java ✅
   - LoanAggregate.java ✅
   - LoanStatusEnum ✅

**PHASE 2 IMPLEMENTATION (Hari 3-4) - 2 FILES BARU:**

1. **RepaymentService.java**
   - makePayment(loanId, paymentAmount): process & update status
   - checkAndUpdateStatus(): PENDING → PAID or OVERDUE (jika telat > 30 hari)
   - calculateDenda(totalDebt, daysLate): 1% × pokok

2. **InterestCalculator.java** - STRATEGY pattern
   - calculate(principalAmount): return pokok × 3%

**TDD Checklist:**
- [ ] RepaymentService (payment processing)
- [ ] Denda calculator (0% untuk ≤30 hari, 1% untuk >30)
- [ ] Status updates (automatic, not manual)
- [ ] All tests GREEN

---

## 8. DANANG: Application Layer (Hari 2-4) - 5 FILES

⚠️ **PHASE 1 (3 files) - SUDAH SELESAI, JANGAN DIUBAH!**
   - RegisterBorrowerUseCase + DTO ✅
   - ApplyLoanUseCase + DTO ✅
   - InvestLoanUseCase + DTO ✅

**PHASE 2 IMPLEMENTATION (Hari 4-5) - 2 FILES BARU:**

1. **MakePaymentUseCase + DTO**
   - Input: borrowerId, loanId, paymentAmount, paymentDate
   - Output: PaymentDTO (status, denda jika ada)
   - Update payment status & repayment status

2. **GetPaymentScheduleUseCase + DTO**
   - Input: loanId
   - Output: List<PaymentDTO> dengan due date, amount, status

**TDD with Mockito:**
- @Mock repositories
- @InjectMocks use cases
- Simple test: input → process → verify output

**TDD Checklist:**
- [ ] MakePaymentUseCase (process payment, update status)
- [ ] GetPaymentScheduleUseCase (return list)
- [ ] All tests GREEN

---

## 9. JAYA: Infrastructure (Hari 3-4) - 4 FILES

⚠️ **PHASE 1 (3 files) - SUDAH SELESAI, JANGAN DIUBAH!**
   - SharedStorage.java ✅
   - InMemoryBorrowerRepository.java ✅
   - InMemoryLoanRepository.java ✅

**PHASE 2 IMPLEMENTATION (Hari 4-5) - 1 FILE BARU:**

1. **InMemoryPaymentRepository.java**
   - save(payment): add to HashMap
   - findById(id): return from HashMap
   - findByLoanId(loanId): filter List<Payment>
   - updateStatus(paymentId, status): find & update

**TDD Checklist:**
- [ ] PaymentRepository (CRUD basic)
- [ ] findByLoanId, updateStatus
- [ ] Integration test (save & retrieve)
- [ ] All tests GREEN

---

## 10. RAFI: E2E Tests (Hari 2-5) - 2 FILES

⚠️ **PHASE 1 - SUDAH SELESAI, JANGAN DIUBAH!**
   - EndToEndFlowTest.java (Phase 1 scenarios) ✅
   - LendingApp.java (Phase 1 demo) ✅

**PHASE 2 IMPLEMENTATION (Hari 4-5) - ADD TO EXISTING TESTS:**

**Add 3 Scenarios ke EndToEndFlowTest.java:**
- Scenario 1: Borrower register → apply loan → approve → lender invest → FUNDED → disburse → DISBURSED ✅ (already tested)
- Scenario 2 (NEW): Payment on time → status PAID → next payment
- Scenario 3 (NEW): Payment telat 40 hari → status OVERDUE → denda 1% × pokok

**Update LendingApp.java:**
- Keep Phase 1 hardcoded demo
- Add Phase 2 scenarios (payment flow)
- Console output dengan timeline jelas

**TDD Checklist:**
- [ ] Add Phase 2 scenarios to E2E test
- [ ] Verify payment processing
- [ ] Verify denda calculation
- [ ] All tests GREEN (both phases)

---

## 11. Git Strategy (SIMPLE)

```
main (production)
develop (integration)
├── feature/phase1-disbursement (Hari 1-3)
└── feature/phase2-repayment (Hari 3-5)
```

Merge to develop Hari 3, then main Hari 5.

---

## 12. TDD Workflow

```
RED (test fail) → GREEN (test pass) → REFACTOR → COMMIT
```
**Every file, every time. No exceptions.**

---

## 13. Definition of Done (QUICK VERSION)

**Daily:**
- [ ] Code written + tested
- [ ] All tests GREEN
- [ ] No compile errors

**Phase 1 (Hari 3):**
- [ ] Disbursement flow working
- [ ] E2E test success

**Phase 2 (Hari 5):**
- [ ] Repayment flow working
- [ ] Full E2E test success
- [ ] Final merge to main

---

## 14. Critical Rules ⚠️

1. **🔒 DO NOT MODIFY PHASE 1** - Disbursement is COMPLETE & tested
   - Do NOT touch: Borrower, LoanApplication, LoanAggregate, LoanService, etc.
   - Do NOT modify existing tests or code
   - ONLY add new PHASE 2 features
   
2. **RED→GREEN→REFACTOR (mandatory)**

3. **Repository INTERFACE in domain, IMPL in infrastructure**

4. **Domain never imports application/infrastructure**

5. **Mockito only in application tests**

6. **Payment status AUTOMATIC (calculated, not manual)**

7. **Denda = 1% × pokok, only if >30 hari telat**

8. **No complex logic - keep it simple!**

9. **Commit every file after test passes**

10. **PHASE 2 ONLY: Payment entity, RepaymentService, MakePaymentUseCase, PaymentRepository, E2E scenarios**

---

## 15. Timeline Summary

| Phase | Status | Days | IMAN | KEMAL | DANANG | JAYA | RAFI |
|-------|--------|------|------|-------|--------|------|------|
| **PHASE 1** | ✅ DONE | Hari 1-3 | ✅ 5 files | ✅ 4 services | ✅ 3 use cases | ✅ 3 repos | ✅ 1 E2E |
| **PHASE 2** | 🟡 TODO | Hari 3-5 | 2 Payment files | 2 Repayment svc | 2 Payment use cases | 1 Payment repo | Add E2E scenarios |

**Total PHASE 2: Only 8 files to implement**

---

## 16. Clean Code Guidelines (MANDATORY)

Semua kode PHASE 2 HARUS mengikuti Clean Code principles dari Uncle Bob:

### 1. MEANINGFUL NAMES
✅ **GOOD**
```java
double monthlyPayment = principal / tenor + (principal * INTEREST_RATE);
boolean isPaymentOverdue = paymentDate.isAfter(dueDate);
```

❌ **BAD**
```java
double mp = p / t + (p * 0.03);
boolean pd = pd > dd;  // ambiguous variable names
```

**Aturan:**
- Gunakan nama yang menjelaskan tujuan (intent)
- Hindari nama singkat atau ambigu (d, t, x)
- Satu kata untuk satu konsep
- Gunakan pronounceable names

### 2. FUNCTIONS (SMALL & FOCUSED)
✅ **GOOD**
```java
public void makePayment(String loanId, BigDecimal amount) {
    validatePaymentAmount(amount);
    updatePaymentStatus(loanId, amount);
    notifyLender(loanId);
}

private void validatePaymentAmount(BigDecimal amount) {
    if (amount.compareTo(ZERO) <= 0) {
        throw new IllegalArgumentException("Payment amount must be positive");
    }
}
```

❌ **BAD**
```java
// Function terlalu panjang dan melakukan banyak hal
public void makePayment(String loanId, BigDecimal amount) {
    if (amount <= 0) throw new Exception("Invalid");
    // validasi
    // update status
    // notify
    // distribute
    // log
    // ... 100+ lines
}
```

**Aturan:**
- Fungsi harus kecil (5-10 baris ideal)
- Satu fungsi = satu tanggung jawab (Single Responsibility)
- Nama fungsi harus deskriptif
- Parameter minimal (max 3)

### 3. COMMENTS (MINIMAL)
✅ **GOOD**
```java
// Kode yang self-documenting, minimal comments
public BigDecimal calculateDenda(BigDecimal principal, int daysLate) {
    if (daysLate <= 30) return ZERO;
    return principal.multiply(DENDA_RATE);  // 1% of principal
}
```

❌ **BAD**
```java
// Calculate denda
// Input: p = principal, d = days late
// Output: denda amount
public BigDecimal calc(BigDecimal p, int d) {  // Wrong name!
    // if days > 30
    if (d > 30) {
        // multiply by 1%
        return p.multiply(new BigDecimal("0.01"));
    }
    return new BigDecimal("0");
}
```

**Aturan:**
- Kode harus self-documenting (nama jelas = comment minimal)
- Hanya gunakan comment untuk:
  - Legal (copyright)
  - Warning (beware of this!)
  - Explanation of intent (WHY, bukan WHAT)
  - TODO comments untuk pekerjaan belum selesai

### 4. FORMATTING & STRUCTURE
✅ **GOOD**
```java
public class PaymentService {
    private static final BigDecimal DENDA_RATE = new BigDecimal("0.01");
    private static final int DENDA_THRESHOLD_DAYS = 30;
    
    private PaymentRepository paymentRepository;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    public void processPayment(String loanId, BigDecimal amount) {
        validatePaymentAmount(amount);
        updatePaymentStatus(loanId, amount);
    }
    
    private void validatePaymentAmount(BigDecimal amount) {
        // ...
    }
}
```

❌ **BAD**
```java
// Class tidak terorganisir, indentasi inconsistent
public class PaymentService{
private PaymentRepository r;
public PaymentService(PaymentRepository repo){this.r=repo;}
public void p(String id,BigDecimal a){if(a>0){r.save(id,a);}}
}  // Sangat sulit dibaca!
```

**Aturan:**
- Gunakan indentasi konsisten (4 spaces)
- File tidak lebih dari 500 baris
- Related methods harus berdekatan
- Deklarasi constants di atas
- Gunakan line length max 120 karakter

### 5. ERROR HANDLING
✅ **GOOD**
```java
try {
    FileReader reader = new FileReader(filePath);
    return gson.fromJson(reader, Config.class);
} catch (FileNotFoundException e) {
    logger.error("Config file not found: " + filePath, e);
    throw new ConfigurationException("Failed to load config: " + filePath, e);
} catch (JsonSyntaxException e) {
    logger.error("Invalid JSON in config file: " + filePath, e);
    throw new ConfigurationException("Invalid config format", e);
}
```

❌ **BAD**
```java
try {
    // ... code
} catch (Exception e) {  // Terlalu generic!
    e.printStackTrace();  // Don't do this!
    return null;  // Returning null is bad
}
```

**Aturan:**
- Tangani exception spesifik, bukan generic Exception
- Berikan konteks yang cukup di pesan error
- Jangan return null (throw exception instead)
- Use try-with-resources untuk auto-close

### 6. OBJECTS & DATA STRUCTURES
✅ **GOOD**
```java
// Object dengan behavior
public class Payment {
    private BigDecimal amount;
    private LocalDateTime dueDate;
    private PaymentStatus status;
    
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate) && status == PaymentStatus.PENDING;
    }
    
    public BigDecimal calculateDenda() {
        if (!isOverdue()) return ZERO;
        int daysLate = calculateDaysLate();
        if (daysLate <= 30) return ZERO;
        return amount.multiply(DENDA_RATE);
    }
}
```

❌ **BAD**
```java
// Data structure tanpa behavior (anemic object)
public class Payment {
    public BigDecimal amount;
    public LocalDateTime dueDate;
    public String status;
    // No methods! Just getters/setters
}

// Then scattered logic everywhere
if (payment.dueDate.isBefore(now) && payment.status.equals("PENDING")) {
    // calculate denda...
}
```

**Aturan:**
- Object harus punya behavior (methods)
- Encapsulation: private fields + public methods
- Jangan expose internal structure
- Data structure: simple data containers only

### 7. TESTING (UNIT TESTS)
✅ **GOOD**
```java
@Test
public void makePayment_OnTimePayment_StatusShouldBePaid() {
    // Arrange
    Payment payment = new Payment(amount, dueDate);
    
    // Act
    paymentService.processPayment(payment);
    
    // Assert
    assertEquals(PaymentStatus.PAID, payment.getStatus());
}

@Test
public void calculateDenda_LatePayment40Days_ShouldReturnDenda() {
    // Arrange
    Payment payment = new Payment(principal, dueDate.minusDays(40));
    
    // Act
    BigDecimal denda = payment.calculateDenda();
    
    // Assert
    assertEquals(principal.multiply(DENDA_RATE), denda);
}
```

❌ **BAD**
```java
@Test
public void test1() {
    // Unclear test name
    Payment p = new Payment(1000, LocalDateTime.now().minusDays(50));
    BigDecimal d = p.getDenda();
    assertTrue(d > 0);  // Weak assertion
}
```

**Aturan:**
- Test name = describe what is being tested
- Format: `methodName_Condition_ExpectedResult`
- AAA pattern: Arrange, Act, Assert
- One assertion per test (atau related assertions)
- 100% coverage untuk critical path

### 8. DEPENDENCY INJECTION (Constructor Injection)
✅ **GOOD**
```java
public class MakePaymentUseCase {
    private final PaymentRepository paymentRepository;
    private final RepaymentService repaymentService;
    
    public MakePaymentUseCase(PaymentRepository repo, RepaymentService service) {
        this.paymentRepository = repo;
        this.repaymentService = service;
    }
    
    public void execute(String loanId, BigDecimal amount) {
        // Use dependencies
    }
}
```

❌ **BAD**
```java
public class MakePaymentUseCase {
    private PaymentRepository paymentRepository = new InMemoryPaymentRepository();  // Tightly coupled!
    
    public void execute(String loanId, BigDecimal amount) {
        // Hard to test
    }
}
```

**Aturan:**
- Gunakan constructor injection
- Dependencies dalam constructor parameter
- Jangan create instances dalam class (tightly coupled)
- Memudahkan testing dengan mock

### 9. NAMING CONVENTIONS FOR JAVA
```java
// Classes: PascalCase
public class PaymentService { }
public class RepaymentAggregate { }

// Methods & Variables: camelCase
public void calculateDenda() { }
BigDecimal monthlyPayment = ...;

// Constants: UPPER_SNAKE_CASE
private static final BigDecimal DENDA_RATE = new BigDecimal("0.01");
private static final int DENDA_THRESHOLD_DAYS = 30;

// Booleans: is/has prefix
boolean isOverdue = false;
boolean isActive = true;
boolean hasError = false;
```

### 10. DESIGN PATTERNS (Use Naturally, Don't Over-Engineer)
```java
// STRATEGY: Interest calculation
public interface InterestCalculator {
    BigDecimal calculate(BigDecimal principal);
}

public class SimpleInterestCalculator implements InterestCalculator {
    public BigDecimal calculate(BigDecimal principal) {
        return principal.multiply(RATE);
    }
}

// AGGREGATE: Loan management
public class LoanAggregate {
    private Loan loan;
    private List<Payment> payments;
    
    public void makePayment(BigDecimal amount) { }
    public boolean isComplete() { }
}

// REPOSITORY: Data access
public interface PaymentRepository {
    void save(Payment payment);
    Payment findById(String id);
    List<Payment> findByLoanId(String loanId);
}
```

---

### CHECKLIST SEBELUM COMMIT

Sebelum push code ke git, pastikan:

- [ ] Semua method names deskriptif (bukan p(), f(), x())
- [ ] Semua class names meaningful (bukan Util, Helper)
- [ ] Semua functions kecil (< 20 baris)
- [ ] Tidak ada nested if/loop lebih dari 2 level
- [ ] Tidak ada magic numbers (gunakan constants)
- [ ] Comments hanya untuk "WHY", bukan "WHAT"
- [ ] Proper exception handling (specific exceptions)
- [ ] Consistent indentation & formatting
- [ ] Unit tests ada & passing (>80% coverage)
- [ ] No commented-out code (delete, gunakan git history)
- [ ] No println/System.out (use logger)
- [ ] No global variables (tightly coupled)
- [ ] Dependency injection digunakan
- [ ] No null returns (throw exception)
- [ ] Code reviewed oleh team member

---

**INGAT:** "Code is read much more often than it is written." — Uncle Bob

Tulis kode untuk manusia, bukan komputer! 🎯

---

## 🎯 KEY SIMPLIFICATIONS

1. **23 files instead of 61** - lean scope
2. **3+2 use cases** - focus on core flow
3. **5 GoF patterns** - essential only
4. **No Observer/Event bus** - overhead removed
5. **Denda logic simple** - 1% only if >30 hari
6. **Payment auto-generated** - no manual entry
7. **Two phases only** - Disbursement + Repayment
8. **Hardcoded demo** - no CLI complexity

---

**STATUS: 4 HARI TERSISA → SPRINT MULAI SEKARANG! 🚀**
**JANGAN KOMPLEKS, FOKUS PADA CORE FLOW SAJA!**
