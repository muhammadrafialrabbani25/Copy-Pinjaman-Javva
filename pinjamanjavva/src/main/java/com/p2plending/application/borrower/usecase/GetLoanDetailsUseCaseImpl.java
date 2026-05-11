package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;

public class GetLoanDetailsUseCaseImpl implements GetLoanDetailsUseCase {

    private final LoanRepository loanRepository;

    public GetLoanDetailsUseCaseImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public LoanDTO execute(String loanId) {
        LoanApplication loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + loanId));

        return new LoanDTO(
            loan.getId(),
            loan.getBorrowerId(),
            loan.getAmount().getAmount().longValue(),
            loan.getTenor().getMonths()
        );
    }
}