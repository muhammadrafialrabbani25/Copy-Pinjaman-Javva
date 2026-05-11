package com.p2plending.application.lender.dto;

import java.math.BigDecimal;

public class InvestCommand {
    private final String lenderId;
    private final String loanId;
    private final BigDecimal amount;

    public InvestCommand(String lenderId, String loanId, BigDecimal amount) {
        this.lenderId = lenderId;
        this.loanId = loanId;
        this.amount = amount;
    }

    public String getLenderId() {
        return lenderId;
    }

    public String getLoanId() {
        return loanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}