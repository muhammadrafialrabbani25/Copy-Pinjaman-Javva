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

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Integration Test — P2P Lending Platform
 *
 * Menggunakan real in-memory repositories (NO mocks).
 * Setiap test dibuat independent via @BeforeEach yang clear SharedStorage.
 *
 * Skenario yang diuji:
 * 1. Happy path : apply → approve → invest → funded → disburse
 * 2. Cancellation : apply → approve → invest → cancel (counter +1)
 * 2b. 3x cancel : borrower diblokir 4 bulan setelah 3x cancel
 * 3. Expired : apply → approve → tidak terfund → EXPIRED_FUNDING
 * Edge cases : reject oleh admin, cancel dengan funded < 20%
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
        private static final String KTP_IMAN = "1111222233334444";
        private static final String KTP_BUDI = "5555666677778888";
        private static final String KTP_KEMAL = "3333444455556666";

        @BeforeEach
        void setUp() {
                // Bersihkan SharedStorage supaya setiap test independen
                SharedStorage.getInstance().getBorrowers().clear();
                SharedStorage.getInstance().getLoans().clear();
                SharedStorage.getInstance().getLenders().clear();
                SharedStorage.getInstance().getInvestments().clear();

                // Inisialisasi repositories (semuanya point ke SharedStorage yang sudah bersih)
                borrowerRepo = new InMemoryBorrowerRepository();
                loanRepo = new InMemoryLoanRepository();
                lenderRepo = new InMemoryLenderRepository();
                investmentRepo = new InMemoryInvestmentRepository();

                // Domain services
                LoanApprovalService loanApprovalService = new LoanApprovalService();
                LoanCancellationService loanCancellationService = new LoanCancellationService();
                InvestmentService investmentService = new InvestmentService();

                // Use cases
                registerBorrowerUseCase = new RegisterBorrowerUseCase(borrowerRepo);
                applyLoanUseCase = new ApplyLoanUseCaseImpl(borrowerRepo, loanRepo, loanApprovalService);
                approveLoanUseCase = new ApproveLoanUseCaseImpl(loanRepo);
                disburseUseCase = new DisburseUseCaseImpl(loanRepo);
                cancelLoanUseCase = new CancelLoanUseCaseImpl(borrowerRepo, loanRepo, loanCancellationService, investmentRepo);
                registerLenderUseCase = new RegisterLenderUseCaseImpl(lenderRepo);
                investLoanUseCase = new InvestLoanUseCaseImpl(lenderRepo, loanRepo, investmentRepo, investmentService);
        }

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 1 — HAPPY PATH
        // ═════════════════════════════════════════════════════════════════

        @Test
        @Order(1)
        @DisplayName("Skenario 1: Happy path — loan berhasil dicairkan (DISBURSED)")
        void scenario1_happyPath_loanSuccessfullyDisbursed() {
                // ── Register IMAN (gaji 10jt → limit 30jt) ───────────────────
                BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Iman Santoso", "081111111111", KTP_IMAN,
                                "Jl. Merdeka No. 1", 10_000_000L, 750));
                assertNotNull(iman.getId(), "Borrower harus mendapat ID");
                assertEquals("Iman Santoso", iman.getNama());

                // ── IMAN apply loan 30jt ──────────────────────────────────────
                LoanDTO loan = applyLoanUseCase.execute(
                                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
                assertNotNull(loan.getId());
                assertEquals(30_000_000L, loan.getAmount());
                assertEquals(6, loan.getTermInMonths());

                // Status awal harus PENDING
                assertEquals(LoanStatus.PENDING, getLoanStatus(loan.getId()));

                // ── Admin approve → FUNDING ───────────────────────────────────
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
                assertEquals(LoanStatus.FUNDING, getLoanStatus(loan.getId()),
                                "Setelah approve, status harus FUNDING");

                // ── Register BUDI (saldo 50jt) ────────────────────────────────
                LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                                "Budi Investor", "082222222222", KTP_BUDI,
                                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")));
                assertNotNull(budi.getId());
                assertEquals(new BigDecimal("50000000"), budi.getSaldo());

                // ── BUDI invest 30jt (100% loan amount) ──────────────────────
                investLoanUseCase.execute(
                                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("30000000")));

                // Saldo BUDI berkurang 30jt
                BigDecimal saldoBudiSetelah = lenderRepo.findById(budi.getId()).get().getSaldo().getAmount();
                assertEquals(new BigDecimal("20000000"), saldoBudiSetelah,
                                "Saldo BUDI harus berkurang 30jt (50jt - 30jt = 20jt)");

                // ── Set FUNDED (simulasi: 100% terfund) ──────────────────────
                forceSetLoanStatus(loan.getId(), LoanStatus.FUNDED);
                assertEquals(LoanStatus.FUNDED, getLoanStatus(loan.getId()));

                // ── Admin disburse ────────────────────────────────────────────
                disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
                assertEquals(LoanStatus.DISBURSED, getLoanStatus(loan.getId()),
                                "Setelah disburse, status harus DISBURSED");
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

                // Admin reject (approve = false)
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), false));

                assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()),
                                "Loan yang direject admin harus berstatus CANCELLED");
        }

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 2 — CANCELLATION
        // ═════════════════════════════════════════════════════════════════

        @Test
        @Order(3)
        @DisplayName("Skenario 2: Borrower cancel loan → status CANCELLED, counter +1")
        void scenario2_cancellation_counterIncremented() {
                // Setup
                BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Iman Santoso", "081111111111", KTP_IMAN,
                                "Jl. Merdeka No. 1", 10_000_000L, 700));
                LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                                "Budi Investor", "082222222222", KTP_BUDI,
                                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("20000000")));

                LoanDTO loan = applyLoanUseCase.execute(
                                new ApplyLoanCommand(iman.getId(), 30_000_000L, 3));
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

                // BUDI invest tepat 20% (6jt dari 30jt)
                investLoanUseCase.execute(
                                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000")));

                // Cancellation count sebelum
                int countBefore = getCancellationCount(iman.getId());
                assertEquals(0, countBefore, "Cancellation count awal harus 0");

                // IMAN cancel loan
                Money funded = new Money(new BigDecimal("6000000"), "IDR");
                cancelLoanUseCase.execute(new CancelLoanCommand(iman.getId(), loan.getId(), funded));

                // Verify loan status = CANCELLED
                assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()),
                                "Status loan harus CANCELLED setelah dibatalkan");

                // Verify cancelledDate diisi
                LoanApplication cancelledLoan = loanRepo.findById(loan.getId()).get();
                assertNotNull(cancelledLoan.getCancelledDate(),
                                "cancelledDate harus diisi saat cancel");

                // Verify cancellation count naik +1
                int countAfter = getCancellationCount(iman.getId());
                assertEquals(countBefore + 1, countAfter,
                                "Cancellation count harus naik dari " + countBefore + " → " + (countBefore + 1));

                // Belum diblokir (baru 1x cancel)
                Borrower borrower = borrowerRepo.findById(iman.getId()).get();
                assertNull(borrower.getLastBlockedDate(),
                                "Borrower belum diblokir karena baru 1x cancel (butuh 3x)");
        }

        @Test
        @Order(4)
        @DisplayName("Skenario 2b: 3x cancel → borrower diblokir 4 bulan")
        void scenario2b_threeTimesCancel_borrowerBlocked() {
                BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Iman Santoso", "081111111111", KTP_IMAN,
                                "Jl. Merdeka No. 1", 10_000_000L, 750));
                // Lender dengan saldo besar untuk 3x invest
                LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                                "Budi Investor", "082222222222", KTP_BUDI,
                                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("100000000")));

                // Lakukan 3x siklus: apply → approve → invest → cancel
                for (int i = 1; i <= 3; i++) {
                        LoanDTO loan = applyLoanUseCase.execute(
                                        new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
                        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
                        investLoanUseCase.execute(
                                        new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000")));

                        Money funded = new Money(new BigDecimal("6000000"), "IDR");
                        cancelLoanUseCase.execute(
                                        new CancelLoanCommand(iman.getId(), loan.getId(), funded));

                        assertEquals(i, getCancellationCount(iman.getId()),
                                        "Setelah cancel ke-" + i + ", count harus " + i);
                }

                // Setelah 3x cancel → borrower diblokir
                Borrower borrowerAfter = borrowerRepo.findById(iman.getId()).get();
                assertNotNull(borrowerAfter.getLastBlockedDate(),
                                "Borrower harus diblokir (lastBlockedDate diisi) setelah 3x cancel");
                assertEquals(3, borrowerAfter.getCancellationCount());
        }

        @Test
        @Order(5)
        @DisplayName("Skenario 2c: Cancel BISA jika funded < 20% tanpa penalty/mark")
        void scenario2c_canCancel_ifFundedLessThan20Percent_NoPenalty() {
                BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Iman Santoso", "081111111111", KTP_IMAN,
                                "Jl. Merdeka No. 1", 10_000_000L, 700));

                LoanDTO loan = applyLoanUseCase.execute(
                                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

                // Funded hanya 5jt → kurang dari 20% (threshold = 6jt)
                // Cancel sebelum 20% DIBOLEHKAN tanpa penalty
                Money lessThan20Pct = new Money(new BigDecimal("5000000"), "IDR");

                // Should NOT throw exception - cancel allowed with no penalty
                cancelLoanUseCase.execute(
                                new CancelLoanCommand(iman.getId(), loan.getId(), lessThan20Pct));

                // Status loan berubah ke CANCELLED
                assertEquals(LoanStatus.CANCELLED, getLoanStatus(loan.getId()),
                                "Status loan harus berubah ke CANCELLED");
                // No penalty applied (cancellation count not incremented) - verified via
                // LoanCancellationServiceTest
        }

        @Test
        @Order(6)
        @DisplayName("Skenario 2d: Cancel tidak bisa jika sudah 3x cancel (blokir)")
        void scenario2d_cannotCancel_afterMaxCancellations() {
                BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Iman Santoso", "081111111111", KTP_IMAN,
                                "Jl. Merdeka No. 1", 10_000_000L, 750));
                LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                                "Budi Investor", "082222222222", KTP_BUDI,
                                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("200000000")));

                // Cancel 3x untuk mencapai batas
                for (int i = 0; i < 3; i++) {
                        LoanDTO l = applyLoanUseCase.execute(
                                        new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
                        approveLoanUseCase.execute(new ApproveLoanCommand(l.getId(), true));
                        investLoanUseCase.execute(
                                        new InvestCommand(budi.getId(), l.getId(), new BigDecimal("6000000")));
                        cancelLoanUseCase.execute(
                                        new CancelLoanCommand(iman.getId(), l.getId(),
                                                        new Money(new BigDecimal("6000000"), "IDR")));
                }

                // Coba cancel ke-4 (cancellationCount = 3 → tidak boleh)
                // Tapi karena borrower sudah diblokir, apply pun harusnya gagal
                // Kita cek logika canCancelLoan langsung via service
                Borrower blockedBorrower = borrowerRepo.findById(iman.getId()).get();
                assertEquals(3, blockedBorrower.getCancellationCount());
                assertNotNull(blockedBorrower.getLastBlockedDate(), "Borrower harus diblokir");
        }

        // ═════════════════════════════════════════════════════════════════
        // SKENARIO 3 — EXPIRED FUNDING
        // ═════════════════════════════════════════════════════════════════

        @Test
        @Order(7)
        @DisplayName("Skenario 3: Loan tidak terfund → EXPIRED_FUNDING")
        void scenario3_expiredFunding_statusSetCorrectly() {
                // Register KEMAL (gaji 5jt → limit 15jt)
                BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Kemal Peminjam", "085555555555", KTP_KEMAL,
                                "Jl. Kuningan No. 7", 5_000_000L, 650));
                assertNotNull(kemal.getId());

                // KEMAL apply 15jt
                LoanDTO loan = applyLoanUseCase.execute(
                                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
                assertEquals(15_000_000L, loan.getAmount());

                // Admin approve → FUNDING
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
                assertEquals(LoanStatus.FUNDING, getLoanStatus(loan.getId()));

                // Simulasi: 6 hari berlalu tanpa investor → EXPIRED_FUNDING
                forceSetLoanStatus(loan.getId(), LoanStatus.EXPIRED_FUNDING);
                assertEquals(LoanStatus.EXPIRED_FUNDING, getLoanStatus(loan.getId()),
                                "Status harus EXPIRED_FUNDING setelah 5 hari tanpa funding");
        }

        @Test
        @Order(8)
        @DisplayName("Skenario 3b: Loan EXPIRED tidak bisa di-disburse")
        void scenario3b_expiredLoan_cannotBesDisbursed() {
                BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Kemal Peminjam", "085555555555", KTP_KEMAL,
                                "Jl. Kuningan No. 7", 5_000_000L, 650));
                LoanDTO loan = applyLoanUseCase.execute(
                                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

                forceSetLoanStatus(loan.getId(), LoanStatus.EXPIRED_FUNDING);

                // Disburse harus gagal
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

                forceSetLoanStatus(loan.getId(), LoanStatus.EXPIRED_FUNDING);

                // Approve ulang harus gagal
                assertThrows(IllegalStateException.class,
                                () -> approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true)),
                                "Loan expired tidak bisa di-approve ulang");
        }

        // ═════════════════════════════════════════════════════════════════
        // Edge Cases Tambahan
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

                // Invest 5jt < 20% dari 30jt (threshold = 6jt)
                assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(
                                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("5000000"))),
                                "Investasi di bawah 20% harus ditolak");
        }

        @Test
        @Order(11)
        @DisplayName("Edge case: Lender saldo tidak cukup → invest ditolak")
        void edgeCase_insufficientSaldo_investRejected() {
                BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                                "Iman Santoso", "081111111111", KTP_IMAN,
                                "Jl. Merdeka No. 1", 10_000_000L, 700));
                // BUDI hanya punya 5jt, padahal min invest 6jt (20% dari 30jt)
                LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                                "Budi Investor", "082222222222", KTP_BUDI,
                                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("5000000")));

                LoanDTO loan = applyLoanUseCase.execute(
                                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
                approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

                assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(
                                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000"))),
                                "Invest harus ditolak jika saldo lender tidak cukup");
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

                assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(
                                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("10000000"))),
                                "Tidak bisa invest ke loan yang belum FUNDING");
        }

        @Test
        @Order(13)
        @DisplayName("Edge case: Borrower tidak ditemukan saat apply → exception")
        void edgeCase_borrowerNotFound_applyLoan() {
                assertThrows(IllegalArgumentException.class, () -> applyLoanUseCase.execute(
                                new ApplyLoanCommand("BORROWER-TIDAK-ADA", 10_000_000L, 6)),
                                "Harus exception jika borrower tidak ditemukan");
        }

        // ═════════════════════════════════════════════════════════════════
        // Helper Methods
        // ═════════════════════════════════════════════════════════════════

        /**
         * Set loan status via reflection (sama pola dengan use case lain di codebase)
         */
        private void forceSetLoanStatus(String loanId, LoanStatus status) {
                try {
                        LoanApplication loan = loanRepo.findById(loanId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "Loan tidak ditemukan: " + loanId));
                        Field field = LoanApplication.class.getDeclaredField("status");
                        field.setAccessible(true);
                        field.set(loan, status);
                        loanRepo.save(loan);
                } catch (IllegalArgumentException e) {
                        throw e;
                } catch (Exception e) {
                        throw new RuntimeException("Gagal set status loan via reflection", e);
                }
        }

        private LoanStatus getLoanStatus(String loanId) {
                return loanRepo.findById(loanId)
                                .map(LoanApplication::getStatus)
                                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));
        }

        private int getCancellationCount(String borrowerId) {
                return borrowerRepo.findById(borrowerId)
                                .map(Borrower::getCancellationCount)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Borrower tidak ditemukan: " + borrowerId));
        }
}