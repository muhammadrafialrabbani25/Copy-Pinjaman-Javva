package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.BorrowerDTO;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.entity.Borrower;


public class RegisterBorrowerUseCase {
    private final BorrowerRepository borrowerRepository;

    public RegisterBorrowerUseCase(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowerDTO execute(RegisterBorrowerCommand command) {
        Borrower borrower = new Borrower(
            null,
            command.getName(),
            command.getKtpNumber(),
            command.getMonthlySalary(),
            command.getCreditScore()
        );
        Borrower savedBorrower = borrowerRepository.save(borrower);
        return new BorrowerDTO(
            savedBorrower.getId(),
            savedBorrower.getName(),
            savedBorrower.getKtpNumber(),
            savedBorrower.getMonthlySalary(),
            savedBorrower.getCreditScore()
        );
    }
}