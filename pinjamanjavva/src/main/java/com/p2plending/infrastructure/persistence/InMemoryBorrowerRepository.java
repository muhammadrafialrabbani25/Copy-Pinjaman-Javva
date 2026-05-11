package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryBorrowerRepository implements BorrowerRepository {

    private final Map<String, Borrower> store = SharedStorage.getInstance().getBorrowers();

    @Override
    public Borrower save(Borrower borrower) {
        String id = borrower.getId() != null ? borrower.getId() : UUID.randomUUID().toString();
        Borrower toSave = borrower.getId() != null ? borrower
                : new Borrower(id, borrower.getNama(), borrower.getNoTelepon(),
                        borrower.getAlamat(), borrower.getKtp(), borrower.getGaji(),
                        borrower.getPekerjaan(), borrower.getCreditScore());
        store.put(id, toSave);
        return toSave;
    }

    @Override
    public Optional<Borrower> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}