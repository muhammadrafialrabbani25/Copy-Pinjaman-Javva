package com.p2plending.domain.borrower.service;

import com.p2plending.domain.shared.Money;

import java.math.BigDecimal;

/**
 * Implementasi InterestCalculator dengan bunga sederhana (flat 3%).
 * Menghitung bunga = 3% × principal.
 */
public class SimpleInterestCalculator implements InterestCalculator {

    private static final BigDecimal INTEREST_RATE = new BigDecimal("0.03");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * Menghitung bunga 3% dari jumlah pokok.
     */
    @Override
    public BigDecimal calculate(BigDecimal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal must not be null");
        }

        if (principal.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Principal must not be negative");
        }

        return principal.multiply(INTEREST_RATE);
    }

    /**
     * Menghitung bunga 3% dari Money object.
     */
    @Override
    public Money calculateFromMoney(Money money) {
        if (money == null) {
            throw new IllegalArgumentException("Money must not be null");
        }

        BigDecimal interestAmount = calculate(money.getAmount());
        return new Money(interestAmount, money.getCurrency());
    }

    /**
     * Menghitung bunga bulanan (alias calculate()).
     */
    public BigDecimal calculateMonthlyInterest(BigDecimal principal) {
        return calculate(principal);
    }

    /**
     * Mendapatkan interest rate (0.03 = 3%).
     */
    public BigDecimal getInterestRate() {
        return INTEREST_RATE;
    }
}
