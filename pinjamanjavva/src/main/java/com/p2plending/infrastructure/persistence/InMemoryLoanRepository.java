package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryLoanRepository implements LoanRepository {

    private final Map<String, LoanApplication> store = SharedStorage.getInstance().getLoans();

    @Override
    public void save(LoanApplication loan) {
        String id = loan.getId() != null ? loan.getId() : UUID.randomUUID().toString();
        store.put(id, loan);
    }

    @Override
    public Optional<LoanApplication> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<LoanApplication> findByBorrowerId(String borrowerId) {
        return store.values().stream()
                .filter(loan -> loan.getBorrowerId().equals(borrowerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanApplication> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }
}