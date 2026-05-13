package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBorrowerRepositoryTest {

    private InMemoryBorrowerRepository repository;

    @BeforeEach
    void setUp() {
        SharedStorage.getInstance().getBorrowers().clear();
        repository = new InMemoryBorrowerRepository();
    }

    private Borrower buatBorrower(String id) {
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(BigDecimal.valueOf(5_000_000), "IDR");
        return new Borrower(id, "Budi", "08123456789", "Bandung", ktp, gaji, "Pegawai", 700);
    }

    @Test
    @DisplayName("save() dengan ID null harus auto-generate ID")
    void testSaveAutoGenerateId() {
        Borrower borrower = buatBorrower(null);
        Borrower saved = repository.save(borrower);
        assertNotNull(saved.getId());
    }

    @Test
    @DisplayName("save() dengan ID yang ada harus pakai ID tersebut")
    void testSaveWithExistingId() {
        Borrower borrower = buatBorrower("BORROWER-001");
        Borrower saved = repository.save(borrower);
        assertEquals("BORROWER-001", saved.getId());
    }

    @Test
    @DisplayName("findById() harus return borrower yang sudah disimpan")
    void testFindById() {
        Borrower borrower = buatBorrower("BORROWER-001");
        repository.save(borrower);
        Optional<Borrower> result = repository.findById("BORROWER-001");
        assertTrue(result.isPresent());
        assertEquals("Budi", result.get().getNama());
    }

    @Test
    @DisplayName("findById() dengan ID tidak ada harus return empty")
    void testFindByIdNotFound() {
        Optional<Borrower> result = repository.findById("TIDAK-ADA");
        assertFalse(result.isPresent());
    }
}
