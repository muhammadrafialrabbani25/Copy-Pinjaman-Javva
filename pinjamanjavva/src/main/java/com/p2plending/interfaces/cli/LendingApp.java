package com.p2plending.interfaces.cli;

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
import com.p2plending.domain.borrower.service.LoanApprovalService;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.borrower.service.PaymentScheduleService;
import com.p2plending.domain.borrower.service.RepaymentService;
import com.p2plending.domain.lender.service.InvestmentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.infrastructure.persistence.InMemoryBorrowerRepository;
import com.p2plending.infrastructure.persistence.InMemoryInvestmentRepository;
import com.p2plending.infrastructure.persistence.InMemoryLenderRepository;
import com.p2plending.infrastructure.persistence.InMemoryLoanRepository;
import com.p2plending.infrastructure.persistence.InMemmoryPaymentRepository;
import com.p2plending.infrastructure.persistence.SharedStorage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class LendingApp {

    private static final Logger LOGGER = Logger.getLogger(LendingApp.class.getName());

    private static final String KTP_IMAN  = "1111222233334444";
    private static final String KTP_BUDI  = "5555666677778888";
    private static final String KTP_KEMAL = "3333444455556666";

    // Borrower IMAN
    private static final String NAMA_IMAN    = "Iman Santoso";
    private static final String TELEPON_IMAN = "081111111111";
    private static final String ALAMAT_IMAN  = "Jl. Merdeka No. 1";

    // Borrower KEMAL
    private static final String NAMA_KEMAL    = "Kemal Peminjam";
    private static final String TELEPON_KEMAL = "085555555555";
    private static final String ALAMAT_KEMAL  = "Jl. Kuningan No. 7";

    // Lender BUDI
    private static final String NAMA_BUDI      = "Budi Investor";
    private static final String TELEPON_BUDI   = "082222222222";
    private static final String ALAMAT_BUDI    = "Jl. Sudirman No. 99";
    private static final String PEKERJAAN_BUDI = "Wirausaha";

    // Nominal
    private static final String NOMINAL_6JT  = "6000000";
    private static final String NOMINAL_5JT  = "5000000";

    // Format output
    private static final String FMT_STATUS   = "Status: %s%n";
    private static final String FMT_PESAN    = "Pesan: %s%n";
    private static final String FMT_HASIL    = "\nHasil";
    private static final String INVEST_DITOLAK   = "Invest ditolak dengan IllegalArgumentException %n";
    private static final String MSG_BORROWER_NOT_FOUND = "Borrower tidak ditemukan";

    // Pesan hasil edge case
    private static final String MSG_SEHARUSNYA_FAIL  = "[Hasil] Seharusnya gagal! [FAIL]";
    private static final String MSG_SEHARUSNYA_TOLAK = "[Hasil] Seharusnya ditolak! [FAIL]";

    //Repositories
    private static InMemoryBorrowerRepository   borrowerRepo;
    private static InMemoryLoanRepository       loanRepo;
    private static InMemoryLenderRepository     lenderRepo;
    private static InMemoryInvestmentRepository investmentRepo;
    private static InMemmoryPaymentRepository   paymentRepo;

    //Use Cases
    private static RegisterBorrowerUseCase       registerBorrowerUseCase;
    private static ApplyLoanUseCaseImpl          applyLoanUseCase;
    private static ApproveLoanUseCaseImpl        approveLoanUseCase;
    private static DisburseUseCaseImpl           disburseUseCase;
    private static CancelLoanUseCaseImpl         cancelLoanUseCase;
    private static RegisterLenderUseCaseImpl     registerLenderUseCase;
    private static InvestLoanUseCaseImpl         investLoanUseCase;
    private static MakePaymentUseCaseImpl        makePaymentUseCase;
    private static GetPaymentScheduleUseCaseImpl getPaymentScheduleUseCase;

    // =============================================================
    public static void main(String[] args) {
    Logger rootLogger = Logger.getLogger("");
    rootLogger.setLevel(java.util.logging.Level.INFO);
    for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
        handler.setFormatter(new java.util.logging.Formatter() {
            @Override
            public String format(java.util.logging.LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        printBanner();

        setUp();
        scenario1HappyPath();
        separator();

        setUp();
        scenario2PaymentOnTime();
        separator();

        setUp();
        scenario3PaymentOverdue();
        separator();

        setUp();
        edgeCase4AdminRejectLoan();
        separator();

        setUp();
        edgeCase5CancellationCounterIncremented();
        separator();

        setUp();
        edgeCase6ThreeTimesCancelBorrowerBlocked();
        separator();

        setUp();
        edgeCase7CancelIfFundedLessThan20Percent();
        separator();

        setUp();
        edgeCase8CannotCancelAfterMaxCancellations();
        separator();

        setUp();
        edgeCase9ExpiredFunding();
        separator();

        setUp();
        edgeCase10ExpiredLoanCannotBeDisbursed();
        separator();

        setUp();
        edgeCase11ExpiredLoanCannotBeReapproved();
        separator();

        setUp();
        edgeCase12InvestLessThan20PercentRejected();
        separator();

        setUp();
        edgeCase13InsufficientSaldoInvestRejected();
        separator();

        setUp();
        edgeCase14InvestToNonFundingLoanRejected();
        separator();

        setUp();
        edgeCase15BorrowerNotFoundApplyLoan();

        LOGGER.info("\nSemua skenario selesai dijalankan!\n");}
    }


    private static void setUp() {
        SharedStorage.getInstance().getBorrowers().clear();
        SharedStorage.getInstance().getLoans().clear();
        SharedStorage.getInstance().getLenders().clear();
        SharedStorage.getInstance().getInvestments().clear();
        SharedStorage.getInstance().getPayments().clear();

        borrowerRepo   = new InMemoryBorrowerRepository();
        loanRepo       = new InMemoryLoanRepository();
        lenderRepo     = new InMemoryLenderRepository();
        investmentRepo = new InMemoryInvestmentRepository();
        paymentRepo    = new InMemmoryPaymentRepository();

        LoanApprovalService     loanApprovalService     = new LoanApprovalService();
        LoanCancellationService loanCancellationService = new LoanCancellationService();
        InvestmentService       investmentService       = new InvestmentService();
        RepaymentService        repaymentService        = new RepaymentService();
        PaymentScheduleService  paymentScheduleService  = new PaymentScheduleService();

        registerBorrowerUseCase   = new RegisterBorrowerUseCase(borrowerRepo);
        applyLoanUseCase          = new ApplyLoanUseCaseImpl(borrowerRepo, loanRepo, loanApprovalService);
        approveLoanUseCase        = new ApproveLoanUseCaseImpl(loanRepo);
        disburseUseCase           = new DisburseUseCaseImpl(loanRepo);
        cancelLoanUseCase         = new CancelLoanUseCaseImpl(borrowerRepo, loanRepo, loanCancellationService, investmentRepo);
        registerLenderUseCase     = new RegisterLenderUseCaseImpl(lenderRepo);
        investLoanUseCase         = new InvestLoanUseCaseImpl(lenderRepo, loanRepo, investmentRepo, investmentService);
        makePaymentUseCase        = new MakePaymentUseCaseImpl(borrowerRepo, loanRepo, paymentRepo, repaymentService);
        getPaymentScheduleUseCase = new GetPaymentScheduleUseCaseImpl(borrowerRepo, loanRepo, paymentRepo, paymentScheduleService);
    }


    // SKENARIO 1 — HAPPY PATH

    private static void scenario1HappyPath() {
        LOGGER.info("[SKENARIO 1] HAPPY PATH - Loan berhasil dicairkan (DISBURSED)");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 750));
        LOGGER.info(String.format("[Borrower] Terdaftar: %s | id: %s%n", iman.getNama(), iman.getId()));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        LOGGER.info(String.format("Loan diajukan: %s | Rp 30.000.000 | 6 bulan%n", loan.getId()));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("50000000")));
        LOGGER.info(String.format("Lender Terdaftar: %s | saldo: Rp 50.000.000%n", budi.getNama()));

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("30000000")));
        LOGGER.info("Invest BUDI invest Rp 30.000.000 (100%)");

        BigDecimal saldoBudiSetelah = lenderRepo.findById(budi.getId())
                .orElseThrow(() -> new IllegalStateException("Lender tidak ditemukan"))
                .getSaldo().getAmount();
        LOGGER.info(String.format("Saldo BUDI sisa: Rp %,.0f%n", saldoBudiSetelah));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        LOGGER.info("\nHasil Skenario 1");
        LOGGER.info(String.format("Loan status : %s%n", getLoanStatus(loan.getId())));
        LOGGER.info(String.format("Saldo BUDI  : Rp %,.0f (berkurang 30jt)%n", saldoBudiSetelah));
    }

    // SKENARIO 2 — PAYMENT ON TIME

    private static void scenario2PaymentOnTime() {
        LOGGER.info("[SKENARIO 2] PAYMENT ON TIME - Borrower bayar cicilan tepat waktu");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 750));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 12_000_000L, 12));
        LOGGER.info(String.format("Loan Diajukan: %s | Rp 12.000.000 | 12 bulan%n", loan.getId()));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("50000000")));

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("12000000")));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        List<PaymentDTO> schedule = getPaymentScheduleUseCase.execute(iman.getId(), loan.getId());
        LOGGER.info(String.format("Schedule %d cicilan digenerate%n", schedule.size()));

        PaymentDTO firstPaymentDTO = schedule.get(0);
        LOGGER.info(String.format("Payment Cicilan bulan 1 : Rp %,.0f | Due: %s | Status: %s%n",
                firstPaymentDTO.getAmount(), firstPaymentDTO.getDueDate(), firstPaymentDTO.getStatus()));

        MakePaymentCommand paymentCommand = new MakePaymentCommand(
                iman.getId(),
                firstPaymentDTO.getId(),
                new Money(firstPaymentDTO.getAmount(), "IDR")
        );
        PaymentDTO result = makePaymentUseCase.execute(paymentCommand);

        LOGGER.info("\nHasil Skenario 2");
        LOGGER.info(String.format("Payment status : %s %n", result.getStatus()));
        LOGGER.info(String.format("Paid date      : %s %n", result.getPaidDate()));
        LOGGER.info(String.format("Denda          : Rp %,.0f (tidak ada denda) %n", result.getDenda()));
    }

    // SKENARIO 3 — PAYMENT OVERDUE + DENDA

    private static void scenario3PaymentOverdue() {
        LOGGER.info("[SKENARIO 3] PAYMENT OVERDUE - Telat 40 hari, denda 1% pokok");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 750));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 12_000_000L, 12));
        LOGGER.info(String.format("Loan diajukan: %s | Rp 12.000.000 | 12 bulan%n", loan.getId()));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("50000000")));

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("12000000")));

        disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        List<PaymentDTO> schedule = getPaymentScheduleUseCase.execute(iman.getId(), loan.getId());
        LOGGER.info(String.format("Schedule %d cicilan digenerate%n", schedule.size()));

        String firstPaymentId = schedule.get(0).getId();
        Payment firstPayment = paymentRepo.findById(firstPaymentId)
                .orElseThrow(() -> new IllegalStateException("Payment tidak ditemukan"));

        // Simulasi: ganti dueDate jadi 40 hari lalu
        Payment overduePayment = new Payment(
                firstPayment.getId(),
                firstPayment.getLoanId(),
                firstPayment.getNoBulan(),
                firstPayment.getAmount(),
                LocalDate.now().minusDays(40)
        );
        paymentRepo.save(overduePayment);
        LOGGER.info("Simulasi: Due date diubah ke 40 hari lalu");

        BigDecimal pokok         = overduePayment.getAmount().getAmount();
        BigDecimal expectedDenda = pokok.multiply(new BigDecimal("0.01"));
        BigDecimal totalBayar    = pokok.add(expectedDenda);

        LOGGER.info(String.format("Denda 1%% x Rp %,.0f = Rp %,.2f%n", pokok, expectedDenda));
        LOGGER.info(String.format("Total Rp %,.0f + Rp %,.2f = Rp %,.2f%n", pokok, expectedDenda, totalBayar));

        MakePaymentCommand paymentCommand = new MakePaymentCommand(
                iman.getId(),
                firstPaymentId,
                new Money(totalBayar, "IDR")
        );
        PaymentDTO result = makePaymentUseCase.execute(paymentCommand);

        LOGGER.info("\nHasil Skenario 3");
        LOGGER.info(String.format("Payment status : %s %n", result.getStatus()));
        LOGGER.info(String.format("Paid date      : %s %n", result.getPaidDate()));
        LOGGER.info(String.format("Denda > 0      : %s %n", result.getDenda().compareTo(BigDecimal.ZERO) > 0));
        LOGGER.info(String.format("Denda applied  : Rp %,.2f%n", result.getDenda()));
    }

    // EDGE CASE 4 — Admin reject loan -> CANCELLED

    private static void edgeCase4AdminRejectLoan() {
        LOGGER.info("[EDGE CASE 4] Admin reject loan -> status CANCELLED");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 700));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 20_000_000L, 6));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), false));

        LoanStatus status = getLoanStatus(loan.getId());
        LOGGER.info(String.format(FMT_STATUS, status));
        LOGGER.info(String.format("Hasil Loan status CANCELLED : %s %n", status == LoanStatus.CANCELLED));
    }


    // EDGE CASE 5 — Borrower cancel -> counter +1

    private static void edgeCase5CancellationCounterIncremented() {
        LOGGER.info("[EDGE CASE 5] Borrower cancel loan -> counter +1");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("20000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 3));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        investLoanUseCase.execute(
                new InvestCommand(budi.getId(), loan.getId(), new BigDecimal(NOMINAL_6JT)));

        int countBefore = getCancellationCount(iman.getId());

        cancelLoanUseCase.execute(new CancelLoanCommand(
                iman.getId(), loan.getId(), new Money(new BigDecimal(NOMINAL_6JT), "IDR")));

        LoanStatus loanStatus     = getLoanStatus(loan.getId());
        LoanApplication cancelled = loanRepo.findById(loan.getId())
                .orElseThrow(() -> new IllegalStateException("Loan tidak ditemukan"));
        int countAfter            = getCancellationCount(iman.getId());
        Borrower borrower         = borrowerRepo.findById(iman.getId())
                .orElseThrow(() -> new IllegalStateException(MSG_BORROWER_NOT_FOUND));

        LOGGER.info(String.format(FMT_STATUS, loanStatus));
        LOGGER.info(FMT_HASIL);
        LOGGER.info(String.format("Loan status CANCELLED      : %s %n", loanStatus == LoanStatus.CANCELLED));
        LOGGER.info(String.format("cancelledDate tidak null   : %s %n", cancelled.getCancelledDate() != null));
        LOGGER.info(String.format("Counter %d -> %d (+1)       : %s %n", countBefore, countAfter, countAfter == countBefore + 1));
        LOGGER.info(String.format("Belum diblokir             : %s %n", borrower.getLastBlockedDate() == null));
    }

    // EDGE CASE 6 — 3x cancel -> borrower diblokir 4 bulan

    private static void edgeCase6ThreeTimesCancelBorrowerBlocked() {
        LOGGER.info("[EDGE CASE 6] 3x cancel -> borrower diblokir 4 bulan");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 750));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("100000000")));

        for (int i = 1; i <= 3; i++) {
            LoanDTO loan = applyLoanUseCase.execute(
                    new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
            approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), loan.getId(), new BigDecimal(NOMINAL_6JT)));
            cancelLoanUseCase.execute(new CancelLoanCommand(
                    iman.getId(), loan.getId(), new Money(new BigDecimal(NOMINAL_6JT), "IDR")));
            LOGGER.info(String.format("Cancel ke-%d : count = %d%n", i, getCancellationCount(iman.getId())));
        }

        Borrower borrowerAfter = borrowerRepo.findById(iman.getId())
                .orElseThrow(() -> new IllegalStateException(MSG_BORROWER_NOT_FOUND));
        LOGGER.info(FMT_HASIL);
        LOGGER.info(String.format("Cancellation count = 3     : %s %n", borrowerAfter.getCancellationCount() == 3));
        LOGGER.info(String.format("lastBlockedDate tidak null : %s %n", borrowerAfter.getLastBlockedDate() != null));
    }

    // EDGE CASE 7 — Cancel jika funded < 20% -> tanpa penalty

    private static void edgeCase7CancelIfFundedLessThan20Percent() {
        LOGGER.info("[EDGE CASE 7] Cancel jika funded < 20% -> tanpa penalty");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 700));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        cancelLoanUseCase.execute(new CancelLoanCommand(
                iman.getId(), loan.getId(), new Money(new BigDecimal(NOMINAL_5JT), "IDR")));

        LoanStatus status = getLoanStatus(loan.getId());
        LOGGER.info(String.format(FMT_STATUS, status));
        LOGGER.info(String.format("Hasil Loan status CANCELLED : %s %n", status == LoanStatus.CANCELLED));
    }

    // EDGE CASE 8 — Tidak bisa cancel setelah 3x (blocked)

    private static void edgeCase8CannotCancelAfterMaxCancellations() {
        LOGGER.info("[EDGE CASE 8] Tidak bisa cancel setelah 3x cancel (blokir)");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 750));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("200000000")));

        for (int i = 0; i < 3; i++) {
            LoanDTO l = applyLoanUseCase.execute(
                    new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
            approveLoanUseCase.execute(new ApproveLoanCommand(l.getId(), true));
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), l.getId(), new BigDecimal(NOMINAL_6JT)));
            cancelLoanUseCase.execute(new CancelLoanCommand(
                    iman.getId(), l.getId(), new Money(new BigDecimal(NOMINAL_6JT), "IDR")));
        }

        Borrower blockedBorrower = borrowerRepo.findById(iman.getId())
                .orElseThrow(() -> new IllegalStateException(MSG_BORROWER_NOT_FOUND));
        LOGGER.info(FMT_HASIL);
        LOGGER.info(String.format("Cancellation count = 3     : %s %n", blockedBorrower.getCancellationCount() == 3));
        LOGGER.info(String.format("lastBlockedDate tidak null : %s %n", blockedBorrower.getLastBlockedDate() != null));
    }

    // EDGE CASE 9 — Loan tidak terfund -> EXPIRED_FUNDING

    private static void edgeCase9ExpiredFunding() {
        LOGGER.info("[EDGE CASE 9] Loan tidak terfund -> EXPIRED_FUNDING");
        LOGGER.info("-".repeat(55));

        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_KEMAL, TELEPON_KEMAL, KTP_KEMAL,
                ALAMAT_KEMAL, 5_000_000L, 650));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));

        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        expireLoanFunding(loan.getId());
        LOGGER.info(String.format(FMT_STATUS, getLoanStatus(loan.getId())));

        LOGGER.info(String.format("Hasi Loan status EXPIRED_FUNDING : %s %n",
                getLoanStatus(loan.getId()) == LoanStatus.EXPIRED_FUNDING));
    }

    // EDGE CASE 10 — Loan EXPIRED tidak bisa di-disburse

    private static void edgeCase10ExpiredLoanCannotBeDisbursed() {
        LOGGER.info("[EDGE CASE 10] Loan EXPIRED tidak bisa di-disburse");
        LOGGER.info("-".repeat(55));

        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_KEMAL, TELEPON_KEMAL, KTP_KEMAL,
                ALAMAT_KEMAL, 5_000_000L, 650));
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        expireLoanFunding(loan.getId());

        try {
            disburseUseCase.execute(new DisburseLoanCommand(loan.getId()));
            LOGGER.info(MSG_SEHARUSNYA_FAIL);
        } catch (IllegalStateException e) {
            LOGGER.info(String.format("Hasil Disburse ditolak dengan IllegalStateException %n"));
            LOGGER.info(String.format(FMT_PESAN, e.getMessage()));
        }
    }

    // EDGE CASE 11 — Loan EXPIRED tidak bisa di-approve ulang

    private static void edgeCase11ExpiredLoanCannotBeReapproved() {
        LOGGER.info("[EDGE CASE 11] Loan EXPIRED tidak bisa di-approve ulang");
        LOGGER.info("-".repeat(55));

        BorrowerDTO kemal = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_KEMAL, TELEPON_KEMAL, KTP_KEMAL,
                ALAMAT_KEMAL, 5_000_000L, 650));
        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(kemal.getId(), 15_000_000L, 12));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
        expireLoanFunding(loan.getId());

        try {
            approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));
            LOGGER.info(MSG_SEHARUSNYA_FAIL);
        } catch (IllegalStateException e) {
            LOGGER.info(String.format("Hasil Approve ditolak dengan IllegalStateException %n"));
            LOGGER.info(String.format(FMT_PESAN, e.getMessage()));
        }
    }

    // EDGE CASE 12 — Invest < 20% -> ditolak

    private static void edgeCase12InvestLessThan20PercentRejected() {
        LOGGER.info("[EDGE CASE 12] Lender invest < 20% dari loan -> ditolak");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("20000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        try {
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), loan.getId(), new BigDecimal(NOMINAL_5JT)));
            LOGGER.info(MSG_SEHARUSNYA_TOLAK);
        } catch (IllegalArgumentException e) {
            LOGGER.info(String.format(INVEST_DITOLAK));
            LOGGER.info(String.format(FMT_PESAN, e.getMessage()));
        }
    }

    // EDGE CASE 13 — Saldo lender tidak cukup -> invest ditolak

    private static void edgeCase13InsufficientSaldoInvestRejected() {
        LOGGER.info("[EDGE CASE 13] Lender saldo tidak cukup -> invest ditolak");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal(NOMINAL_5JT)));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        approveLoanUseCase.execute(new ApproveLoanCommand(loan.getId(), true));

        try {
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), loan.getId(), new BigDecimal(NOMINAL_6JT)));
            LOGGER.info(MSG_SEHARUSNYA_TOLAK);
        } catch (IllegalArgumentException e) {
            LOGGER.info(String.format(INVEST_DITOLAK));
            LOGGER.info(String.format(FMT_PESAN, e.getMessage()));
        }
    }

    // EDGE CASE 14 — Invest ke loan bukan status FUNDING -> ditolak

    private static void edgeCase14InvestToNonFundingLoanRejected() {
        LOGGER.info("[EDGE CASE 14] Invest ke loan bukan status FUNDING -> ditolak");
        LOGGER.info("-".repeat(55));

        BorrowerDTO iman = registerBorrowerUseCase.execute(new RegisterBorrowerCommand(
                NAMA_IMAN, TELEPON_IMAN, KTP_IMAN,
                ALAMAT_IMAN, 10_000_000L, 700));
        LenderDTO budi = registerLenderUseCase.execute(new RegisterLenderCommand(
                NAMA_BUDI, TELEPON_BUDI, KTP_BUDI,
                ALAMAT_BUDI, PEKERJAAN_BUDI, new BigDecimal("50000000")));

        LoanDTO loan = applyLoanUseCase.execute(
                new ApplyLoanCommand(iman.getId(), 30_000_000L, 6));
        // Sengaja tidak di-approve -> masih PENDING

        try {
            investLoanUseCase.execute(
                    new InvestCommand(budi.getId(), loan.getId(), new BigDecimal("10000000")));
            LOGGER.info(MSG_SEHARUSNYA_TOLAK);
        } catch (IllegalArgumentException e) {
            LOGGER.info(String.format(INVEST_DITOLAK));
            LOGGER.info(String.format(FMT_PESAN, e.getMessage()));
        }
    }

    // EDGE CASE 15 — Borrower tidak ditemukan saat apply -> exception

    private static void edgeCase15BorrowerNotFoundApplyLoan() {
        LOGGER.info("[EDGE CASE 15] Borrower tidak ditemukan saat apply -> exception");
        LOGGER.info("-".repeat(55));

        try {
            applyLoanUseCase.execute(
                    new ApplyLoanCommand("BORROWER-TIDAK-ADA", 10_000_000L, 6));
            LOGGER.info(MSG_SEHARUSNYA_FAIL);
        } catch (IllegalArgumentException e) {
            LOGGER.info(String.format("Apply ditolak dengan IllegalArgumentException %n"));
            LOGGER.info(String.format(FMT_PESAN, e.getMessage()));
        }
    }


    // HELPERS

    private static void expireLoanFunding(String loanId) {
        LoanApplication loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));
        LoanAggregate aggregate = LoanAggregate.load(loan, null);
        aggregate.expireFunding();
        loanRepo.save(loan);
    }

    private static LoanStatus getLoanStatus(String loanId) {
        return loanRepo.findById(loanId)
                .map(LoanApplication::getStatus)
                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));
    }

    private static int getCancellationCount(String borrowerId) {
        return borrowerRepo.findById(borrowerId)
                .map(Borrower::getCancellationCount)
                .orElse(-1);
    }
    

    private static void printBanner() {
        LOGGER.info("P2P LENDING PLATFORM  -  DEMO CLI");
        LOGGER.info("");
    }

    private static void separator() {
        LOGGER.info("\n" + "=".repeat(57) + "\n");
    }
}