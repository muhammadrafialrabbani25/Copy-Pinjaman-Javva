package com.p2plending.application.borrower.dto;

public class ApplyLoanCommand {
    private final String borrowerId;
    private final long amount;
    private final int termInMonths;

    public ApplyLoanCommand(String borrowerId, long amount, int termInMonths) {
        this.borrowerId = borrowerId;
        this.amount = amount;
        this.termInMonths = termInMonths;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public long getAmount() {
        return amount;
    }

    public int getTermInMonths() {
        return termInMonths;
    }
}