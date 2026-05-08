package com.p2plending.domain.borrower.repository;

import com.p2plending.domain.borrower.entity.Borrower;

public interface BorrowerRepository {
    Borrower save(Borrower borrower);
    java.util.Optional<Borrower> findById(String id);
}