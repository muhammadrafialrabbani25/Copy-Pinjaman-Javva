package com.p2plending.domain.borrower.entity;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class LoanApplicationTest {
    @Test // test 1
    void shouldCreateLoanApplicationWithValidData () {
        String id = "P001";
        String borrowerId = "B001";
        Money amount = new Money(new BigDecimal("30000000"), "IDR");
        Tenor tenor = Tenor.TWELVE_MONTHS;
        int creditScore = 750;

        LoanApplication loan = new LoanApplication(id, borrowerId, amount, tenor, creditScore);

        assertEquals(id, loan.getId());
        assertEquals(borrowerId, loan.getBorrowerId());
        assertEquals(amount, loan.getAmount());
        assertEquals(tenor, loan.getTenor());
        assertEquals(creditScore, loan.getCreditScore());
    
    }

    @Test // test 2
    void shouldHavePendingStatusWhenCreated() {
        String id = "P001";
        String borrowerId = "B001";
        Money amount = new Money(new BigDecimal("30000000"), "IDR");
        Tenor tenor = Tenor.TWELVE_MONTHS;
        int creditScore = 750;

        LoanApplication loan = new LoanApplication(id, borrowerId, amount, tenor, creditScore);

        assertEquals(id, loan.getId());
        assertEquals(borrowerId, loan.getBorrowerId());
        assertEquals(amount, loan.getAmount());
        assertEquals(tenor, loan.getTenor());
        assertEquals(creditScore, loan.getCreditScore());
        assertEquals(LoanStatus.PENDING, loan.getStatus());
    }

    @Test // test 3
    void shouldHaveFalseMinInvestedPercentageWhenCreated() {
        String id = "P001";
        String borrowerId = "B001";
        Money amount = new Money(new BigDecimal("30000000"), "IDR");
        Tenor tenor = Tenor.TWELVE_MONTHS;
        int creditScore = 750;

        LoanApplication loan = new LoanApplication(id, borrowerId, amount, tenor, creditScore);

        assertEquals(id, loan.getId());
        assertEquals(borrowerId, loan.getBorrowerId());
        assertEquals(amount, loan.getAmount());
        assertEquals(tenor, loan.getTenor());
        assertEquals(creditScore, loan.getCreditScore());
        assertFalse(loan.isMinInvestedPercentageReached());
    }

    @Test // test 4
    void shouldHaveNullCancelledDateWhenCreated() {
        String id = "P001";
        String borrowerId = "B001";
        Money amount = new Money(new BigDecimal("30000000"), "IDR");
        Tenor tenor = Tenor.TWELVE_MONTHS;
        int creditScore = 750;

        LoanApplication loan = new LoanApplication(id, borrowerId, amount, tenor, creditScore);

        assertEquals(id, loan.getId());
        assertEquals(borrowerId, loan.getBorrowerId());
        assertEquals(amount, loan.getAmount());
        assertEquals(tenor, loan.getTenor());
        assertEquals(creditScore, loan.getCreditScore());
        assertNull(loan.getCancelledDate());
    }

    @Test // test 5
    void shouldThrowExceptionWhenAmountIsNull() {
        String id = "P001";
        String borrowerId = "B001";
        Money amount = null;
        Tenor tenor = Tenor.TWELVE_MONTHS;
        int creditScore = 750;

        assertThrows(IllegalArgumentException.class, () -> {
        LoanApplication loan = new LoanApplication(id, borrowerId, amount, tenor, creditScore);
        });
    }

    @Test // test 6
    void shouldThrowExceptionWhenTenorIsNull() {
        String id = "P001";
        String borrowerId = "B001";
        Money amount = new Money(new BigDecimal("30000000"), "IDR");
        Tenor tenor  = null;
        int creditScore = 750;

        assertThrows(IllegalArgumentException.class, () -> {
            LoanApplication loan = new LoanApplication(id, borrowerId, amount, tenor, creditScore);
        });
    }

}
