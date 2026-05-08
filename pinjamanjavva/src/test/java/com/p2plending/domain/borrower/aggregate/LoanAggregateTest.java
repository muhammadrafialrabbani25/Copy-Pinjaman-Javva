package com.p2plending.domain.borrower.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

class LoanAggregateTest {

    @Test
    void testCreateLoanWithValidBorrower() {
        Borrower borrower = new Borrower(
            "B001", "Budi", "08123456789", "Bandung",
            new KTP("Budi", "1234567890123456"),
            new Money(new BigDecimal("10000000"), "IDR"),
            "Engineer", 750
        );
        Money amount = new Money(new BigDecimal("5000000"), "IDR");
        Tenor tenor = Tenor.TWELVE_MONTHS;
        
        // Act
        LoanAggregate loan = LoanAggregate.create(borrower, amount, tenor);
        
        // Assert
        assertNotNull(loan);
        assertEquals(LoanStatus.PENDING, loan.getStatus());
    }
    
    @Test
    void testCreateLoanExceedsSalaryLimit() {
        // Arrange - inline setup
        Borrower borrower = new Borrower(
            "B001", "Budi", "08123456789", "Bandung",
            new KTP("Budi", "1234567890123456"),
            new Money(new BigDecimal("10000000"), "IDR"),
            "Engineer", 750
        );
        Money excessiveAmount = new Money(new BigDecimal("35000000"), "IDR"); // > 3x salary (30M)
        Tenor tenor = Tenor.TWELVE_MONTHS;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            LoanAggregate.create(borrower, excessiveAmount, tenor);
        });
    }
}
