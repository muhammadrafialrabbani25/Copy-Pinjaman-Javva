package com.p2plending.domain.borrower.entity;

import com.p2plending.domain.borrower.entity.Payment.PaymentStatus;
import com.p2plending.domain.shared.Money;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private final String id;
    private final String loanId;
    private final int noBulan;
    private final Money amount;
    private final LocalDate dueDate;
    private PaymentStatus status;

    

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
        status = PaymentStatus.PENDING;
    }



    public enum PaymentStatus {
        PENDING, PAID
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
    
    
}