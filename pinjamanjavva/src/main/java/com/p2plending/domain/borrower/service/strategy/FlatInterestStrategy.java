package com.p2plending.domain.borrower.service.strategy;

import java.math.BigDecimal;

import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

/**
 * Strategi Flat Interest: Bunga dihitung berdasarkan pokok awal dan
 * dialokasikan
 * secara merata ke setiap bulan pembayaran.

 * Formula: (Principal + (Principal × Rate × Months/12)) / Months
 */
public class FlatInterestStrategy implements PaymentCalculationStrategy {

    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");
    private static final int INTEREST_SCALE = 2;
    private static final int PAYMENT_SCALE = 0;

    @Override
    public Money calculateMonthlyPayment(Money loanAmount, Tenor tenor, double interestRate) {
        if (loanAmount == null || tenor == null) {
            throw new IllegalArgumentException("Loan amount and tenor must not be null");
        }

        int numberOfMonths = tenor.getMonths();
        BigDecimal principal = loanAmount.getAmount();

        // Menghitung total bunga: Principal × Rate × (Months / 12)
        BigDecimal rate = new BigDecimal(String.valueOf(interestRate));
        BigDecimal monthsDecimal = new BigDecimal(numberOfMonths);
        BigDecimal totalInterest = principal
                .multiply(rate)
                .multiply(monthsDecimal)
                .divide(MONTHS_IN_YEAR, INTEREST_SCALE, BigDecimal.ROUND_HALF_UP);

        // Total yang harus dibayar = Principal + Bunga
        BigDecimal totalToPay = principal.add(totalInterest);

        // Cicilan Bulanan = Total / Bulan (dibulatkan ke rupiah terdekat)
        BigDecimal monthlyPaymentAmount = totalToPay
                .divide(new BigDecimal(numberOfMonths), PAYMENT_SCALE, BigDecimal.ROUND_HALF_UP);

        return new Money(monthlyPaymentAmount, loanAmount.getCurrency());
    }
}
