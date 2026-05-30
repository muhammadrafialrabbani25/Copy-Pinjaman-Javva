package com.p2plending.domain.borrower.repository;

import com.p2plending.domain.borrower.entity.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    
    void save(Payment payment);
    
    Optional<Payment> findById(String id);
    
    List<Payment> findByLoanId(String loanId);
}
