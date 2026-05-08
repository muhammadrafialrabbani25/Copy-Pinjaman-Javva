package com.p2plending.domain.borrower.entity;

import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplication {
    private final String id;
    private final String borrowerId;
    private final Money amount;
    private final Tenor tenor;
    private final int creditScore;
    private LoanStatus status;
    private final LocalDateTime createdDate;
    private boolean minInvestedPercentageReached;
    private LocalDateTime cancelledDate;
    
    public LoanApplication(String id, String borrowerId, Money amount, Tenor tenor, int creditScore) {
        this.id = id;
        this.borrowerId = borrowerId;

        if (amount == null){
            throw new IllegalArgumentException("masukan data valid");
        }
        this.amount = amount;

        if (tenor == null){
            throw new IllegalArgumentException("masukan data valid");
        }
        this.tenor = tenor;
        this.creditScore = creditScore;
        status = LoanStatus.PENDING;
        createdDate = LocalDateTime.now();
        minInvestedPercentageReached = false;
        cancelledDate = null;

    }

    public String getId() {
        return id;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public Money getAmount() {
        return amount;
    }

    public Tenor getTenor() {
        return tenor;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public boolean isMinInvestedPercentageReached() {
        return minInvestedPercentageReached;
    }

    public LocalDateTime getCancelledDate() {
        return cancelledDate;
    }

}