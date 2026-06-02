package com.p2plending.integration;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.BorrowerDTO;
import com.p2plending.application.borrower.dto.CancelLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.application.borrower.dto.MakePaymentCommand;
import com.p2plending.application.borrower.dto.PaymentDTO;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand;
import com.p2plending.application.borrower.usecase.ApplyLoanUseCaseImpl;
import com.p2plending.application.borrower.usecase.CancelLoanUseCaseImpl;
import com.p2plending.application.borrower.usecase.GetPaymentScheduleUseCaseImpl;
import com.p2plending.application.borrower.usecase.MakePaymentUseCaseImpl;
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
import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.repository.PaymentRepository;
import com.p2plending.domain.borrower.service.LoanApprovalService;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.borrower.service.PaymentScheduleService;
import com.p2plending.domain.borrower.service.RepaymentService;
import com.p2plending.domain.lender.service.InvestmentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.PaymentStatus;
import com.p2plending.infrastructure.persistence.InMemoryBorrowerRepository;
import com.p2plending.infrastructure.persistence.InMemoryInvestmentRepository;
import com.p2plending.infrastructure.persistence.InMemoryLenderRepository;
import com.p2plending.infrastructure.persistence.InMemoryLoanRepository;
import com.p2plending.infrastructure.persistence.InMemmoryPaymentRepository;
import com.p2plending.infrastructure.persistence.SharedStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EndToEndFlowTest {

    // ─── Repositories ──────────────────────────────────────────────
    private InMemoryBorrowerRepository borrowerRepo;
    private InMemoryLoanRepository loanRepo;
    private InMemoryLenderRepository lenderRepo;
    private InMemoryInvestmentRepository investmentRepo;
    private InMemmoryPaymentRepository paymentRepo;

    // ─── Use Cases ─────────────────────────────────────────────────
    private RegisterBorrowerUseCase registerBorrowerUseCase;
    private ApplyLoanUseCaseImpl applyLoanUseCase;
    private ApproveLoanUseCaseImpl approveLoanUseCase;
    private DisburseUseCaseImpl disburseUseCase;
    private CancelLoanUseCaseImpl cancelLoanUseCase;
    private RegisterLenderUseCaseImpl registerLenderUseCase;
    private InvestLoanUseCaseImpl investLoanUseCase;
    private MakePaymentUseCaseImpl makePaymentUseCase;
    private GetPaymentScheduleUseCaseImpl getPaymentScheduleUseCase;

    // ─── Test Data Constants ───────────────────────────────────────
    private static final String KTP_IMAN  = "1111222233334444";
    private static final String KTP_BUDI  = "5555666677778888";
    private static final String KTP_KEMAL = "3333444455556666";

    @BeforeEach
    void setUp() {
        // Bersihkan storage sebelum tiap test
        SharedStorage.getInstance().getBorrowers().clear();
        SharedStorage.getInstance().getLoans().clear();
        SharedStorage.getInstance().getLenders().clear();
        SharedStorage.getInstance().getInvestments().clear();
        SharedStorage.getInstance().getPayments().clear();

        // Inisialisasi repository
        borrowerRepo   = new InMemoryBorrowerRepository();
        loanRepo       = new InMemoryLoanRepository();
        lenderRepo     = new InMemoryLenderRepository();
        investmentRepo = new InMemoryInvestmentRepository();
        paymentRepo    = new InMemmoryPaymentRepository();

        // Inisialisasi service
        LoanApprovalService     loanApprovalService     = new LoanApprovalService();
        LoanCancellationService loanCancellationService = new LoanCancellationService();
        InvestmentService       investmentService       = new InvestmentService();
        RepaymentService        repaymentService        = new RepaymentService();
        PaymentScheduleService  paymentScheduleService  = new PaymentScheduleService();

        // Inisialisasi use case
        registerBorrowerUseCase  = new RegisterBorrowerUseCase(borrowerRepo);
        applyLoanUseCase         = new ApplyLoanUseCaseImpl(borrowerRepo, loanRepo, loanApprovalService);
        approveLoanUseCase       = new ApproveLoanUseCaseImpl(loanRepo);
        disburseUseCase          = new DisburseUseCaseImpl(loanRepo);
        cancelLoanUseCase        = new CancelLoanUseCaseImpl(borrowerRepo, loanRepo, loanCancellationService, investmentRepo);
        registerLenderUseCase    = new RegisterLenderUseCaseImpl(lenderRepo);
        investLoanUseCase        = new InvestLoanUseCaseImpl(lenderRepo, loanRepo, investmentRepo, investmentService);
        makePaymentUseCase       = new MakePaymentUseCaseImpl(borrowerRepo, loanRepo, paymentRepo, repaymentService);
        getPaymentScheduleUseCase = new GetPaymentScheduleUseCaseImpl(borrowerRepo, loanRepo, paymentRepo, paymentScheduleService);
    }

    // SKENARIO 1 — HAPPY PATH

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

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        assertEquals(LoanStatus.FUNDING, getLoanStatus(loan.getId()));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")));
        assertEquals(new BigDecimal("50000000"), budi.getSaldo());

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("30000000")));

        BigDecimal saldoBudiSetelah = lenderRepo.findById(budi.getId()).get().getSaldo().getAmount();
        assertEquals(new BigDecimal("20000000"), saldoBudiSetelah);

        assertEquals(LoanStatus.FUNDED, getLoanStatus(loan.getId()));

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

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), false));
        assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()));
    }

    // SKENARIO 2 — CANCELLATION

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

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000")));

        int countBefore = getCancellationCount(iman.getId());
        assertEquals(0, countBefore);

        cancelLoanUseCase.execute(new CancelLoanCommand(
                iman.getId(), loan.getId(), new Money(new BigDecimal("6000000"), "IDR")));

        assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()));

        LoanApplication cancelledLoan = loanRepo.findById(loan.getId()).get();
        assertNotNull(cancelledLoan.getCancelledDate());

        int countAfter = getCancellationCount(iman.getId());
        assertEquals(countBefore + 1, countAfter);

        Borrower borrower = borrowerRepo.findById(iman.getId()).get();
        assertNull(borrower.getLastBlockedDate());
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
            assertEquals(i, getCancellationCount(iman.getId()));
        }

        Borrower borrowerAfter = borrowerRepo.findById(iman.getId()).get();
        assertNotNull(borrowerAfter.getLastBlockedDate());
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
        assertNotNull(blockedBorrower.getLastBlockedDate());
    }

    //SKENARIO 3 — EXPIRED FUNDING

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
                () -> disburseUseCase.execute(new DisburseLoanCommand(loan.getId())));
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
                () -> approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true)));
    }

    // SKENARIO 4 — PAYMENT ON TIME

    @Test
    @Order(10)
    @DisplayName("Skenario 4: Borrower bayar cicilan tepat waktu → status PAID")
    void scenario4_paymentOnTime_statusPaid() {
        // Setup: register, apply, approve, invest, disburse
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 750));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 12_000_000L, 12));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")));

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("12000000")));

        assertEquals(LoanStatus.FUNDED, getLoanStatus(loan.getId()));

        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        assertEquals(LoanStatus.DISBURSED, getLoanStatus(loan.getId()));

        // Ambil jadwal cicilan
        List<PaymentDTO> schedule = getPaymentScheduleUseCase.execute(iman.getId(), loan.getId());
        assertNotNull(schedule);
        assertFalse(schedule.isEmpty());
        assertEquals(12, schedule.size()); // tenor 12 bulan = 12 cicilan

        // Ambil cicilan pertama
        PaymentDTO firstPaymentDTO = schedule.get(0);
        assertEquals("PENDING", firstPaymentDTO.getStatus());

        // Bayar cicilan pertama — amount = pokok cicilan (tidak overdue, denda = 0)
        MakePaymentCommand paymentCommand = new MakePaymentCommand(
                iman.getId(),
                firstPaymentDTO.getId(),
                new Money(firstPaymentDTO.getAmount(), "IDR")
        );
        PaymentDTO result = makePaymentUseCase.execute(paymentCommand);

        // Pastikan status jadi PAID
        assertEquals("PAID", result.getStatus());
        assertNotNull(result.getPaidDate());
        assertEquals(BigDecimal.ZERO, result.getDenda());
    }

    // SKENARIO 5 — PAYMENT OVERDUE + DENDA

    @Test
    @Order(11)
    @DisplayName("Skenario 5: Payment telat 40 hari → status OVERDUE, denda 1% pokok")
    void scenario5_paymentOverdue_dendaApplied() {
        // Setup: register, apply, approve, invest, disburse
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 750));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 12_000_000L, 12));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", KTP_BUDI,
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")));

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("12000000")));

        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        assertEquals(LoanStatus.DISBURSED, getLoanStatus(loan.getId()));

        // Generate jadwal cicilan
        List<PaymentDTO> schedule = getPaymentScheduleUseCase.execute(iman.getId(), loan.getId());
        assertNotNull(schedule);
        assertFalse(schedule.isEmpty());

        // Ambil cicilan pertama dari repository langsung
        // supaya bisa manipulasi due date jadi 40 hari yang lalu
        String firstPaymentId = schedule.get(0).getId();
        Payment firstPayment = paymentRepo.findById(firstPaymentId).get();

        // Simulasi payment sudah telat 40 hari
        // dengan membuat payment baru dengan due date 40 hari lalu
        Payment overduePayment = new Payment(
                firstPayment.getId(),
                firstPayment.getLoanId(),
                firstPayment.getNoBulan(),
                firstPayment.getAmount(),
                LocalDate.now().minusDays(40) // due date 40 hari lalu
        );
        paymentRepo.save(overduePayment); // overwrite dengan yang overdue

        // Hitung total yang harus dibayar: pokok + denda (1% dari pokok)
        BigDecimal pokok = overduePayment.getAmount().getAmount();
        BigDecimal expectedDenda = pokok.multiply(new BigDecimal("0.01"));
        BigDecimal totalBayar = pokok.add(expectedDenda);

        // Bayar cicilan yang sudah overdue
        MakePaymentCommand paymentCommand = new MakePaymentCommand(
                iman.getId(),
                firstPaymentId,
                new Money(totalBayar, "IDR")
        );
        PaymentDTO result = makePaymentUseCase.execute(paymentCommand);

        // Pastikan status PAID setelah dibayar
        assertEquals("PAID", result.getStatus());
        assertNotNull(result.getPaidDate());

        // Pastikan denda dihitung dengan benar (1% dari pokok)
        assertTrue(result.getDenda().compareTo(BigDecimal.ZERO) > 0);
    }

    // EDGE CASES

    @Test
    @Order(12)
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

        assertThrows(IllegalArgumentException.class,
                () -> investLoanUseCase.execute(
                        new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("5000000"))));
    }

    @Test
    @Order(13)
    @DisplayName("Edge case: Lender saldo tidak cukup → invest ditolak")
    void edgeCase_insufficientSaldo_investRejected() {
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", KTP_IMAN,
                "Jl. Merdeka No. 1", 10_000_000L, 700));
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
    @Order(14)
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

        assertThrows(IllegalArgumentException.class,
                () -> investLoanUseCase.execute(
                        new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("10000000"))));
    }

    @Test
    @Order(15)
    @DisplayName("Edge case: Borrower tidak ditemukan saat apply → exception")
    void edgeCase_borrowerNotFound_applyLoan() {
        assertThrows(IllegalArgumentException.class,
                () -> applyLoanUseCase.execute(
                        new ApplyLoanCommand("BORROWER-TIDAK-ADA", 10_000_000L, 6)));
    }

    // ══════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ══════════════════════════════════════════════════════════════

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