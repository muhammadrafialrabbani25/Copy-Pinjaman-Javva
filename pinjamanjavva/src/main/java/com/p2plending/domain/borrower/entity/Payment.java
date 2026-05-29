package com.p2plending.domain.borrower.entity;

import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Payment {

    private final String id;
    private final String loanId;
    private final int noBulan;
    private final Money amount;
    private final LocalDate dueDate;

    private PaymentStatus status;

    private LocalDate paidDate;
    private BigDecimal denda = BigDecimal.ZERO;

    public Payment(String id, String loanId, int noBulan, Money amount, LocalDate dueDate) {
        this.id = id;
        this.loanId = loanId;
        this.noBulan = noBulan;

        if (amount == null) {
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.amount = amount;

        if (dueDate == null) {
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.dueDate = dueDate;

        this.status = PaymentStatus.PENDING;
    }

    public boolean isOverdue() {
        return status == PaymentStatus.PENDING &&
                LocalDate.now().isAfter(dueDate);
    }

    public void calculateDenda() {
        if (isOverdue()) {
            long telatHari = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

            if (telatHari > 30) {
                this.denda = amount.getAmount().multiply(new BigDecimal("0.01"));
            } else {
                this.denda = BigDecimal.ZERO;
            }

            this.status = PaymentStatus.OVERDUE;
        }
    }

    public void pay() {
        this.status = PaymentStatus.PAID;
        this.paidDate = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getLoanId() {
        return loanId;
    }

    public int getNoBulan() {
        return noBulan;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public BigDecimal getDenda() {
        return denda;
    }
}