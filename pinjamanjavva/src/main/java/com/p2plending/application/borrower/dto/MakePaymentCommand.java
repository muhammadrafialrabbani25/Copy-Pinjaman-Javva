package com.p2plending.application.borrower.dto;

import com.p2plending.domain.shared.Money;
import java.math.BigDecimal;

public class MakePaymentCommand {
    private final String borrowerId;
    private final String paymentId;
    private final Money amount;

    public MakePaymentCommand(String borrowerId, String paymentId, Money amount) {
        if (borrowerId == null || borrowerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower ID tidak boleh kosong");
        }
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID tidak boleh kosong");
        }
        if (amount == null || amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount harus lebih dari 0");
        }

        this.borrowerId = borrowerId;
        this.paymentId = paymentId;
        this.amount = amount;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Money getAmount() {
        return amount;
    }
}
