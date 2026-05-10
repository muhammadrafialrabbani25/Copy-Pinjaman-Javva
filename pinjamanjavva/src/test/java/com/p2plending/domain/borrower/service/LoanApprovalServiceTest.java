package com.p2plending.domain.borrower.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.Money;

class LoanApprovalServiceTest {

    private LoanApprovalService service;

    @BeforeEach
    void setUp() {
        service = new LoanApprovalService();
    }

    // ============ KTP Verification Tests ============

    @Test
    void testVerifyValidKTP() {
        // Arrange
        KTP validKtp = new KTP("Budi", "1234567890123456");

        // Act
        boolean result = service.verifyKTP(validKtp);

        // Assert
        assertTrue(result, "Valid KTP should be verified");
    }

    @Test
    void testVerifyKTP_NullInput() {
        // Act
        boolean result = service.verifyKTP(null);

        // Assert
        assertFalse(result, "Null KTP should fail verification");
    }

    // ============ Loan Limit Calculation Tests ============

    @Test
    void testCalculateLoanLimit_Basic() {
        // Arrange
        Money salary = new Money(new BigDecimal("10000000"), "IDR"); // 10M

        // Act
        Money limit = service.calculateLoanLimit(salary);

        // Assert
        assertEquals(new BigDecimal("30000000"), limit.getAmount(),
                "Loan limit should be 3x salary");
    }

    @Test
    void testCalculateLoanLimit_SmallSalary() {
        // Arrange
        Money salary = new Money(new BigDecimal("1000000"), "IDR"); // 1M

        // Act
        Money limit = service.calculateLoanLimit(salary);

        // Assert
        assertEquals(new BigDecimal("3000000"), limit.getAmount(),
                "Loan limit should be 3x salary");
    }

    @Test
    void testCalculateLoanLimit_LargeSalary() {
        // Arrange
        Money salary = new Money(new BigDecimal("100000000"), "IDR"); // 100M

        // Act
        Money limit = service.calculateLoanLimit(salary);

        // Assert
        assertEquals(new BigDecimal("300000000"), limit.getAmount(),
                "Loan limit should be 3x salary");
    }

    // ============ Credit Score Verification Tests ============

    @Test
    void testVerifyValidCreditScore_Minimum() {
        // Arrange
        int creditScore = 600; // Minimum valid score

        // Act
        boolean result = service.verifyCreditScore(creditScore);

        // Assert
        assertTrue(result, "Credit score 600 should pass");
    }

    @Test
    void testVerifyValidCreditScore_Maximum() {
        // Arrange
        int creditScore = 1000; // Maximum valid score

        // Act
        boolean result = service.verifyCreditScore(creditScore);

        // Assert
        assertTrue(result, "Credit score 1000 should pass");
    }

    @Test
    void testVerifyValidCreditScore_Middle() {
        // Arrange
        int creditScore = 750; // Mid-range score

        // Act
        boolean result = service.verifyCreditScore(creditScore);

        // Assert
        assertTrue(result, "Credit score 750 should pass");
    }

    @Test
    void testVerifyInvalidCreditScore_TooLow() {
        // Arrange
        int creditScore = 599; // Below minimum

        // Act
        boolean result = service.verifyCreditScore(creditScore);

        // Assert
        assertFalse(result, "Credit score below 600 should fail");
    }

    @Test
    void testVerifyInvalidCreditScore_TooHigh() {
        // Arrange
        int creditScore = 1001; // Above maximum

        // Act
        boolean result = service.verifyCreditScore(creditScore);

        // Assert
        assertFalse(result, "Credit score above 1000 should fail");
    }

    @Test
    void testVerifyInvalidCreditScore_Negative() {
        // Arrange
        int creditScore = -100; // Negative value

        // Act
        boolean result = service.verifyCreditScore(creditScore);

        // Assert
        assertFalse(result, "Negative credit score should fail");
    }
}
