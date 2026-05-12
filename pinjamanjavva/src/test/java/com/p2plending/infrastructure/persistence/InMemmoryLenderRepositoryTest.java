package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryLenderRepositoryTest {

    private InMemoryLenderRepository repository;

    @BeforeEach
    void setUp() {
        SharedStorage.getInstance().getLenders().clear();
        repository = new InMemoryLenderRepository();
    }

    private Lender buatLender(String id) {
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money saldo = new Money(BigDecimal.valueOf(50_000_000), "IDR");
        return new Lender(id, "Budi", "08123456789", "Bandung", ktp, "Pegawai", saldo);
    }

    @Test
    @DisplayName("save() dengan ID null harus auto-generate ID")
    void testSaveAutoGenerateId() {
        Lender lender = buatLender(null);
        Lender saved = repository.save(lender);
        assertNotNull(saved.getId());
    }

    @Test
    @DisplayName("save() dengan ID yang ada harus pakai ID tersebut")
    void testSaveWithExistingId() {
        Lender lender = buatLender("LENDER-001");
        Lender saved = repository.save(lender);
        assertEquals("LENDER-001", saved.getId());
    }

    @Test
    @DisplayName("findById() harus return lender yang sudah disimpan")
    void testFindById() {
        Lender lender = buatLender("LENDER-001");
        repository.save(lender);
        Optional<Lender> result = repository.findById("LENDER-001");
        assertTrue(result.isPresent());
        assertEquals("Budi", result.get().getNama());
    }

    @Test
    @DisplayName("findById() dengan ID tidak ada harus return empty")
    void testFindByIdNotFound() {
        Optional<Lender> result = repository.findById("TIDAK-ADA");
        assertFalse(result.isPresent());
    }
}
