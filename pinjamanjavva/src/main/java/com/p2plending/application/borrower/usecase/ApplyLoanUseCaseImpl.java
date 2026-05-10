package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.service.LoanApprovalService;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class ApplyLoanUseCaseImpl implements ApplyLoanUseCase {
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;
    private final LoanApprovalService loanApprovalService;

    public ApplyLoanUseCaseImpl(BorrowerRepository borrowerRepository, LoanRepository loanRepository, LoanApprovalService loanApprovalService) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
        this.loanApprovalService = loanApprovalService;
    }

    @Override
    public LoanDTO execute(ApplyLoanCommand command) {
        Optional<Borrower> borrowerOpt = borrowerRepository.findById(command.getBorrowerId());

        if (!borrowerOpt.isPresent()) {
            throw new IllegalArgumentException("Borrower tidak ditemukan");
        }
        Borrower borrower = borrowerOpt.get();

        if (!loanApprovalService.verifyKTP(borrower.getKtp())) {
            throw new IllegalArgumentException("KTP tidak valid");
        }

        if (!loanApprovalService.verifyCreditScore(borrower.getCreditScore())) {
            throw new IllegalArgumentException("Credit score tidak memenuhi syarat");
        }

        if (borrower.getLastBlockedDate() != null) {
            LocalDate blokirSampai = borrower.getLastBlockedDate().plusMonths(4).toLocalDate();
            if (LocalDate.now().isBefore(blokirSampai)) {
                throw new IllegalArgumentException("Borrower sedang dalam masa blokir");
            }
        }

        Money loanAmount = new Money(BigDecimal.valueOf(command.getAmount()), "IDR");
        Money loanLimit = loanApprovalService.calculateLoanLimit(borrower.getGaji());

        if (loanAmount.getAmount().compareTo(loanLimit.getAmount()) > 0) {
            throw new IllegalArgumentException("Jumlah pinjaman melebihi limit: " + loanLimit.getAmount());
        }

        Tenor tenor = convertMonthsToTenor(command.getTermInMonths());

        String loanId = "LOAN-" + System.currentTimeMillis();
        LoanApplication loanApplication = new LoanApplication(loanId, command.getBorrowerId(), loanAmount, tenor, borrower.getCreditScore());
        loanRepository.save(loanApplication);

        return new LoanDTO(
            loanApplication.getId(),
            loanApplication.getBorrowerId(),
            loanApplication.getAmount().getAmount().longValue(),
            loanApplication.getTenor().getMonths()
        );
    }

    private Tenor convertMonthsToTenor(int months) {
        switch (months) {
            case 1:
                return Tenor.ONE_MONTH;
            case 3:
                return Tenor.THREE_MONTHS;
            case 6:
                return Tenor.SIX_MONTHS;
            case 12:
                return Tenor.TWELVE_MONTHS;
            default:
                throw new IllegalArgumentException("Tenor tidak valid: pilih 1,3,6,12");
        }
    }
}