package com.p2plending.application.borrower.dto;

import com.p2plending.domain.borrower.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentDTO {
    private final String id;
    private final String loanId;
    private final int noBulan;
    private final BigDecimal amount;
    private final String currency;
    private final LocalDate dueDate;
    private final String status;
    private final LocalDate paidDate;
    private final BigDecimal denda;

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.loanId = payment.getLoanId();
        this.noBulan = payment.getNoBulan();
        this.amount = payment.getAmount().getAmount();
        this.currency = payment.getAmount().getCurrency();
        this.dueDate = payment.getDueDate();
        this.status = payment.getStatus().name();
        this.paidDate = payment.getPaidDate();
        this.denda = payment.getDenda();
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

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public BigDecimal getDenda() {
        return denda;
    }
}
