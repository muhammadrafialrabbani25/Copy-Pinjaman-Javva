package com.p2plending.domain.borrower.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.shared.Money;

public class PaymentTest {

    @Test // test 1
    void shouldCreatePaymentWithValidData() {
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
            new Payment(id, loanId, noBulan, amount, dueDate);
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
            new Payment(id, loanId, noBulan, amount, dueDate);
        });
    }

    @Test // test 5
    void shouldHaveNullPaidDateWhenCreated() {
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("100000"), "IDR"),
                LocalDate.now().plusDays(10));

        assertNull(payment.getPaidDate());
    }

    @Test // test 6
    void shouldHaveZeroDendaWhenCreated() {
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("100000"), "IDR"),
                LocalDate.now().plusDays(10));

        assertEquals(BigDecimal.ZERO, payment.getDenda());
    }

    @Test // test 7
    void shouldReturnTrueWhenPaymentIsOverdue() {
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("100000"), "IDR"),
                LocalDate.now().minusDays(5));

        assertTrue(payment.isOverdue());
    }

    @Test // test 8
    void shouldReturnFalseWhenPaymentIsNotOverdue() {
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("100000"), "IDR"),
                LocalDate.now().plusDays(5));

        assertFalse(payment.isOverdue());
    }

    @Test // test 9
    void shouldCalculateDendaWhenLateMoreThan30Days() {
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("100000"), "IDR"),
                LocalDate.now().minusDays(40));

        payment.calculateDenda();

        assertTrue(payment.getDenda().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test // test 10
    void shouldReturnZeroDendaWhenLate30DaysOrLess() {
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("100000"), "IDR"),
                LocalDate.now().minusDays(10));

        payment.calculateDenda();

        assertEquals(BigDecimal.ZERO, payment.getDenda());
    }
}