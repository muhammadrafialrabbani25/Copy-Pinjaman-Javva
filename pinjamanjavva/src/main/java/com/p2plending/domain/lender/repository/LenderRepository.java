package com.p2plending.domain.lender.repository;

import com.p2plending.domain.lender.entity.Lender;

public interface LenderRepository {
    Lender save(Lender lender);
    java.util.Optional<Lender> findById(String id);
}