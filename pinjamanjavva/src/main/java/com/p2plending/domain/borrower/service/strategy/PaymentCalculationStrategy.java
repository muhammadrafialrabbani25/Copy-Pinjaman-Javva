package com.p2plending.domain.borrower.service.strategy;

import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

/**
 * Contract interface untuk strategi perhitungan cicilan pinjaman.
 */
public interface PaymentCalculationStrategy {

    /**
     * Menghitung cicilan bulanan berdasarkan strategi yang diimplementasikan.
     */
    Money calculateMonthlyPayment(Money loanAmount, Tenor tenor, double interestRate);
}
