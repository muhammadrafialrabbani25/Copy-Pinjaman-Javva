package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.InvestCommand;

public interface InvestLoanUseCase {
    void execute(InvestCommand command);
}