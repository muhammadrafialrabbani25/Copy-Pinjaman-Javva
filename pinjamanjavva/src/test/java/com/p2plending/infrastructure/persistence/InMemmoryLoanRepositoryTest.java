package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemmoryLoanRepositoryTest {

    private InMemoryLoanRepository repository;

    @BeforeEach
    void setUp() {
        SharedStorage.getInstance().getLoans().clear();
        repository = new InMemoryLoanRepository();
    }

    private LoanApplication buatLoan(String id, String borrowerId) {
        Money amount = new Money(BigDecimal.valueOf(10_000_000), "IDR");
        return new LoanApplication(id, borrowerId, amount, Tenor.THREE_MONTHS, 700);
    }

    @Test
    @DisplayName("save() lalu findById() harus return loan yang sama")
    void testSaveAndFindById() {
        LoanApplication loan = buatLoan("LOAN-001", "BORROWER-001");
        repository.save(loan);
        Optional<LoanApplication> result = repository.findById("LOAN-001");
        assertTrue(result.isPresent());
        assertEquals("LOAN-001", result.get().getId());
    }

    @Test
    @DisplayName("findById() dengan ID tidak ada harus return empty")
    void testFindByIdNotFound() {
        Optional<LoanApplication> result = repository.findById("TIDAK-ADA");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByBorrowerId() harus return semua loan milik borrower")
    void testFindByBorrowerId() {
        repository.save(buatLoan("LOAN-001", "BORROWER-001"));
        repository.save(buatLoan("LOAN-002", "BORROWER-001"));
        repository.save(buatLoan("LOAN-003", "BORROWER-002"));

        List<LoanApplication> result = repository.findByBorrowerId("BORROWER-001");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findByBorrowerId() dengan borrower tidak ada harus return list kosong")
    void testFindByBorrowerIdNotFound() {
        List<LoanApplication> result = repository.findByBorrowerId("TIDAK-ADA");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAll() harus return semua loan yang tersimpan")
    void testFindAll() {
        repository.save(buatLoan("LOAN-001", "BORROWER-001"));
        repository.save(buatLoan("LOAN-002", "BORROWER-002"));

        List<LoanApplication> result = repository.findAll();
        assertEquals(2, result.size());
    }
}
