package com.p2plending.domain.borrower.service;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service untuk mengelola proses pembayaran dan perhitungan denda.
 * Stateless service yang mengimplementasikan logika bisnis repayment.
 */
public class RepaymentService {

    private static final int DENDA_THRESHOLD_DAYS = 30;
    private static final BigDecimal DENDA_RATE = new BigDecimal("0.01");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * Memproses pembayaran cicilan pinjaman.
     */
    public void makePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment must not be null");
        }
        payment.pay();
    }

    /**
     * Mengecek dan mengupdate status pembayaran secara otomatis.
     */
    public void checkAndUpdateStatus(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment must not be null");
        }

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        if (payment.isOverdue()) {
            payment.calculateDenda();
        }
    }

    /**
     * Menghitung denda untuk pembayaran yang terlambat.
     */
    public BigDecimal calculateDenda(BigDecimal principal, long daysLate) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal must not be null");
        }

        if (principal.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Principal must not be negative");
        }

        if (daysLate <= DENDA_THRESHOLD_DAYS) {
            return ZERO;
        }

        return principal.multiply(DENDA_RATE);
    }

    /**
     * Menghitung denda dari Payment object.
     */
    public BigDecimal calculateDendaForPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment must not be null");
        }

        long daysLate = ChronoUnit.DAYS.between(payment.getDueDate(), LocalDate.now());
        BigDecimal principal = payment.getAmount().getAmount();

        return calculateDenda(principal, daysLate);
    }

    /**
     * Memvalidasi jumlah pembayaran.
     */
    public void validatePaymentAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Payment amount must not be null");
        }

        if (amount.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    }

    /**
     * Memvalidasi jumlah pembayaran (Money object).
     */
    public void validatePaymentAmount(Money money) {
        if (money == null) {
            throw new IllegalArgumentException("Payment amount (Money) must not be null");
        }

        validatePaymentAmount(money.getAmount());
    }
}
