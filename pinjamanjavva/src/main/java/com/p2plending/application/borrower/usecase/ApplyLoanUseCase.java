package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
public interface ApplyLoanUseCase {
    LoanDTO execute(ApplyLoanCommand command);
}