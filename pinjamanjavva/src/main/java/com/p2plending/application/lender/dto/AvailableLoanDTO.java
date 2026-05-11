package com.p2plending.application.lender.dto;

import com.p2plending.domain.shared.LoanStatus;
import java.math.BigDecimal;

public class AvailableLoanDTO {
    private final String loanId;
    private final String borrowerId;
    private final BigDecimal amount;
    private final int tenorMonths;
    private final LoanStatus status;
    private final BigDecimal minimumInvestmentAmount;

    public AvailableLoanDTO(String loanId, String borrowerId, BigDecimal amount, 
                           int tenorMonths, LoanStatus status, BigDecimal minimumInvestmentAmount) {
        this.loanId = loanId;
        this.borrowerId = borrowerId;
        this.amount = amount;
        this.tenorMonths = tenorMonths;
        this.status = status;
        this.minimumInvestmentAmount = minimumInvestmentAmount;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getTenorMonths() {
        return tenorMonths;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public BigDecimal getMinimumInvestmentAmount() {
        return minimumInvestmentAmount;
    }
}