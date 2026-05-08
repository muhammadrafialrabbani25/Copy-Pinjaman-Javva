package com.p2plending.domain.borrower.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.shared.Money;

public class PaymentTest {
    @Test // test 1
    void shouldCreatePaymentWithValidData(){
        String id = "P001";
        String loanId = "L001";
        int noBulan = 1;
        Money amount = new Money(new BigDecimal("2500000"), "IDR");
        LocalDate dueDate = LocalDate.of(2026, 6, 1);

        Payment payment = new Payment(id, loanId, noBulan, amount, dueDate);

        
        assertEquals(id, payment.getId());
        assertEquals(loanId, payment.getLoanId());
        assertEquals(noBulan, payment.getNoBulan());
        assertEquals(amount, payment.getAmount());
        assertEquals(dueDate, payment.getDueDate());
    }

    @Test // test 2
    void shouldHavePendingStatusWhenCreated() {
        String id = "P001";
        String loanId = "L001";
        int noBulan = 1;
        Money amount = new Money(new BigDecimal("2500000"), "IDR");
        LocalDate dueDate = LocalDate.of(2026, 6, 1);

        Payment payment = new Payment(id, loanId, noBulan, amount, dueDate);

        assertEquals(id, payment.getId());
        assertEquals(loanId, payment.getLoanId());
        assertEquals(noBulan, payment.getNoBulan());
        assertEquals(amount, payment.getAmount());
        assertEquals(dueDate, payment.getDueDate());
        assertEquals("PENDING", payment.getStatus().name());
    }
    
    @Test // test 3
    void shouldThrowExceptionWhenAmountIsNull() {
        String id = "P001";
        String loanId = "L001";
        int noBulan = 1;
        Money amount = null;
        LocalDate dueDate = LocalDate.of(2026, 6, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            Payment payment = new Payment(id, loanId, noBulan, amount, dueDate);
        });
    }

    @Test // test 4
    void shouldThrowExceptionWhenDueDateIsNull() {
        String id = "P001";
        String loanId = "L001";
        int noBulan = 1;
        Money amount = new Money(new BigDecimal("2500000"), "IDR");
        LocalDate dueDate = null;

        assertThrows(IllegalArgumentException.class, () -> {
            Payment payment = new Payment(id, loanId, noBulan, amount, dueDate);
        });
    }
}
