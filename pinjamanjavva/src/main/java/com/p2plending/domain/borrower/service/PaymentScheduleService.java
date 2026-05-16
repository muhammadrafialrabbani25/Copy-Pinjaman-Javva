package com.p2plending.domain.borrower.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.service.strategy.FlatInterestStrategy;
import com.p2plending.domain.borrower.service.strategy.PaymentCalculationStrategy;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

/**
 * Service untuk menghitung jadwal pembayaran cicilan pinjaman.
 */
public class PaymentScheduleService {

    private final PaymentCalculationStrategy calculationStrategy;

    /**
     * Konstruktor dengan default strategy (FlatInterestStrategy).
     */
    public PaymentScheduleService() {
        this.calculationStrategy = new FlatInterestStrategy();
    }

    /**
     * Konstruktor dengan custom strategy
     */
    public PaymentScheduleService(PaymentCalculationStrategy calculationStrategy) {
        if (calculationStrategy == null) {
            throw new IllegalArgumentException("Calculation strategy must not be null");
        }
        this.calculationStrategy = calculationStrategy;
    }

    /**
     * Menghitung cicilan bulanan berdasarkan strategi yang digunakan.
     */
    public Money calculateMonthlyPayment(Money loanAmount, Tenor tenor, double interestRate) {
        return calculationStrategy.calculateMonthlyPayment(loanAmount, tenor, interestRate);
    }

    public List<Payment> generatePaymentSchedule(
            String loanId,
            Money loanAmount,
            Tenor tenor,
            double interestRate) {
        if (loanId == null || loanAmount == null || tenor == null) {
            throw new IllegalArgumentException("All parameters must not be null");
        }

        List<Payment> schedule = new ArrayList<>();

        Money monthlyPayment = calculateMonthlyPayment(loanAmount, tenor, interestRate);

        int numberOfMonths = tenor.getMonths();
        LocalDate dueDate = LocalDate.now();

        for (int month = 1; month <= numberOfMonths; month++) {
            Payment payment = new Payment(
                    UUID.randomUUID().toString(),
                    loanId,
                    month,
                    monthlyPayment,
                    dueDate);
            schedule.add(payment);

            dueDate = dueDate.plusMonths(1);
        }

        return schedule;
    }
}