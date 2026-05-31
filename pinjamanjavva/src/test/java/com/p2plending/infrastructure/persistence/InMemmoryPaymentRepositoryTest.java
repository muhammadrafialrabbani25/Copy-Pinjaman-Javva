package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemmoryPaymentRepositoryTest {

    private InMemmoryPaymentRepository repository;

    @BeforeEach
    void setUp() {
        SharedStorage.getInstance().getPayments().clear();
        repository = new InMemmoryPaymentRepository();
    }

    private Payment buatPayment(String id, String loanId, int noBulan) {
        Money amount = new Money(BigDecimal.valueOf(1_000_000), "IDR");
        LocalDate dueDate = LocalDate.now().plusDays(30);
        return new Payment(id, loanId, noBulan, amount, dueDate);
    }

    // ─── CRUD Basic ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("save() lalu findById() harus return payment yang sama")
    void save_thenFindById_shouldReturnSamePayment() {
        Payment payment = buatPayment("PAY-001", "LOAN-001", 1);
        repository.save(payment);

        Optional<Payment> result = repository.findById("PAY-001");

        assertTrue(result.isPresent());
        assertEquals("PAY-001", result.get().getId());
    }

    @Test
    @DisplayName("findById() dengan ID tidak ada harus return empty")
    void findById_withNonExistentId_shouldReturnEmpty() {
        Optional<Payment> result = repository.findById("TIDAK-ADA");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("save() dua kali dengan ID sama harus overwrite data lama")
    void save_withSameId_shouldOverwriteExistingData() {
        Payment paymentPertama = buatPayment("PAY-001", "LOAN-001", 1);
        Payment paymentKedua = buatPayment("PAY-001", "LOAN-002", 2);

        repository.save(paymentPertama);
        repository.save(paymentKedua);

        Optional<Payment> result = repository.findById("PAY-001");
        assertTrue(result.isPresent());
        assertEquals("LOAN-002", result.get().getLoanId());
    }

    // ─── findByLoanId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByLoanId() harus return semua payment milik loan tersebut")
    void findByLoanId_shouldReturnAllPaymentsForThatLoan() {
        repository.save(buatPayment("PAY-001", "LOAN-001", 1));
        repository.save(buatPayment("PAY-002", "LOAN-001", 2));
        repository.save(buatPayment("PAY-003", "LOAN-002", 1));

        List<Payment> result = repository.findByLoanId("LOAN-001");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getLoanId().equals("LOAN-001")));
    }

    @Test
    @DisplayName("findByLoanId() dengan loan tidak ada harus return list kosong")
    void findByLoanId_withNonExistentLoanId_shouldReturnEmptyList() {
        List<Payment> result = repository.findByLoanId("TIDAK-ADA");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ─── updateStatus ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateStatus() ke PAID harus mengubah status payment menjadi PAID")
    void updateStatus_toPaid_shouldChangeStatusToPaid() {
        Payment payment = buatPayment("PAY-001", "LOAN-001", 1);
        repository.save(payment);

        repository.updateStatus("PAY-001", PaymentStatus.PAID);

        Optional<Payment> result = repository.findById("PAY-001");
        assertTrue(result.isPresent());
        assertEquals(PaymentStatus.PAID, result.get().getStatus());
    }

    @Test
    @DisplayName("updateStatus() ke OVERDUE harus mengubah status payment menjadi OVERDUE")
    void updateStatus_toOverdue_shouldChangeStatusToOverdue() {
        Money amount = new Money(BigDecimal.valueOf(1_000_000), "IDR");
        LocalDate dueDateLewat = LocalDate.now().minusDays(40); // sudah lewat 40 hari
        Payment payment = new Payment("PAY-001", "LOAN-001", 1, amount, dueDateLewat);
        repository.save(payment);

        repository.updateStatus("PAY-001", PaymentStatus.OVERDUE);

        Optional<Payment> result = repository.findById("PAY-001");
        assertTrue(result.isPresent());
        assertEquals(PaymentStatus.OVERDUE, result.get().getStatus());
    }

    @Test
    @DisplayName("updateStatus() dengan ID tidak ada harus throw IllegalArgumentException")
    void updateStatus_withNonExistentId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.updateStatus("TIDAK-ADA", PaymentStatus.PAID));
    }

    // ─── Integration: save & retrieve ─────────────────────────────────────────

    @Test
    @DisplayName("save multiple payments lalu findByLoanId harus return semua payment dengan urutan benar")
    void saveMultiplePayments_thenFindByLoanId_shouldReturnAllPayments() {
        String loanId = "LOAN-001";
        repository.save(buatPayment("PAY-001", loanId, 1));
        repository.save(buatPayment("PAY-002", loanId, 2));
        repository.save(buatPayment("PAY-003", loanId, 3));

        List<Payment> result = repository.findByLoanId(loanId);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("status default payment setelah save harus PENDING")
    void save_newPayment_shouldHaveDefaultStatusPending() {
        Payment payment = buatPayment("PAY-001", "LOAN-001", 1);
        repository.save(payment);

        Optional<Payment> result = repository.findById("PAY-001");

        assertTrue(result.isPresent());
        assertEquals(PaymentStatus.PENDING, result.get().getStatus());
    }
}
