package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;

public interface GetLoanDetailsUseCase {
    LoanDTO execute(String loanId);
}