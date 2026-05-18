package com.p2plending.integration;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.BorrowerDTO;
import com.p2plending.application.borrower.dto.CancelLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand;
import com.p2plending.application.borrower.usecase.ApplyLoanUseCaseImpl;
import com.p2plending.application.borrower.usecase.CancelLoanUseCaseImpl;
import com.p2plending.application.borrower.usecase.RegisterBorrowerUseCase;
import com.p2plending.application.lender.dto.InvestCommand;
import com.p2plending.application.lender.dto.LenderDTO;
import com.p2plending.application.lender.dto.RegisterLenderCommand;
import com.p2plending.application.lender.usecase.InvestLoanUseCaseImpl;
import com.p2plending.application.lender.usecase.RegisterLenderUseCaseImpl;
import com.p2plending.application.shared.ApproveLoanCommand;
import com.p2plending.application.shared.ApproveLoanUseCaseImpl;
import com.p2plending.application.shared.DisburseLoanCommand;
import com.p2plending.application.shared.DisburseUseCaseImpl;
import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.service.LoanApprovalService;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.lender.service.InvestmentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.infrastructure.persistence.InMemoryBorrowerRepository;
import com.p2plending.infrastructure.persistence.InMemoryInvestmentRepository;
import com.p2plending.infrastructure.persistence.InMemoryLenderRepository;
import com.p2plending.infrastructure.persistence.InMemoryLoanRepository;
import com.p2plending.infrastructure.persistence.SharedStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Integration Test — P2P Lending Platform
 *
 * Menggunakan real in-memory repositories (NO mocks).
 * Setiap test independen via @BeforeEach yang clear SharedStorage.
 *
 * Skenario:
 * 1. Happy path   : apply → approve → invest → FUNDED (otomatis) → disburse
 * 2. Cancellation : apply → approve → invest → cancel (counter +1)
 * 2b. 3x cancel   : borrower diblokir 4 bulan
 * 3. Expired      : apply → approve → expireFunding() → EXPIRED_FUNDING
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EndToEndFlowTest {

    // ─── Repositories ─────────────────────────────────────────────────
    private InMemoryBorrowerRepository borrowerRepo;
    private InMemoryLoanRepository loanRepo;
    private InMemoryLenderRepository lenderRepo;
    private InMemoryInvestmentRepository investmentRepo;

    // ─── Use Cases ────────────────────────────────────────────────────
    private RegisterBorrowerUseCase registerBorrowerUseCase;
    private ApplyLoanUseCaseImpl applyLoanUseCase;
    private ApproveLoanUseCaseImpl approveLoanUseCase;
    private DisburseUseCaseImpl disburseUseCase;
    private CancelLoanUseCaseImpl cancelLoanUseCase;
    private RegisterLenderUseCaseImpl registerLenderUseCase;
    private InvestLoanUseCaseImpl investLoanUseCase;

    // ─── Test Data Constants ───────────────────────────────────────────
    private static final String KTP_IMAN  = "1111222233334444";
    private static final String KTP_BUDI  = "5555666677778888";
    private static final String KTP_KEMAL = "3333444455556666";

    @BeforeEach
    void setUp() {
        SharedStorage.getInstance().getBorrowers().clear();
        SharedStorage.getInstance().getLoans().clear();
        SharedStorage.getInstance().getLenders().clear();
        SharedStorage.getInstance().getInvestments().clear();

        borrowerRepo   = new InMemoryBorrowerRepository();
        loanRepo       = new InMemoryLoanRepository();
        lenderRepo     = new InMemoryLenderRepository();
        investmentRepo = new InMemoryInvestmentRepository();

        LoanApprovalService     loanApprovalService     = new LoanApprovalService();
        LoanCancellationService loanCancellationService = new LoanCancellationService();
        InvestmentService       investmentService       = new InvestmentService();

        registerBorrowerUseCase = new RegisterBorrowerUseCase(borrowerRepo);
        applyLoanUseCase        = new ApplyLoanUseCaseImpl(borrowerRepo, loanRepo, loanApprovalService);
        approveLoanUseCase      = new ApproveLoanUseCaseImpl(loanRepo);
        disburseUseCase         = new DisburseUseCaseImpl(loanRepo);
        cancelLoanUseCase       = new CancelLoanUseCaseImpl(borrowerRepo, loanRepo, loanCancellationService, investmentRepo);
        registerLenderUseCase   = new RegisterLenderUseCaseImpl(lenderRepo);
        investLoanUseCase       = new InvestLoanUseCaseImpl(lenderRepo, loanRepo, investmentRepo, investmentService);
    }

    // ═════════════════════════════════════════════════════════════════
    // SKENARIO 1 — HAPPY PATH
    // ═════════════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("Skenario 1: Happy path — loan berhasil dicairkan (DISBURSED)")
    void scenario1_happyPath_loanSuccessfullyDisbursed() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 750));
        assertNotNull(iman.getId());
        assertEquals("Iman Santoso", iman.getNama());

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        assertNotNull(loan.getId());
        assertEquals(LoanStatus.PENDING, getLoanStatus(loan.getId()));

        // Approve → FUNDING via State Pattern (PENDING → VERIFIED → FUNDING)
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        assertEquals(LoanStatus.FUNDING, getLoanStatus(loan.getId()));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")));
        assertEquals(new BigDecimal("50000000"), budi.getSaldo());

        // Invest 30jt (100%) → State Pattern otomatis transisi ke FUNDED
        // via FundingState.addInvestment() → checkFundingComplete() → FundedState
        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("30000000")));

        BigDecimal saldoBudiSetelah = lenderRepo.findById(budi.getId()).get().getSaldo().getAmount();
        assertEquals(new BigDecimal("20000000"), saldoBudiSetelah,
                "Saldo BUDI harus berkurang 30jt");

        // FUNDED sudah otomatis — tidak perlu forceSet
        assertEquals(LoanStatus.FUNDED, getLoanStatus(loan.getId()),
                "Status harus FUNDED otomatis setelah invest 100%");

        // Disburse → FUNDED → DISBURSED via State Pattern
        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        assertEquals(LoanStatus.DISBURSED, getLoanStatus(loan.getId()));
    }

    @Test
    @Order(2)
    @DisplayName("Skenario 1b: Admin reject loan → status CANCELLED")
    void scenario1b_adminRejectLoan_statusCancelled() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 20_000_000L, 6));

        // Reject → CANCELLED via State Pattern (PENDING → CANCELLED)
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), false));
        assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()));
    }

    // ═════════════════════════════════════════════════════════════════
    // SKENARIO 2 — CANCELLATION
    // ═════════════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @DisplayName("Skenario 2: Borrower cancel loan → status CANCELLED, counter +1")
    void scenario2_cancellation_counterIncremented() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("20000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 3));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        // Invest tepat 20% (6jt dari 30jt) → tidak cukup untuk auto-FUNDED
        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000")));

        int countBefore = getCancellationCount(iman.getId());
        assertEquals(0, countBefore);

        // Cancel → CANCELLED via LoanCancellationService + State Pattern
        cancelLoanUseCase.execute(new CancelLoanCommand(
                iman.getId(), loan.getId(), new Money(new BigDecimal("6000000"), "IDR")));

        assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()));

        LoanApplication cancelledLoan = loanRepo.findById(loan.getId()).get();
        assertNotNull(cancelledLoan.getCancelledDate(), "cancelledDate harus diisi");

        int countAfter = getCancellationCount(iman.getId());
        assertEquals(countBefore + 1, countAfter, "Counter harus naik +1");

        Borrower borrower = borrowerRepo.findById(iman.getId()).get();
        assertNull(borrower.getLastBlockedDate(), "Belum diblokir (baru 1x cancel)");
    }

    @Test
    @Order(4)
    @DisplayName("Skenario 2b: 3x cancel → borrower diblokir 4 bulan")
    void scenario2b_threeTimesCancel_borrowerBlocked() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 750));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("100000000")));

        for (int i = 1; i <= 3; i++) {
            LoanDTO loan = applyLoanUseCase.execute(
                    new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
            approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000")));
            cancelLoanUseCase.execute(new CancelLoanCommand(
                    iman.getId(), loan.getId(), new Money(new BigDecimal("6000000"), "IDR")));

            assertEquals(i, getCancellationCount(iman.getId()),
                    "Setelah cancel ke-" + i + ", count harus " + i);
        }

        Borrower borrowerAfter = borrowerRepo.findById(iman.getId()).get();
        assertNotNull(borrowerAfter.getLastBlockedDate(),
                "Borrower harus diblokir setelah 3x cancel");
        assertEquals(3, borrowerAfter.getCancellationCount());
    }

    @Test
    @Order(5)
    @DisplayName("Skenario 2c: Cancel BISA jika funded < 20% — tanpa penalty")
    void scenario2c_canCancel_ifFundedLessThan20Percent_NoPenalty() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        // funded 5jt < 20% threshold (6jt) → cancel dibolehkan tanpa penalty
        cancelLoanUseCase.execute(new CancelLoanCommand(
                iman.getId(), loan.getId(), new Money(new BigDecimal("5000000"), "IDR")));

        assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()));
    }

    @Test
    @Order(6)
    @DisplayName("Skenario 2d: Cancel tidak bisa setelah 3x cancel (blokir)")
    void scenario2d_cannotCancel_afterMaxCancellations() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 750));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("200000000")));

        for (int i = 0; i < 3; i++) {
            LoanDTO l = applyLoanUseCase.execute(
                    new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
            approveLoanUseCase.execute(new ApproveLoanCommand(l.getId(), true));
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), l.getId(), new BigDecimal("6000000")));
            cancelLoanUseCase.execute(new CancelLoanCommand(
                    iman.getId(), l.getId(), new Money(new BigDecimal("6000000"), "IDR")));
        }

        Borrower blockedBorrower = borrowerRepo.findById(iman.getId()).get();
        assertEquals(3, blockedBorrower.getCancellationCount());
        assertNotNull(blockedBorrower.getLastBlockedDate(), "Borrower harus diblokir");
    }

    // ═════════════════════════════════════════════════════════════════
    // SKENARIO 3 — EXPIRED FUNDING
    // ═════════════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @DisplayName("Skenario 3: Loan tidak terfund → EXPIRED_FUNDING via State Pattern")
    void scenario3_expiredFunding_statusSetCorrectly() {
        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Kemal Peminjam", "085555555555", KTP_KEMAL,
                "Jl. Kuningan No. 7", 5_000_000L, 650));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        assertEquals(LoanStatus.FUNDING, getLoanStatus(loan.getId()));

        // Simulasi 5 hari berlalu → FUNDING → EXPIRED_FUNDING via State Pattern
        expireLoanFunding(loan.getId());
        assertEquals(LoanStatus.EXPIRED_FUNDING, getLoanStatus(loan.getId()));
    }

    @Test
    @Order(8)
    @DisplayName("Skenario 3b: Loan EXPIRED tidak bisa di-disburse")
    void scenario3b_expiredLoan_cannotBeDisbursed() {
        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Kemal Peminjam", "085555555555", KTP_KEMAL,
                "Jl. Kuningan No. 7", 5_000_000L, 650));
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        expireLoanFunding(loan.getId());

        assertThrows(IllegalStateException.class,
                () -> disburseUseCase.execute(new DisburseLoanCommand(loan.getId())),
                "Loan expired tidak bisa di-disburse");
    }

    @Test
    @Order(9)
    @DisplayName("Skenario 3c: Loan EXPIRED tidak bisa di-approve ulang")
    void scenario3c_expiredLoan_cannotBeReapproved() {
        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Kemal Peminjam", "085555555555", KTP_KEMAL,
                "Jl. Kuningan No. 7", 5_000_000L, 650));
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        expireLoanFunding(loan.getId());

        assertThrows(IllegalStateException.class,
                () -> approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true)),
                "Loan expired tidak bisa di-approve ulang");
    }

    // ═════════════════════════════════════════════════════════════════
    // Edge Cases
    // ═════════════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("Edge case: Lender invest < 20% dari loan → ditolak")
    void edgeCase_investLessThan20Percent_rejected() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("20000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        // 5jt < 20% dari 30jt (threshold = 6jt) → harus ditolak
        assertThrows(IllegalArgumentException.class,
                () -> investLoanUseCase.execute(
                        new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("5000000"))));
    }

    @Test
    @Order(11)
    @DisplayName("Edge case: Lender saldo tidak cukup → invest ditolak")
    void edgeCase_insufficientSaldo_investRejected() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));
        // BUDI hanya punya 5jt, padahal min invest 6jt
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("5000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        assertThrows(IllegalArgumentException.class,
                () -> investLoanUseCase.execute(
                        new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000"))));
    }

    @Test
    @Order(12)
    @DisplayName("Edge case: Invest ke loan bukan status FUNDING → ditolak")
    void edgeCase_investToNonFundingLoan_rejected() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        // Belum di-approve → masih PENDING

        assertThrows(IllegalArgumentException.class,
                () -> investLoanUseCase.execute(
                        new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("10000000"))));
    }

    @Test
    @Order(13)
    @DisplayName("Edge case: Borrower tidak ditemukan saat apply → exception")
    void edgeCase_borrowerNotFound_applyLoan() {
        assertThrows(IllegalArgumentException.class,
                () -> applyLoanUseCase.execute(
                        new ApplyLoanCommand("BORROWER-TIDAK-ADA", 10_000_000L, 6)));
    }

    // ═════════════════════════════════════════════════════════════════
    // Helper Methods
    // ═════════════════════════════════════════════════════════════════

    /**
     * Trigger expireFunding via State Pattern (FUNDING → EXPIRED_FUNDING).
     * Menggantikan reflection hack — sekarang lewat LoanAggregate.expireFunding()
     * yang delegate ke FundingState → transitionToState(new ExpiredFundingState()).
     */
    private void expireLoanFunding(String loanId) {
        LoanApplication loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));
        LoanAggregate aggregate = LoanAggregate.load(loan, null);
        aggregate.expireFunding();
        loanRepo.save(loan);
    }

    private LoanStatus getLoanStatus(String loanId) {
        return loanRepo.findById(loanId)
                .map(LoanApplication::getStatus)
                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));
    }

    private int getCancellationCount(String borrowerId) {
        return borrowerRepo.findById(borrowerId)
                .map(Borrower::getCancellationCount)
                .orElseThrow(() -> new IllegalArgumentException("Borrower tidak ditemukan: " + borrowerId));
    }
}