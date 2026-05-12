package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemmoryInvestmentRepositoryTest {

    private InMemoryInvestmentRepository repository;

    @BeforeEach
    void setUp() {
        SharedStorage.getInstance().getInvestments().clear();
        repository = new InMemoryInvestmentRepository();
    }

    private Investment buatInvestment(String id, String lenderId, String loanId) {
        Money amount = new Money(BigDecimal.valueOf(5_000_000), "IDR");
        return new Investment(id, lenderId, loanId, amount);
    }

    @Test
    @DisplayName("save() lalu findById() harus return investment yang sama")
    void testSaveAndFindById() {
        Investment investment = buatInvestment("INV-001", "LENDER-001", "LOAN-001");
        repository.save(investment);
        Optional<Investment> result = repository.findById("INV-001");
        assertTrue(result.isPresent());
        assertEquals("INV-001", result.get().getId());
    }

    @Test
    @DisplayName("findById() dengan ID tidak ada harus return empty")
    void testFindByIdNotFound() {
        Optional<Investment> result = repository.findById("TIDAK-ADA");
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByLoanId() harus return semua investment untuk loan tersebut")
    void testFindByLoanId() {
        repository.save(buatInvestment("INV-001", "LENDER-001", "LOAN-001"));
        repository.save(buatInvestment("INV-002", "LENDER-002", "LOAN-001"));
        repository.save(buatInvestment("INV-003", "LENDER-001", "LOAN-002"));

        List<Investment> result = repository.findByLoanId("LOAN-001");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findByLenderId() harus return semua investment milik lender")
    void testFindByLenderId() {
        repository.save(buatInvestment("INV-001", "LENDER-001", "LOAN-001"));
        repository.save(buatInvestment("INV-002", "LENDER-001", "LOAN-002"));
        repository.save(buatInvestment("INV-003", "LENDER-002", "LOAN-001"));

        List<Investment> result = repository.findByLenderId("LENDER-001");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findByLoanId() dengan loan tidak ada harus return list kosong")
    void testFindByLoanIdNotFound() {
        List<Investment> result = repository.findByLoanId("TIDAK-ADA");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByLenderId() dengan lender tidak ada harus return list kosong")
    void testFindByLenderIdNotFound() {
        List<Investment> result = repository.findByLenderId("TIDAK-ADA");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
