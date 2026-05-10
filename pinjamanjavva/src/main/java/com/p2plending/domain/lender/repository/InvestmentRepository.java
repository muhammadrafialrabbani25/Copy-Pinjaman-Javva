package com.p2plending.domain.lender.repository;

import com.p2plending.domain.lender.entity.Investment;

import java.util.List;
import java.util.Optional;

public interface InvestmentRepository {
    void save(Investment investment);
    Optional<Investment> findById(String id);
    List<Investment> findByLoanId(String loanId);
    List<Investment> findByLenderId(String lenderId);
}