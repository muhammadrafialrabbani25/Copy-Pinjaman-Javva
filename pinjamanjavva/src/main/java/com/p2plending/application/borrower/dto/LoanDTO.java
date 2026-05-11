package com.p2plending.application.borrower.dto;

public class LoanDTO {
    private final String id;
    private final String borrowerId;
    private final long amount;
    private final int termInMonths;

    public LoanDTO(String id, String borrowerId, long amount, int termInMonths) {
        this.id = id;
        this.borrowerId = borrowerId;
        this.amount = amount;
        this.termInMonths = termInMonths;
    }

    public String getId() {
        return id;
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