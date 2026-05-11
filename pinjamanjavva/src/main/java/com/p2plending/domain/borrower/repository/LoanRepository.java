package com.p2plending.domain.borrower.repository;

import com.p2plending.domain.borrower.entity.LoanApplication;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    void save(LoanApplication loanApplication);
    Optional<LoanApplication> findById(String id);
    List<LoanApplication> findByBorrowerId(String borrowerId);
    
    // Tambahan untuk GetAvailableLoansUseCase
    List<LoanApplication> findAll();
}