package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.BorrowerRepository;
import com.p2plending.domain.borrower.entity.LoanRepository;

public class ApplyLoanUseCaseImpl implements ApplyLoanUseCase {
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;

    public ApplyLoanUseCaseImpl(BorrowerRepository borrowerRepository, LoanRepository loanRepository) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public LoanDTO execute(ApplyLoanCommand command) {
        // Implementation for applying loan
        return null;
    }
}
