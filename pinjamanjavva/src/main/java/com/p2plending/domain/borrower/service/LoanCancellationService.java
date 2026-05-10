package com.p2plending.domain.borrower.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.p2plending.domain.shared.Money;

/**
 * Menangani logika pembatalan pinjaman dengan validasi investasi minimum dan blocking
 */
public class LoanCancellationService {

    private static final BigDecimal MINIMUM_INVESTMENT_PERCENTAGE = new BigDecimal("0.20");
    private static final int MAX_CANCELLATION_COUNT = 3;
    private static final int BLOCK_MONTHS = 4;

    public boolean canCancelLoan(Money loanAmount, Money fundedAmount, int cancellationCount) {
        if (loanAmount == null || fundedAmount == null) {
            throw new IllegalArgumentException("Loan amount dan funded amount tidak boleh null");
        }

        // Cek apakah sudah mencapai batas pembatalan
        if (cancellationCount >= MAX_CANCELLATION_COUNT) {
            return false;
        }

        // Cek apakah investasi minimum sudah terpenuhi
        return isMinimumInvestmentMet(loanAmount, fundedAmount);
    }

    public boolean isMinimumInvestmentMet(Money loanAmount, Money fundedAmount) {
        BigDecimal principal = loanAmount.getAmount();
        BigDecimal funded = fundedAmount.getAmount();

        // Hitung persentase: funded / principal
        BigDecimal percentage = funded.divide(principal, 2, BigDecimal.ROUND_HALF_UP);

        // Bandingkan dengan minimum 20%
        return percentage.compareTo(MINIMUM_INVESTMENT_PERCENTAGE) >= 0;
    }

    public LocalDate calculateBlockUntilDate(LocalDate cancellationDate, int cancellationCount) {
        if (cancellationCount >= MAX_CANCELLATION_COUNT) {
            return cancellationDate.plusMonths(BLOCK_MONTHS);
        }
        return cancellationDate;
    }

    public int incrementCancellationCount(int currentCount) {
        return currentCount + 1;
    }
}
