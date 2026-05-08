package com.p2plending.domain.borrower.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

public class PaymentScheduleService {

    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");
    private static final int INTEREST_SCALE = 2;
    private static final int PAYMENT_SCALE = 0;

    public Money calculateMonthlyPayment(Money loanAmount, Tenor tenor, double interestRate) {
        if (loanAmount == null || tenor == null) {
            throw new IllegalArgumentException("Loan amount and tenor must not be null");
        }

        int numberOfMonths = tenor.getMonths();
        BigDecimal principal = loanAmount.getAmount();

        // menghitung cicilan: Principal * Rate * (bulan / 12)
        BigDecimal rate = new BigDecimal(String.valueOf(interestRate));
        BigDecimal monthsDecimal = new BigDecimal(numberOfMonths);
        BigDecimal totalInterest = principal
                .multiply(rate)
                .multiply(monthsDecimal)
                .divide(MONTHS_IN_YEAR, INTEREST_SCALE, BigDecimal.ROUND_HALF_UP);

        // Total cicilan untuk dibayar = Principal + Interest
        BigDecimal totalToPay = principal.add(totalInterest);

        // Ciciclan Bulanan = Total / bulan (rounded to nearest whole number)
        BigDecimal monthlyPaymentAmount = totalToPay
                .divide(new BigDecimal(numberOfMonths), PAYMENT_SCALE, BigDecimal.ROUND_HALF_UP);

        return new Money(monthlyPaymentAmount, loanAmount.getCurrency());
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