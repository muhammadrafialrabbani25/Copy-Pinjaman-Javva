package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.lender.repository.LenderRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryLenderRepository implements LenderRepository {

    private final Map<String, Lender> store = SharedStorage.getInstance().getLenders();

    @Override
    public Lender save(Lender lender) {
        String id = lender.getId() != null ? lender.getId() : UUID.randomUUID().toString();
        Lender toSave = lender.getId() != null ? lender
                : new Lender(id, lender.getNama(), lender.getNoTelepon(),
                        lender.getAlamat(), lender.getKtp(),
                        lender.getPekerjaan(), lender.getSaldo());
        store.put(id, toSave);
        return toSave;
    }

    @Override
    public Optional<Lender> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
}