package com.p2plending.domain.borrower.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.shared.Money;

class SimpleInterestCalculatorTest {

    private SimpleInterestCalculator calculator = new SimpleInterestCalculator();

    // ============= calculate Tests =============

    @Test
    void calculate_1MillionPrincipal_ShouldReturn30Thousand() {
        // Arrange
        BigDecimal principal = new BigDecimal("1000000");

        // Act
        BigDecimal interest = calculator.calculate(principal);

        // Assert
        // 3% × 1000000 = 30000
        assertEquals(0, new BigDecimal("30000").compareTo(interest));
    }

    @Test
    void calculate_ZeroPrincipal_ShouldReturnZero() {
        // Arrange
        BigDecimal principal = BigDecimal.ZERO;

        // Act
        BigDecimal interest = calculator.calculate(principal);

        // Assert
        assertEquals(0, BigDecimal.ZERO.compareTo(interest));
    }

    @Test
    void calculate_SmallPrincipal_ShouldCalculateCorrectly() {
        // Arrange
        BigDecimal principal = new BigDecimal("100");

        // Act
        BigDecimal interest = calculator.calculate(principal);

        // Assert
        // 3% × 100 = 3
        assertEquals(0, new BigDecimal("3").compareTo(interest));
    }

    @Test
    void calculate_LargePrincipal_ShouldCalculateCorrectly() {
        // Arrange
        BigDecimal principal = new BigDecimal("100000000");

        // Act
        BigDecimal interest = calculator.calculate(principal);

        // Assert
        // 3% × 100000000 = 3000000
        assertEquals(0, new BigDecimal("3000000").compareTo(interest));
    }

    @Test
    void calculate_FloatingPointPrincipal_ShouldCalculateCorrectly() {
        // Arrange
        BigDecimal principal = new BigDecimal("1234567.89");

        // Act
        BigDecimal interest = calculator.calculate(principal);

        // Assert
        // 3% × 1234567.89 = 37037.0367 (or rounded)
        BigDecimal expected = new BigDecimal("37037.0367");
        assertEquals(expected, interest);
    }

    @Test
    void calculate_NullPrincipal_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(null);
        });
    }

    @Test
    void calculate_NegativePrincipal_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(new BigDecimal("-1000000"));
        });
    }

    // ============= calculateFromMoney Tests =============

    @Test
    void calculateFromMoney_ValidMoney_ShouldReturnMoneyWithInterest() {
        // Arrange
        Money principal = new Money(new BigDecimal("1000000"), "IDR");

        // Act
        Money interest = calculator.calculateFromMoney(principal);

        // Assert
        assertNotNull(interest);
        assertEquals(0, new BigDecimal("30000").compareTo(interest.getAmount()));
        assertEquals("IDR", interest.getCurrency());
    }

    @Test
    void calculateFromMoney_ValidMoneyUSD_ShouldPreserveCurrency() {
        // Arrange
        Money principal = new Money(new BigDecimal("1000"), "USD");

        // Act
        Money interest = calculator.calculateFromMoney(principal);

        // Assert
        assertNotNull(interest);
        assertEquals(0, new BigDecimal("30").compareTo(interest.getAmount()));
        assertEquals("USD", interest.getCurrency());
    }

    @Test
    void calculateFromMoney_NullMoney_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateFromMoney(null);
        });
    }

    // ============= calculateMonthlyInterest Tests =============

    @Test
    void calculateMonthlyInterest_ValidPrincipal_ShouldReturnSameAsCalculate() {
        // Arrange
        BigDecimal principal = new BigDecimal("1000000");

        // Act
        BigDecimal result1 = calculator.calculateMonthlyInterest(principal);
        BigDecimal result2 = calculator.calculate(principal);

        // Assert
        assertEquals(result1, result2);
    }

    @Test
    void calculateMonthlyInterest_12Months_ShouldCalculateCorrectly() {
        // Arrange
        BigDecimal monthlyPrincipal = new BigDecimal("1000000");
        int months = 12;

        // Act
        BigDecimal monthlyInterest = calculator.calculateMonthlyInterest(monthlyPrincipal);
        BigDecimal totalInterestFor12Months = monthlyInterest.multiply(new BigDecimal(months));

        // Assert
        // Total interest for 12 months = 3% × 1000000 × 12 = 360000
        assertEquals(0, new BigDecimal("360000").compareTo(totalInterestFor12Months));
    }

    // ============= getInterestRate Tests =============

    @Test
    void getInterestRate_ShouldReturn0Point03() {
        // Act
        BigDecimal rate = calculator.getInterestRate();

        // Assert
        assertEquals(new BigDecimal("0.03"), rate);
    }

    // ============= Implementation Tests =============

    @Test
    void shouldImplementInterestCalculator() {
        // Assert
        assertTrue(calculator instanceof InterestCalculator);
    }

    @Test
    void interestRateConstant_ShouldBe0Point03() {
        // Act
        BigDecimal rate = calculator.getInterestRate();

        // Assert
        assertTrue(rate.compareTo(new BigDecimal("0.03")) == 0);
    }

    private void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Assertion failed");
        }
    }
}
