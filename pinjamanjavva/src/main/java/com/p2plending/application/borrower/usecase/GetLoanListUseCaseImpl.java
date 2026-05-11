package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GetLoanListUseCaseImpl implements GetLoanListUseCase {

    private final LoanRepository loanRepository;

    public GetLoanListUseCaseImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<LoanDTO> execute(String borrowerId) {
        List<LoanApplication> loans = loanRepository.findByBorrowerId(borrowerId);

        return loans.stream()
            .map(loan -> new LoanDTO(
                loan.getId(),
                loan.getBorrowerId(),
                loan.getAmount().getAmount().longValue(),
                loan.getTenor().getMonths()
            ))
            .collect(Collectors.toList());
    }
}