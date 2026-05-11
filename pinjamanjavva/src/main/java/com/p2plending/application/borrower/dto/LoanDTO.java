package com.p2plending.application.borrower.dto;

public class LoanDTO {
    private final String id;
    private final String borrowerId;
    private final long amount;
    private final int termInMonths;
    private final int cancellationCount;

    public LoanDTO(String id, String borrowerId, long amount, int termInMonths) {
        this(id, borrowerId, amount, termInMonths, 0);
    }

    public LoanDTO(String id, String borrowerId, long amount, int termInMonths, int cancellationCount) {
        this.id = id;
        this.borrowerId = borrowerId;
        this.amount = amount;
        this.termInMonths = termInMonths;
        this.cancellationCount = cancellationCount;
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

    public int getCancellationCount() {
        return cancellationCount;
    }
}