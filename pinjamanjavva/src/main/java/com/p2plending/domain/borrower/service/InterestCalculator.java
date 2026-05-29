package com.p2plending.domain.borrower.service;

import com.p2plending.domain.shared.Money;

import java.math.BigDecimal;

/**
 * Interface untuk strategi perhitungan bunga pinjaman.
 * Implementasi STRATEGY pattern untuk memungkinkan berbagai cara perhitungan
 * bunga.
 */
public interface InterestCalculator {

    /**
     * Menghitung bunga dari jumlah pokok.
     */
    BigDecimal calculate(BigDecimal principal);

    /**
     * Menghitung bunga dari Money object.
     */
    Money calculateFromMoney(Money money);
}
