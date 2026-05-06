package com.p2plending.domain.shared;
import java.math.BigDecimal;

public class Money {
    BigDecimal amount;
    String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getCurrency() {
        return currency;
    }

    

}