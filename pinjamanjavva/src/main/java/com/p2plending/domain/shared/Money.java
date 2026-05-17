package com.p2plending.domain.shared;

import java.math.BigDecimal;

public class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("uang anda harus lebih dari 0");
        }
        this.amount = amount;

        if (currency == null) {
            throw new IllegalArgumentException("mata uang anda harus rupiah");
        }
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.getAmount()), this.currency);
    }

    public Money subtract(Money other) {
        BigDecimal result = this.amount.subtract(other.getAmount());

        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("...");
        }

        return new Money(result, this.currency);
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.getAmount()) >= 0;
    }

    public Money multiply(BigDecimal multiplier) {
        if (multiplier == null) {
            throw new IllegalArgumentException("Multiplier must not be null");
        }
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Money other = (Money) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        return true;
    }

}