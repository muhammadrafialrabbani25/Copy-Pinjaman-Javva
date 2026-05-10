package com.p2plending.application.lender.service;

import com.p2plending.application.lender.dto.AvailableLoanDTO;
import com.p2plending.application.lender.dto.InvestCommand;
import com.p2plending.application.lender.dto.LenderDTO;
import com.p2plending.application.lender.dto.RegisterLenderCommand;
import com.p2plending.application.lender.usecase.GetAvailableLoansUseCase;
import com.p2plending.application.lender.usecase.InvestLoanUseCase;
import com.p2plending.application.lender.usecase.RegisterLenderUseCase;
import com.p2plending.application.lender.usecase.TopUpSaldoUseCase;

import java.math.BigDecimal;
import java.util.List;

/**
 * Facade untuk orchestrasi lender use cases
 */
public class LenderApplicationService {

    private final RegisterLenderUseCase registerLenderUseCase;
    private final TopUpSaldoUseCase topUpSaldoUseCase;
    private final InvestLoanUseCase investLoanUseCase;
    private final GetAvailableLoansUseCase getAvailableLoansUseCase;

    public LenderApplicationService(RegisterLenderUseCase registerLenderUseCase,
                                    TopUpSaldoUseCase topUpSaldoUseCase,
                                    InvestLoanUseCase investLoanUseCase,
                                    GetAvailableLoansUseCase getAvailableLoansUseCase) {
        this.registerLenderUseCase = registerLenderUseCase;
        this.topUpSaldoUseCase = topUpSaldoUseCase;
        this.investLoanUseCase = investLoanUseCase;
        this.getAvailableLoansUseCase = getAvailableLoansUseCase;
    }

    public LenderDTO registerLender(RegisterLenderCommand command) {
        return registerLenderUseCase.execute(command);
    }

    public LenderDTO topUpSaldo(String lenderId, BigDecimal amount) {
        return topUpSaldoUseCase.execute(lenderId, amount);
    }

    public void investLoan(InvestCommand command) {
        investLoanUseCase.execute(command);
    }

    public List<AvailableLoanDTO> getAvailableLoans() {
        return getAvailableLoansUseCase.execute();
    }
}