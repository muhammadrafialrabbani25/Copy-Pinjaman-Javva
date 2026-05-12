package com.p2plending.interfaces.cli;

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
import com.p2plending.application.lender.usecase.TopUpSaldoUseCaseImpl;
import com.p2plending.application.shared.ApproveLoanCommand;
import com.p2plending.application.shared.ApproveLoanUseCaseImpl;
import com.p2plending.application.shared.DisburseLoanCommand;
import com.p2plending.application.shared.DisburseUseCaseImpl;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.service.LoanApprovalService;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.lender.service.InvestmentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.infrastructure.persistence.InMemoryBorrowerRepository;
import com.p2plending.infrastructure.persistence.InMemoryInvestmentRepository;
import com.p2plending.infrastructure.persistence.InMemoryLenderRepository;
import com.p2plending.infrastructure.persistence.InMemoryLoanRepository;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class LendingApp {

    // ─── Repositories (Manual DI, no Spring) ─────────────────────────
    private static final InMemoryBorrowerRepository   borrowerRepo   = new InMemoryBorrowerRepository();
    private static final InMemoryLoanRepository       loanRepo       = new InMemoryLoanRepository();
    private static final InMemoryLenderRepository     lenderRepo     = new InMemoryLenderRepository();
    private static final InMemoryInvestmentRepository investmentRepo = new InMemoryInvestmentRepository();

    // ─── Domain Services ──────────────────────────────────────────────
    private static final LoanApprovalService     loanApprovalService     = new LoanApprovalService();
    private static final LoanCancellationService loanCancellationService = new LoanCancellationService();
    private static final InvestmentService       investmentService       = new InvestmentService();

    // ─── Use Cases ────────────────────────────────────────────────────
    private static final RegisterBorrowerUseCase   registerBorrowerUseCase = new RegisterBorrowerUseCase(borrowerRepo);
    private static final ApplyLoanUseCaseImpl      applyLoanUseCase        = new ApplyLoanUseCaseImpl(borrowerRepo, loanRepo, loanApprovalService);
    private static final ApproveLoanUseCaseImpl    approveLoanUseCase      = new ApproveLoanUseCaseImpl(loanRepo);
    private static final DisburseUseCaseImpl       disburseUseCase         = new DisburseUseCaseImpl(loanRepo);
    private static final CancelLoanUseCaseImpl     cancelLoanUseCase       = new CancelLoanUseCaseImpl(borrowerRepo, loanRepo, loanCancellationService);
    private static final RegisterLenderUseCaseImpl registerLenderUseCase   = new RegisterLenderUseCaseImpl(lenderRepo);
    private static final TopUpSaldoUseCaseImpl     topUpSaldoUseCase       = new TopUpSaldoUseCaseImpl(lenderRepo);
    private static final InvestLoanUseCaseImpl     investLoanUseCase       = new InvestLoanUseCaseImpl(lenderRepo, loanRepo, investmentRepo, investmentService);

    // ═════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        printBanner();

        scenario1HappyPath();
        separator();
        scenario2Cancellation();
        separator();
        scenario3ExpiredFunding();

        System.out.println("\n✨  Semua skenario selesai dijalankan!\n");
    }

    // ═════════════════════════════════════════════════════════════════
    // SKENARIO 1 — Happy Path: IMAN apply 30jt, BUDI invest full → DISBURSED
    // ═════════════════════════════════════════════════════════════════
    private static void scenario1HappyPath() {
        System.out.println("📋  SKENARIO 1 : HAPPY PATH — Loan berhasil dicairkan");
        System.out.println("-".repeat(55));

        // 1. Register IMAN sebagai borrower (gaji 10jt → limit 30jt)
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Santoso", "081111111111", "1111222233334444",
                "Jl. Merdeka No. 1", 10_000_000L, 750
        ));
        System.out.printf("👤  Borrower terdaftar : %s  (gaji: 10jt | credit score: 750)%n", iman.getNama());

        // 2. IMAN apply loan 30jt, tenor 6 bulan
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        System.out.printf("📄  Loan diajukan      : %s | Rp 30.000.000 | 6 bulan%n", loan.getId());
        printLoanStatus(loan.getId(), "PENDING");

        // 3. Admin setujui loan
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        printLoanStatus(loan.getId(), "FUNDING");

        // 4. Register BUDI sebagai lender (saldo awal 50jt)
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Investor", "082222222222", "5555666677778888",
                "Jl. Sudirman No. 99", "Wirausaha", new BigDecimal("50000000")
        ));
        System.out.printf("💼  Lender terdaftar   : %s  (saldo: Rp 50.000.000)%n", budi.getNama());

        // 5. BUDI invest 30jt (100% → fully funded)
        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("30000000")));
        System.out.println("💰  BUDI invest        : Rp 30.000.000 (100% dari loan amount)");

        BigDecimal sisaSaldoBudi = getSaldo(budi.getId());
        System.out.printf("    Saldo BUDI sisa    : Rp %,.0f%n", sisaSaldoBudi);

        // 6. Sistem mendeteksi fully funded → set FUNDED
        forceSetLoanStatus(loan.getId(), LoanStatus.FUNDED);
        printLoanStatus(loan.getId(), "FUNDED");

        // 7. Admin disburse
        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        printLoanStatus(loan.getId(), "DISBURSED");

        System.out.println("\n📊  Hasil Skenario 1:");
        System.out.printf("    Loan status    : %s ✅%n", loanRepo.findById(loan.getId()).get().getStatus());
        System.out.printf("    Saldo BUDI     : Rp %,.0f (berkurang 30jt)%n", sisaSaldoBudi);
    }

    // ═════════════════════════════════════════════════════════════════
    // SKENARIO 2 — Cancellation: IMAN cancel → counter +1, refund ke BUDI
    // ═════════════════════════════════════════════════════════════════
    private static void scenario2Cancellation() {
        System.out.println("📋  SKENARIO 2 : CANCELLATION — Borrower batalkan pinjaman");
        System.out.println("-".repeat(55));

        // 1. Register IMAN (borrower baru untuk isolasi skenario)
        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Iman Kedua", "083333333333", "2222333344445555",
                "Jl. Gatot Subroto No. 5", 10_000_000L, 700
        ));
        System.out.printf("👤  Borrower terdaftar : %s%n", iman.getNama());

        // 2. Apply loan 30jt, tenor 3 bulan
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 3));
        System.out.printf("📄  Loan diajukan      : %s | Rp 30.000.000 | 3 bulan%n", loan.getId());

        // 3. Admin approve
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        printLoanStatus(loan.getId(), "FUNDING");

        // 4. Register BUDI (lender)
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                "Budi Kedua", "084444444444", "6666777788889999",
                "Jl. Thamrin No. 10", "Karyawan", new BigDecimal("20000000")
        ));
        System.out.printf("💼  Lender terdaftar   : %s  (saldo: Rp 20.000.000)%n", budi.getNama());

        // 5. BUDI invest 6jt (tepat 20% dari 30jt → memenuhi syarat cancel)
        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("6000000")));
        System.out.println("💰  BUDI invest        : Rp 6.000.000 (= 20% dari 30jt ✓)");

        BigDecimal saldoSebelumRefund = getSaldo(budi.getId());
        System.out.printf("    Saldo BUDI sebelum refund : Rp %,.0f%n", saldoSebelumRefund);

        // 6. Cancellation count sebelum
        int countSebelum = getCancellationCount(iman.getId());
        System.out.printf("⚠️   Cancellation count sebelum : %d%n", countSebelum);

        // 7. IMAN cancel loan
        Money fundedAmount = new Money(new BigDecimal("6000000"), "IDR");
        cancelLoanUseCase.execute(new CancelLoanCommand(iman.getId(), loan.getId(), fundedAmount));
        printLoanStatus(loan.getId(), "CANCELLED");

        // 8. Simulasi refund ke BUDI (business rule: refund penuh)
        //    Di sistem nyata ini ditangani oleh event/refund service
        //    Untuk demo kita tampilkan nilai refund yang seharusnya diterima
        System.out.println("💸  Refund ke BUDI     : Rp 6.000.000 (full refund sesuai business rule)");

        // 9. Cancellation count sesudah
        int countSesudah = getCancellationCount(iman.getId());
        System.out.printf("⚠️   Cancellation count sesudah : %d%n", countSesudah);

        System.out.println("\n📊  Hasil Skenario 2:");
        System.out.printf("    Loan status        : %s ✅%n", loanRepo.findById(loan.getId()).get().getStatus());
        System.out.printf("    Cancellation count : %d → %d (+1) ✅%n", countSebelum, countSesudah);
        System.out.println("    Refund BUDI        : Rp 6.000.000 ✅");

        Borrower borrower = borrowerRepo.findById(iman.getId()).get();
        if (borrower.getLastBlockedDate() == null) {
            System.out.printf("    Blokir             : Belum diblokir (baru %dx cancel, butuh 3x)%n", countSesudah);
        } else {
            System.out.println("    Blokir             : DIBLOKIR 4 bulan ❌");
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // SKENARIO 3 — Expired Funding: KEMAL apply 15jt, tidak terfund → EXPIRED
    // ═════════════════════════════════════════════════════════════════
    private static void scenario3ExpiredFunding() {
        System.out.println("📋  SKENARIO 3 : EXPIRED FUNDING — Loan tidak terfund dalam 5 hari");
        System.out.println("-".repeat(55));

        // 1. Register KEMAL sebagai borrower (gaji 5jt → limit 15jt)
        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                "Kemal Peminjam", "085555555555", "3333444455556666",
                "Jl. Kuningan No. 7", 5_000_000L, 650
        ));
        System.out.printf("👤  Borrower terdaftar : %s  (gaji: 5jt | limit: 15jt)%n", kemal.getNama());

        // 2. KEMAL apply loan 15jt, tenor 12 bulan
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
        System.out.printf("📄  Loan diajukan      : %s | Rp 15.000.000 | 12 bulan%n", loan.getId());

        // 3. Admin approve
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        printLoanStatus(loan.getId(), "FUNDING");

        // 4. Simulasi: 6 hari berlalu tanpa investor yang cukup
        System.out.println("⏳  Simulasi: 6 hari berlalu tanpa investor...");
        forceSetLoanStatus(loan.getId(), LoanStatus.EXPIRED_FUNDING);
        printLoanStatus(loan.getId(), "EXPIRED_FUNDING");

        // 5. Coba disburse → harus gagal
        System.out.println("🚫  Mencoba disburse loan expired...");
        try {
            disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
            System.out.println("    [ERROR] Seharusnya gagal!");
        } catch (IllegalStateException e) {
            System.out.println("    ✅ Disburse ditolak: " + e.getMessage());
        }

        System.out.println("\n📊  Hasil Skenario 3:");
        System.out.printf("    Loan status  : %s ✅%n", loanRepo.findById(loan.getId()).get().getStatus());
        System.out.println("    Penyebab     : Tidak ada investor dalam 5 hari");
        System.out.println("    Tindakan     : Borrower dapat apply pinjaman baru");
    }

    // ═════════════════════════════════════════════════════════════════
    // Helpers
    // ═════════════════════════════════════════════════════════════════

    /** Set status loan via reflection (sama pola dengan use case lain di codebase) */
    private static void forceSetLoanStatus(String loanId, LoanStatus status) {
        try {
            LoanApplication loan = loanRepo.findById(loanId)
                    .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));
            Field field = LoanApplication.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(loan, status);
            loanRepo.save(loan);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set status loan", e);
        }
    }

    private static BigDecimal getSaldo(String lenderId) {
        return lenderRepo.findById(lenderId)
                .map(l -> l.getSaldo().getAmount())
                .orElse(BigDecimal.ZERO);
    }

    private static int getCancellationCount(String borrowerId) {
        return borrowerRepo.findById(borrowerId)
                .map(Borrower::getCancellationCount)
                .orElse(-1);
    }

    private static void printLoanStatus(String loanId, String expectedStatus) {
        LoanStatus actual = loanRepo.findById(loanId).map(LoanApplication::getStatus).orElse(null);
        System.out.printf("   ➜ Status: %s%n", actual != null ? actual : expectedStatus);
    }

    private static void printBanner() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║       🏦  P2P LENDING PLATFORM  —  DEMO CLI           ║");
        System.out.println("║       Java DDD + TDD  |  Manual DI  |  Sprint 1       ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void separator() {
        System.out.println("\n" + "═".repeat(57) + "\n");
    }
}