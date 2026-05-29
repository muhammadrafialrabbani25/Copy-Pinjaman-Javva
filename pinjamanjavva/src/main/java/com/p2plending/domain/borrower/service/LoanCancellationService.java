package com.p2plending.domain.borrower.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.p2plending.domain.shared.Money;

/**
 * Menangani logika pembatalan pinjaman dengan validasi investasi minimum dan
 * blocking.
 * 
 * REVISED RULES:
 * - Cancel always allowed dari FUNDING state
 * - Jika investment < 20%: NO penalty (counter tetap)
 * - Jika investment >= 20%: WITH penalty (counter +1)
 * - Block hanya berlaku jika sudah cancel 3x dengan penalty
 */
public class LoanCancellationService {

    private static final BigDecimal MINIMUM_INVESTMENT_PERCENTAGE = new BigDecimal("0.20");
    private static final int MAX_CANCELLATION_COUNT = 3;
    private static final int BLOCK_MONTHS = 4;

    /**
     * Cek apakah cancellation akan menghasilkan penalty.
     * Penalty apply jika total investment >= 20% dari loan amount.
     * 
     * @param loanAmount   jumlah pinjaman
     * @param fundedAmount total investasi
     * @return true jika penalty akan diterapkan, false jika tidak
     */
    public boolean shouldApplyPenalty(Money loanAmount, Money fundedAmount) {
        if (loanAmount == null || fundedAmount == null) {
            throw new IllegalArgumentException("Loan amount dan funded amount tidak boleh null");
        }
        return hasReachedInvestmentThreshold(loanAmount, fundedAmount);
    }

    /**
     * Cek apakah total investment sudah mencapai 20% dari loan amount.
     * 
     * @return true jika >= 20%, false jika < 20%
     */
    public boolean hasReachedInvestmentThreshold(Money loanAmount, Money fundedAmount) {
        BigDecimal principal = loanAmount.getAmount();
        BigDecimal funded = fundedAmount.getAmount();

        // Hitung persentase: funded / principal
        BigDecimal percentage = funded.divide(principal, 2, BigDecimal.ROUND_HALF_UP);

        // Bandingkan dengan minimum 20%
        return percentage.compareTo(MINIMUM_INVESTMENT_PERCENTAGE) >= 0;
    }

    /**
     * Cek apakah borrower sudah terkena block dari cancellation sebelumnya.
     * 
     * @param lastBlockedDate tanggal terakhir diblock
     * @return true jika masih dalam block period, false jika block sudah selesai
     */
    public boolean isCurrentlyBlocked(LocalDate lastBlockedDate) {
        if (lastBlockedDate == null) {
            return false;
        }
        return LocalDate.now().isBefore(lastBlockedDate);
    }

    /**
     * Hitung kapan block berakhir setelah cancel yang ke-3x dengan penalty.
     * Block hanya berlaku jika cancellationCount sudah mencapai 3x.
     * 
     * @param cancellationDate  tanggal cancel
     * @param cancellationCount counter cancel (setelah increment)
     * @return tanggal block berakhir, atau cancellationDate jika tidak ada block
     */
    public LocalDate calculateBlockUntilDate(LocalDate cancellationDate, int cancellationCount) {
        // Block hanya jika sudah cancel 3x dengan penalty
        if (cancellationCount >= MAX_CANCELLATION_COUNT) {
            return cancellationDate.plusMonths(BLOCK_MONTHS);
        }
        // Return cancellation date (no block applied)
        return cancellationDate;
    }

    /**
     * Increment cancellation counter (hanya jika ada penalty).
     */
    public int incrementCancellationCount(int currentCount) {
        return currentCount + 1;
    }

    /**
     * Cek apakah borrower bisa melakukan cancel.
     * 
     * Cancel SELALU dibolehkan KECUALI:
     * - Sudah mencapai 3x cancel dengan penalty → BLOCKED
     * 
     * Penalty logic (NO BLOCKING):
     * - Jika investment < 20%: ALLOWED, NO PENALTY (no counter increment)
     * - Jika investment >= 20%: ALLOWED, WITH PENALTY (counter+1)
     * - Jika counter >= 3 dengan penalty: BLOCKED
     * 
     * @param loanAmount        jumlah pinjaman
     * @param fundedAmount      total investasi
     * @param cancellationCount jumlah cancel yang sudah dilakukan DENGAN PENALTY
     * @return true jika bisa cancel, false jika sudah block (3x cancel dgn penalty)
     */
    public boolean canCancelLoan(Money loanAmount, Money fundedAmount, int cancellationCount) {
        // Hanya block jika sudah 3x cancel DENGAN PENALTY
        if (cancellationCount >= MAX_CANCELLATION_COUNT) {
            return false;
        }

        // ALLOWED dalam semua kasus lain (investment < 20% atau < 3x)
        return true;
    }

    /**
     * Cek apakah minimum investasi (20%) sudah terpenuhi.
     * Alias untuk hasReachedInvestmentThreshold untuk backward compatibility.
     * 
     * @param loanAmount   jumlah pinjaman
     * @param fundedAmount total investasi
     * @return true jika >= 20%, false jika < 20%
     */
    public boolean isMinimumInvestmentMet(Money loanAmount, Money fundedAmount) {
        return hasReachedInvestmentThreshold(loanAmount, fundedAmount);
    }
}
