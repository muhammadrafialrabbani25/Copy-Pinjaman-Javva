package com.p2plending.domain.lender.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

public class InvestmentServiceTest {

    private InvestmentService investmentService = new InvestmentService();

    // validasi minimum Invest Tests

    @Test
    public void testValidateMinimumInvestment_ZeroAmount_ReturnsFalse() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(BigDecimal.ZERO, "IDR");

        boolean result = investmentService.validateMinimumInvestment(loanAmount, investmentAmount);

        assertFalse(result);
    }

    @Test
    public void testValidateMinimumInvestment_BelowMinimum_ReturnsFalse() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("19000000"), "IDR");

        boolean result = investmentService.validateMinimumInvestment(loanAmount, investmentAmount);

        assertFalse(result);
    }

    @Test
    public void testValidateMinimumInvestment_ExactlyMinimum_ReturnsTrue() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("20000000"), "IDR");

        boolean result = investmentService.validateMinimumInvestment(loanAmount, investmentAmount);

        assertTrue(result);
    }

    @Test
    public void testValidateMinimumInvestment_AboveMinimum_ReturnsTrue() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("25000000"), "IDR");

        boolean result = investmentService.validateMinimumInvestment(loanAmount, investmentAmount);

        assertTrue(result);
    }

    @Test
    public void testValidateMinimumInvestment_HalfAmount_ReturnsTrue() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("50000000"), "IDR");

        boolean result = investmentService.validateMinimumInvestment(loanAmount, investmentAmount);

        assertTrue(result);
    }

    @Test
    public void testValidateMinimumInvestment_SmallLoan_ReturnsTrue() {
        Money loanAmount = new Money(new BigDecimal("50000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("10000000"), "IDR");

        boolean result = investmentService.validateMinimumInvestment(loanAmount, investmentAmount);

        assertTrue(result);
    }

    @Test
    public void testValidateMinimumInvestment_NullLoanAmount_ThrowsException() {
        Money investmentAmount = new Money(new BigDecimal("20000000"), "IDR");

        assertThrows(IllegalArgumentException.class,
                () -> investmentService.validateMinimumInvestment(null, investmentAmount));
    }

    @Test
    public void testValidateMinimumInvestment_NullInvestmentAmount_ThrowsException() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");

        assertThrows(IllegalArgumentException.class,
                () -> investmentService.validateMinimumInvestment(loanAmount, null));
    }

    // Menghitung Admin Fee Tests

    @Test
    public void testCalculateAdminFee_1MillionTopUp_Returns20Thousand() {
        Money topUpAmount = new Money(new BigDecimal("1000000"), "IDR");

        Money result = investmentService.calculateAdminFee(topUpAmount);

        assertEquals(new BigDecimal("20000"), result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateAdminFee_5MillionTopUp_Returns100Thousand() {
        Money topUpAmount = new Money(new BigDecimal("5000000"), "IDR");

        Money result = investmentService.calculateAdminFee(topUpAmount);

        assertEquals(new BigDecimal("100000"), result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateAdminFee_10MillionTopUp_Returns200Thousand() {
        Money topUpAmount = new Money(new BigDecimal("10000000"), "IDR");

        Money result = investmentService.calculateAdminFee(topUpAmount);

        assertEquals(new BigDecimal("200000"), result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateAdminFee_ZeroAmount_ReturnsZero() {
        Money topUpAmount = new Money(BigDecimal.ZERO, "IDR");

        Money result = investmentService.calculateAdminFee(topUpAmount);

        assertEquals(BigDecimal.ZERO, result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateAdminFee_NullAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> investmentService.calculateAdminFee(null));
    }

    @Test
    public void testCalculateFinalAmount_1MillionTopUp_Returns980Thousand() {
        Money topUpAmount = new Money(new BigDecimal("1000000"), "IDR");

        Money result = investmentService.calculateFinalAmount(topUpAmount);

        assertEquals(new BigDecimal("980000"), result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateFinalAmount_5MillionTopUp_Returns4Point9Million() {
        Money topUpAmount = new Money(new BigDecimal("5000000"), "IDR");

        Money result = investmentService.calculateFinalAmount(topUpAmount);

        assertEquals(new BigDecimal("4900000"), result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateFinalAmount_10MillionTopUp_Returns9Point8Million() {
        Money topUpAmount = new Money(new BigDecimal("10000000"), "IDR");

        Money result = investmentService.calculateFinalAmount(topUpAmount);

        assertEquals(new BigDecimal("9800000"), result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateFinalAmount_ZeroAmount_ReturnsZero() {
        Money topUpAmount = new Money(BigDecimal.ZERO, "IDR");

        Money result = investmentService.calculateFinalAmount(topUpAmount);

        assertEquals(BigDecimal.ZERO, result.getAmount());
        assertEquals("IDR", result.getCurrency());
    }

    @Test
    public void testCalculateFinalAmount_NullAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> investmentService.calculateFinalAmount(null));
    }

    @Test
    public void testFullWorkflow_ValidInvestmentWith20Percent() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("20000000"), "IDR");

        // Validasi minimum 20%
        assertTrue(investmentService.validateMinimumInvestment(loanAmount, investmentAmount));

        // menghitung admin fee (20M * 2%)
        Money adminFee = investmentService.calculateAdminFee(investmentAmount);
        assertEquals(new BigDecimal("400000"), adminFee.getAmount());

        // Menghitung final bayaran (20M - 400K)
        Money finalAmount = investmentService.calculateFinalAmount(investmentAmount);
        assertEquals(new BigDecimal("19600000"), finalAmount.getAmount());
    }

    /**
     * Test: Large investment - 50M invest from 100M loan
     * 50M = 50% (well above 20%), admin fee 1M, final 49M
     */
    @Test
    public void testFullWorkflow_LargeInvestment50Percent() {
        Money loanAmount = new Money(new BigDecimal("100000000"), "IDR");
        Money investmentAmount = new Money(new BigDecimal("50000000"), "IDR");

        // Validate minimum 20%
        assertTrue(investmentService.validateMinimumInvestment(loanAmount, investmentAmount));

        // Calculate admin fee (50M * 2%)
        Money adminFee = investmentService.calculateAdminFee(investmentAmount);
        assertEquals(new BigDecimal("1000000"), adminFee.getAmount());

        // Calculate final amount (50M - 1M)
        Money finalAmount = investmentService.calculateFinalAmount(investmentAmount);
        assertEquals(new BigDecimal("49000000"), finalAmount.getAmount());
    }
}
