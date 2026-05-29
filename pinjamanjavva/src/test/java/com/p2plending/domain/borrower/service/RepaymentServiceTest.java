package com.p2plending.domain.borrower.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.PaymentStatus;

class RepaymentServiceTest {

    private RepaymentService repaymentService;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        repaymentService = new RepaymentService();
        testPayment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().plusDays(10));
    }

    // ============= makePayment Tests =============

    @Test
    void makePayment_ValidPayment_ShouldUpdateStatusToPaid() {
        // Arrange
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().minusDays(5));

        // Act
        repaymentService.makePayment(payment);

        // Assert
        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertTrue(payment.getPaidDate() != null);
    }

    @Test
    void makePayment_NullPayment_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.makePayment(null);
        });
    }

    // ============= checkAndUpdateStatus Tests =============

    @Test
    void checkAndUpdateStatus_PaymentNotOverdue_ShouldRemainPending() {
        // Arrange
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().plusDays(10));

        // Act
        repaymentService.checkAndUpdateStatus(payment);

        // Assert
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void checkAndUpdateStatus_PaymentOverdue_ShouldUpdateToOverdue() {
        // Arrange
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().minusDays(40) // 40 hari yang lalu
        );

        // Act
        repaymentService.checkAndUpdateStatus(payment);

        // Assert
        assertEquals(PaymentStatus.OVERDUE, payment.getStatus());
    }

    @Test
    void checkAndUpdateStatus_PaymentAlreadyPaid_ShouldRemainPaid() {
        // Arrange
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().minusDays(40));
        payment.pay();

        // Act
        repaymentService.checkAndUpdateStatus(payment);

        // Assert
        assertEquals(PaymentStatus.PAID, payment.getStatus());
    }

    @Test
    void checkAndUpdateStatus_NullPayment_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.checkAndUpdateStatus(null);
        });
    }

    // ============= calculateDenda Tests =============

    @Test
    void calculateDenda_OnTimePayment_ShouldReturnZeroDenda() {
        // Arrange
        BigDecimal principal = new BigDecimal("1000000");
        long daysLate = 10;

        // Act
        BigDecimal denda = repaymentService.calculateDenda(principal, daysLate);

        // Assert
        assertEquals(BigDecimal.ZERO, denda);
    }

    @Test
    void calculateDenda_LateExactly30Days_ShouldReturnZeroDenda() {
        // Arrange
        BigDecimal principal = new BigDecimal("1000000");
        long daysLate = 30;

        // Act
        BigDecimal denda = repaymentService.calculateDenda(principal, daysLate);

        // Assert
        assertEquals(BigDecimal.ZERO, denda);
    }

    @Test
    void calculateDenda_LateMoreThan30Days_ShouldReturn1PercentDenda() {
        // Arrange
        BigDecimal principal = new BigDecimal("1000000");
        long daysLate = 40;

        // Act
        BigDecimal denda = repaymentService.calculateDenda(principal, daysLate);

        // Assert
        // Denda = 1% × 1000000 = 10000
        assertEquals(new BigDecimal("10000"), denda);
    }

    @Test
    void calculateDenda_LateMoreThan30Days_WithFloatingPointPrincipal_ShouldCalculateCorrectly() {
        // Arrange
        BigDecimal principal = new BigDecimal("1234567.89");
        long daysLate = 40;

        // Act
        BigDecimal denda = repaymentService.calculateDenda(principal, daysLate);

        // Assert
        // Denda = 1% × 1234567.89 = 12345.67 (or rounded)
        BigDecimal expected = new BigDecimal("12345.6789");
        assertTrue(denda.compareTo(expected) >= 0);
    }

    @Test
    void calculateDenda_NullPrincipal_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.calculateDenda(null, 40);
        });
    }

    @Test
    void calculateDenda_NegativePrincipal_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.calculateDenda(new BigDecimal("-1000000"), 40);
        });
    }

    @Test
    void calculateDenda_ZeroPrincipal_ShouldReturnZeroDenda() {
        // Arrange
        BigDecimal principal = BigDecimal.ZERO;

        // Act
        BigDecimal denda = repaymentService.calculateDenda(principal, 40);

        // Assert
        assertEquals(BigDecimal.ZERO, denda);
    }

    // ============= calculateDendaForPayment Tests =============

    @Test
    void calculateDendaForPayment_OnTimePayment_ShouldReturnZeroDenda() {
        // Arrange
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().plusDays(10));

        // Act
        BigDecimal denda = repaymentService.calculateDendaForPayment(payment);

        // Assert
        assertEquals(BigDecimal.ZERO, denda);
    }

    @Test
    void calculateDendaForPayment_LatePayment40Days_ShouldReturn1PercentDenda() {
        // Arrange
        Payment payment = new Payment(
                "P001",
                "L001",
                1,
                new Money(new BigDecimal("1000000"), "IDR"),
                LocalDate.now().minusDays(40));

        // Act
        BigDecimal denda = repaymentService.calculateDendaForPayment(payment);

        // Assert
        // Denda = 1% × 1000000 = 10000
        assertEquals(new BigDecimal("10000"), denda);
    }

    @Test
    void calculateDendaForPayment_NullPayment_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.calculateDendaForPayment(null);
        });
    }

    // ============= validatePaymentAmount Tests =============

    @Test
    void validatePaymentAmount_ValidPositiveAmount_ShouldNotThrow() {
        // Arrange
        BigDecimal amount = new BigDecimal("1000000");

        // Act & Assert (no exception)
        repaymentService.validatePaymentAmount(amount);
    }

    @Test
    void validatePaymentAmount_ZeroAmount_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.validatePaymentAmount(BigDecimal.ZERO);
        });
    }

    @Test
    void validatePaymentAmount_NegativeAmount_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.validatePaymentAmount(new BigDecimal("-1000000"));
        });
    }

    @Test
    void validatePaymentAmount_NullAmount_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.validatePaymentAmount((BigDecimal) null);
        });
    }

    @Test
    void validatePaymentAmount_ValidMoneyObject_ShouldNotThrow() {
        // Arrange
        Money amount = new Money(new BigDecimal("1000000"), "IDR");

        // Act & Assert (no exception)
        repaymentService.validatePaymentAmount(amount);
    }

    @Test
    void validatePaymentAmount_NullMoneyObject_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            repaymentService.validatePaymentAmount((Money) null);
        });
    }
}
