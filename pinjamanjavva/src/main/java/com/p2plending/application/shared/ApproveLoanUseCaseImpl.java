package com.p2plending.application.shared;

import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.shared.LoanStatus;

import java.util.Optional;

public class ApproveLoanUseCaseImpl implements ApproveLoanUseCase {

    private final LoanRepository loanRepository;

    public ApproveLoanUseCaseImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public void execute(ApproveLoanCommand command) {
        Optional<LoanApplication> loanOpt = loanRepository.findById(command.getLoanId());
        if (!loanOpt.isPresent()) {
            throw new IllegalArgumentException("Loan tidak ditemukan: " + command.getLoanId());
        }

        LoanApplication loan = loanOpt.get();

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Hanya loan dengan status PENDING yang dapat diproses");
        }

        com.p2plending.domain.borrower.aggregate.LoanAggregate aggregate = 
            com.p2plending.domain.borrower.aggregate.LoanAggregate.load(loan, null);

        if (command.isApprove()) {
            // State transition: PENDING -> VERIFIED -> FUNDING
            aggregate.verify();
            aggregate.openFunding();
        } else {
            // Rejected
            aggregate.cancel();
        }

        loanRepository.save(loan);
    }
}
