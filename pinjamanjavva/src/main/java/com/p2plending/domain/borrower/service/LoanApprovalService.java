package com.p2plending.domain.borrower.service;

import java.math.BigDecimal;

import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.Money;

/**
 * Handles loan approval verification logic:
 * - KTP format verification (16 digits)
 * - Loan limit calculation (3x salary)
 * - Credit score validation (600-1000)
 */
public class LoanApprovalService {

    private static final int MIN_CREDIT_SCORE = 600;
    private static final int MAX_CREDIT_SCORE = 1000;
    private static final int KTP_VALID_LENGTH = 16;
    private static final int SALARY_MULTIPLIER = 3;
    private static final String DIGITS_ONLY_PATTERN = "\\d+";

    /**
     * Verify KTP is valid (16-digit format)
     */
    public boolean verifyKTP(KTP ktp) {
        if (ktp == null) {
            return false;
        }

        String nomorKtp = ktp.getNomorKtp();
        if (nomorKtp == null) {
            return false;
        }

        return nomorKtp.length() == KTP_VALID_LENGTH && nomorKtp.matches(DIGITS_ONLY_PATTERN);
    }

    /**
     * Calculate maximum loan limit
     */
    public Money calculateLoanLimit(Money salary) {
        if (salary == null) {
            throw new IllegalArgumentException("Salary must not be null");
        }

        BigDecimal limitAmount = salary.getAmount()
                .multiply(new BigDecimal(SALARY_MULTIPLIER));

        return new Money(limitAmount, salary.getCurrency());
    }

    /**
     * Verify credit score is within valid range (600-1000)
     */
    public boolean verifyCreditScore(int creditScore) {
        return creditScore >= MIN_CREDIT_SCORE && creditScore <= MAX_CREDIT_SCORE;
    }
}