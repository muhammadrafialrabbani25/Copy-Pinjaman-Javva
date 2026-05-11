package com.p2plending.application.borrower.service;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.BorrowerDTO;
import com.p2plending.application.borrower.dto.CancelLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand;
import com.p2plending.application.borrower.usecase.ApplyLoanUseCase;
import com.p2plending.application.borrower.usecase.CancelLoanUseCase;
import com.p2plending.application.borrower.usecase.GetLoanDetailsUseCase;
import com.p2plending.application.borrower.usecase.GetLoanListUseCase;
import com.p2plending.application.borrower.usecase.RegisterBorrowerUseCase;

import java.util.List;

/**
 * Facade untuk orchestrasi borrower use cases
 */
public class BorrowerApplicationService {

    private final RegisterBorrowerUseCase registerBorrowerUseCase;
    private final ApplyLoanUseCase applyLoanUseCase;
    private final CancelLoanUseCase cancelLoanUseCase;
    private final GetLoanDetailsUseCase getLoanDetailsUseCase;
    private final GetLoanListUseCase getLoanListUseCase;

    public BorrowerApplicationService(RegisterBorrowerUseCase registerBorrowerUseCase,
                                      ApplyLoanUseCase applyLoanUseCase,
                                      CancelLoanUseCase cancelLoanUseCase,
                                      GetLoanDetailsUseCase getLoanDetailsUseCase,
                                      GetLoanListUseCase getLoanListUseCase) {
        this.registerBorrowerUseCase = registerBorrowerUseCase;
        this.applyLoanUseCase = applyLoanUseCase;
        this.cancelLoanUseCase = cancelLoanUseCase;
        this.getLoanDetailsUseCase = getLoanDetailsUseCase;
        this.getLoanListUseCase = getLoanListUseCase;
    }

    public BorrowerDTO registerBorrower(RegisterBorrowerCommand command) {
        return registerBorrowerUseCase.execute(command);
    }

    public LoanDTO applyLoan(ApplyLoanCommand command) {
        return applyLoanUseCase.execute(command);
    }

    public void cancelLoan(CancelLoanCommand command) {
        cancelLoanUseCase.execute(command);
    }

    public LoanDTO getLoanDetails(String loanId) {
        return getLoanDetailsUseCase.execute(loanId);
    }

    public List<LoanDTO> getLoanList(String borrowerId) {
        return getLoanListUseCase.execute(borrowerId);
    }
}