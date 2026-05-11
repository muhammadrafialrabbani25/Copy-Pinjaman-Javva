package com.p2plending.application.shared;

import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.shared.LoanStatus;

import java.lang.reflect.Field;
import java.util.Optional;

public class DisburseUseCaseImpl implements DisburseUseCase {

    private final LoanRepository loanRepository;

    public DisburseUseCaseImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public void execute(DisburseLoanCommand command) {
        Optional<LoanApplication> loanOpt = loanRepository.findById(command.getLoanId());
        if (!loanOpt.isPresent()) {
            throw new IllegalArgumentException("Loan tidak ditemukan: " + command.getLoanId());
        }

        LoanApplication loan = loanOpt.get();

        if (loan.getStatus() != LoanStatus.FUNDED) {
            throw new IllegalStateException("Hanya loan dengan status FUNDED yang dapat di-disburse");
        }

        setLoanStatus(loan, LoanStatus.DISBURSED);
        
        loanRepository.save(loan);
    }

    private void setLoanStatus(LoanApplication loan, LoanStatus status) {
        try {
            Field field = LoanApplication.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(loan, status);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set status loan", e);
        }
    }
}
