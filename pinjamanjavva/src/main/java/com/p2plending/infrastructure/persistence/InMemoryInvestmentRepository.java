package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.repository.InvestmentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryInvestmentRepository implements InvestmentRepository {

    private final Map<String, Investment> store = SharedStorage.getInstance().getInvestments();

    @Override
    public void save(Investment investment) {
        String id = investment.getId() != null ? investment.getId() : UUID.randomUUID().toString();
        store.put(id, investment);
    }

    @Override
    public Optional<Investment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Investment> findByLoanId(String loanId) {
        return store.values().stream()
                .filter(inv -> inv.getLoanId().equals(loanId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Investment> findByLenderId(String lenderId) {
        return store.values().stream()
                .filter(inv -> inv.getLenderId().equals(lenderId))
                .collect(Collectors.toList());
    }
}