package com.p2plending.domain.borrower.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

class PaymentScheduleServiceTest {

    private PaymentScheduleService service = new PaymentScheduleService();

    // ============ Test Perhitungan Cicilan Bulanan ============

    @Test
    void testCalculateMonthlyPayment_1MonthTenor() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR"); // 12M
        Tenor tenor = Tenor.ONE_MONTH;
        double interestRate = 0.10; // 10% per tahun

        // Act
        Money monthlyPayment = service.calculateMonthlyPayment(loanAmount, tenor, interestRate);

        // Assert (Verifikasi)
        assertNotNull(monthlyPayment);
        
        assertEquals(new BigDecimal("12100000"), monthlyPayment.getAmount());
    }

    @Test
    void testCalculateMonthlyPayment_3MonthTenor() {
        // Arrange 
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR"); // 12M
        Tenor tenor = Tenor.THREE_MONTHS;
        double interestRate = 0.10; // 10% per tahun

        // Act 
        Money monthlyPayment = service.calculateMonthlyPayment(loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment.getAmount().compareTo(new BigDecimal("4000000")) > 0);
    }

    @Test
    void testCalculateMonthlyPayment_6MonthTenor() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR"); // 12M
        Tenor tenor = Tenor.SIX_MONTHS;
        double interestRate = 0.10; // 10% per annum

        // Act
        Money monthlyPayment = service.calculateMonthlyPayment(loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment.getAmount().compareTo(new BigDecimal("2000000")) > 0);
    }

    @Test
    void testCalculateMonthlyPayment_12MonthTenor() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR"); // 12M
        Tenor tenor = Tenor.TWELVE_MONTHS;
        double interestRate = 0.10; // 10% per annum

        // Act
        Money monthlyPayment = service.calculateMonthlyPayment(loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(monthlyPayment);
        assertTrue(monthlyPayment.getAmount().compareTo(new BigDecimal("1000000")) > 0);
    }

    @Test
    void testCalculateMonthlyPayment_ZeroInterestRate() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR"); // 12M
        Tenor tenor = Tenor.TWELVE_MONTHS;
        double interestRate = 0.0; // 0% interest

        // Act
        Money monthlyPayment = service.calculateMonthlyPayment(loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(monthlyPayment);
        assertEquals(new BigDecimal("1000000"), monthlyPayment.getAmount());
    }

    // ============ Payment Schedule Generation Tests ============

    @Test
    void testGeneratePaymentSchedule_1Month() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR");
        Tenor tenor = Tenor.ONE_MONTH;
        double interestRate = 0.10;

        // Act
        List<Payment> schedule = service.generatePaymentSchedule(
                "LOAN001", loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(schedule);
        assertEquals(1, schedule.size(), "1-month tenor should have 1 payment");
        assertEquals(new BigDecimal("12100000"), schedule.get(0).getAmount().getAmount());
    }

    @Test
    void testGeneratePaymentSchedule_3Months() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR");
        Tenor tenor = Tenor.THREE_MONTHS;
        double interestRate = 0.10;

        // Act
        List<Payment> schedule = service.generatePaymentSchedule(
                "LOAN002", loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(schedule);
        assertEquals(3, schedule.size(), "3-month tenor should have 3 payments");
    }

    @Test
    void testGeneratePaymentSchedule_6Months() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR");
        Tenor tenor = Tenor.SIX_MONTHS;
        double interestRate = 0.10;

        // Act
        List<Payment> schedule = service.generatePaymentSchedule(
                "LOAN003", loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(schedule);
        assertEquals(6, schedule.size(), "6-month tenor should have 6 payments");
    }

    @Test
    void testGeneratePaymentSchedule_12Months() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR");
        Tenor tenor = Tenor.TWELVE_MONTHS;
        double interestRate = 0.10;

        // Act
        List<Payment> schedule = service.generatePaymentSchedule(
                "LOAN004", loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(schedule);
        assertEquals(12, schedule.size(), "12-month tenor should have 12 payments");
    }

    @Test
    void testGeneratePaymentSchedule_AllPaymentsHaveSameAmount() {
        // Arrange
        Money loanAmount = new Money(new BigDecimal("12000000"), "IDR");
        Tenor tenor = Tenor.SIX_MONTHS;
        double interestRate = 0.10;

        // Act
        List<Payment> schedule = service.generatePaymentSchedule(
                "LOAN005", loanAmount, tenor, interestRate);

        // Assert
        assertNotNull(schedule);
        assertEquals(6, schedule.size());

        BigDecimal firstAmount = schedule.get(0).getAmount().getAmount();
        for (Payment payment : schedule) {
            assertEquals(firstAmount, payment.getAmount().getAmount(),
                    "All payments should have equal amount");
        }
    }
}
