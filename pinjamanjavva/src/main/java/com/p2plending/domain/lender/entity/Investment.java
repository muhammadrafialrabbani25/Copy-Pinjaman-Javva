package com.p2plending.domain.lender.entity;
import com.p2plending.domain.shared.Money;

public class Investment {
    private final String id;
    private final String lenderId;
    private final String loanId;
    private final Money amount;
    private InvestmentStatus status;

    

    public Investment(String id, String lenderId, String loanId, Money amount) {
        this.id = id;
        this.lenderId = lenderId;
        this.loanId = loanId;

        if (amount == null){
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.amount = amount;
        status = InvestmentStatus.ACTIVE;
    }



    public enum InvestmentStatus {
        ACTIVE, CANCELLED
    }



    public String getId() {
        return id;
    }



    public String getLenderId() {
        return lenderId;
    }



    public String getLoanId() {
        return loanId;
    }



    public Money getAmount() {
        return amount;
    }



    public InvestmentStatus getStatus() {
        return status;
    }

    
}