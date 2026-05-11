package com.p2plending.application.borrower.dto;

import com.p2plending.domain.shared.Money;

/**
 * Command DTO: Cancel Loan Request
 * 
 * Input from borrower to cancel an active loan
 */
public class CancelLoanCommand {
    private final String borrowerId;
    private final String loanId;
    private final Money fundedAmount; // Total amount funded by investors for this loan

    public CancelLoanCommand(String borrowerId, String loanId, Money fundedAmount) {
        this.borrowerId = borrowerId;
        this.loanId = loanId;
        this.fundedAmount = fundedAmount;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public String getLoanId() {
        return loanId;
    }
    public Money getFundedAmount() {
        return fundedAmount;
    }
}