package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.AvailableLoanDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class GetAvailableLoansUseCaseImpl implements GetAvailableLoansUseCase {

    private final LoanRepository loanRepository;

    public GetAvailableLoansUseCaseImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<AvailableLoanDTO> execute() {
        // Get all loans yang status FUNDING (sedang mencari investasi)
        List<LoanApplication> allLoans = loanRepository.findAll();

        return allLoans.stream()
            .filter(loan -> loan.getStatus() == LoanStatus.FUNDING)
            .map(loan -> new AvailableLoanDTO(
                loan.getId(),
                loan.getBorrowerId(),
                loan.getAmount().getAmount(),
                loan.getTenor().getMonths(),
                loan.getStatus(),
                calculateMinimumInvestment(loan.getAmount())
            ))
            .collect(Collectors.toList());
    }

    private BigDecimal calculateMinimumInvestment(Money loanAmount) {
        return loanAmount.getAmount().multiply(new BigDecimal("0.20"));
    }
}