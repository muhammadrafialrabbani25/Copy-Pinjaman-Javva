package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GetLoanListUseCaseImpl implements GetLoanListUseCase {

    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;

    public GetLoanListUseCaseImpl(BorrowerRepository borrowerRepository, LoanRepository loanRepository) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public List<LoanDTO> execute(String borrowerId) {
        Borrower borrower = borrowerRepository.findById(borrowerId)
            .orElseThrow(() -> new IllegalArgumentException("Borrower tidak ditemukan: " + borrowerId));

        List<LoanApplication> loans = loanRepository.findByBorrowerId(borrowerId);

        return loans.stream()
            .map(loan -> new LoanDTO(
                loan.getId(),
                loan.getBorrowerId(),
                loan.getAmount().getAmount().longValue(),
                loan.getTenor().getMonths(),
                borrower.getCancellationCount()
            ))
            .collect(Collectors.toList());
    }
}