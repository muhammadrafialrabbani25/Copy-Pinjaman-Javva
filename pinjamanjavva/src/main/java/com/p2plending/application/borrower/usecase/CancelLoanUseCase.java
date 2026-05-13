package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.CancelLoanCommand;

public interface CancelLoanUseCase {

        void execute(CancelLoanCommand command);
}
