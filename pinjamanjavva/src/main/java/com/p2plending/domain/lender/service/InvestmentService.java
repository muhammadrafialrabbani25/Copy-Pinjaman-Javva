package com.p2plending.domain.lender.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.p2plending.domain.shared.Money;

/**
 * Mengelola validasi dan kalkulasi investasi lender
 */
public class InvestmentService {

    private static final BigDecimal MINIMUM_INVESTMENT_PERCENTAGE = new BigDecimal("0.20");
    private static final BigDecimal ADMIN_FEE_PERCENTAGE = new BigDecimal("0.02");
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * Validasi apakah investasi memenuhi minimum 20% dari loan amount
     */
    public boolean validateMinimumInvestment(Money loanAmount, Money investmentAmount) {
        if (loanAmount == null) {
            throw new IllegalArgumentException("Loan amount tidak boleh null");
        }
        if (investmentAmount == null) {
            throw new IllegalArgumentException("Investment amount tidak boleh null");
        }

        // Hitung persentase: investasi / loan
        BigDecimal percentage = investmentAmount.getAmount()
                .divide(loanAmount.getAmount(), SCALE, ROUNDING_MODE);

        return percentage.compareTo(MINIMUM_INVESTMENT_PERCENTAGE) >= 0;
    }

    /**
     * Kalkulasi biaya admin 2% dari investasi
     */
    public Money calculateAdminFee(Money investmentAmount) {
        if (investmentAmount == null) {
            throw new IllegalArgumentException("Investment amount tidak boleh null");
        }

        BigDecimal fee = investmentAmount.getAmount()
                .multiply(ADMIN_FEE_PERCENTAGE)
                .setScale(0, ROUNDING_MODE);

        return new Money(fee, investmentAmount.getCurrency());
    }

    /**
     * Kalkulasi jumlah investasi final setelah potongan admin fee 2%
     */
    public Money calculateFinalAmount(Money investmentAmount) {
        if (investmentAmount == null) {
            throw new IllegalArgumentException("Investment amount tidak boleh null");
        }

        BigDecimal netPercentage = BigDecimal.ONE.subtract(ADMIN_FEE_PERCENTAGE);
        BigDecimal finalAmount = investmentAmount.getAmount()
                .multiply(netPercentage)
                .setScale(0, ROUNDING_MODE);

        return new Money(finalAmount, investmentAmount.getCurrency());
    }
}