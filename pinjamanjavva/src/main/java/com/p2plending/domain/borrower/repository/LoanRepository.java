package com.p2plending.domain.borrower.repository;

import com.p2plending.domain.borrower.entity.LoanApplication;

public interface LoanRepository {
    void  save(LoanApplication loanApplication);
        java.util.Optional<LoanApplication> findById(String id);
}
