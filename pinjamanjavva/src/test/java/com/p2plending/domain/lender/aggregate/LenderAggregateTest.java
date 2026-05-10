package com.p2plending.domain.lender.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.shared.Money;

class LenderAggregateTest {

    @Test
    void testCreateLenderAggregate_Valid() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        Money initialSaldo = new Money(new BigDecimal("50000000"), "IDR");

        // Act 
        LenderAggregate aggregate = LenderAggregate.create(lender);

        // Assert 
        assertNotNull(aggregate);
        assertEquals(lender.getId(), aggregate.getLender().getId());
        assertEquals(initialSaldo.getAmount(), aggregate.getTotalSaldo().getAmount());
    }

    @Test
    void testCreateLenderAggregate_NullLender() {
        // Arrange 
        Lender lender = null;

        // Act & Assert
        try {
            LenderAggregate.create(lender);
            assertTrue(false, "Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Lender tidak boleh null"));
        }
    }

    @Test
    void testAddInvestment_Valid() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);
        Investment investment = new Investment(
                "INV001", "L001", "LOAN001",
                new Money(new BigDecimal("10000000"), "IDR"));

        // Act 
        aggregate.addInvestment(investment);

        // Assert 
        assertEquals(1, aggregate.getInvestments().size());
        assertEquals(investment.getId(), aggregate.getInvestments().get(0).getId());
    }

    @Test
    void testAddInvestment_Multiple() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("100000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        Investment investment1 = new Investment(
                "INV001", "L001", "LOAN001",
                new Money(new BigDecimal("10000000"), "IDR"));
        Investment investment2 = new Investment(
                "INV002", "L001", "LOAN002",
                new Money(new BigDecimal("15000000"), "IDR"));

        // Act 
        aggregate.addInvestment(investment1);
        aggregate.addInvestment(investment2);

        // Assert 
        assertEquals(2, aggregate.getInvestments().size());
        assertEquals(new BigDecimal("25000000"), aggregate.getTotalInvested().getAmount());
    }

    @Test
    void testAddInvestment_NullInvestment() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        // Act & Assert
        try {
            aggregate.addInvestment(null);
            assertTrue(false, "Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Investment tidak boleh null"));
        }
    }

    @Test
    void testCalculateTotalInvested_Empty() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        // Act 
        Money totalInvested = aggregate.getTotalInvested();

        // Assert 
        assertEquals(new BigDecimal("0"), totalInvested.getAmount());
    }

    @Test
    void testCalculateTotalInvested_ThreeInvestments() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("100000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        aggregate.addInvestment(new Investment("INV001", "L001", "LOAN001",
                new Money(new BigDecimal("5000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV002", "L001", "LOAN002",
                new Money(new BigDecimal("10000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV003", "L001", "LOAN003",
                new Money(new BigDecimal("8000000"), "IDR")));

        // Act 
        Money totalInvested = aggregate.getTotalInvested();

        // Assert 
        assertEquals(new BigDecimal("23000000"), totalInvested.getAmount());
    }

    @Test
    void testGetAvailableSaldo_InitialState() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        // Act 
        Money availableSaldo = aggregate.getAvailableSaldo();

        // Assert 
        assertEquals(new BigDecimal("50000000"), availableSaldo.getAmount());
    }

    @Test
    void testGetAvailableSaldo_AfterInvestment() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);
        aggregate.addInvestment(new Investment("INV001", "L001", "LOAN001",
                new Money(new BigDecimal("15000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV002", "L001", "LOAN002",
                new Money(new BigDecimal("10000000"), "IDR")));

        // Act 
        Money availableSaldo = aggregate.getAvailableSaldo();

        // Assert 
        assertEquals(new BigDecimal("25000000"), availableSaldo.getAmount());
    }


    @Test
    void testGetInvestmentsByLoanId_Found() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("100000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        aggregate.addInvestment(new Investment("INV001", "L001", "LOAN001",
                new Money(new BigDecimal("5000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV002", "L001", "LOAN001",
                new Money(new BigDecimal("10000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV003", "L001", "LOAN002",
                new Money(new BigDecimal("8000000"), "IDR")));

        // Act 
        List<Investment> investmentsForLoan1 = aggregate.getInvestmentsByLoanId("LOAN001");

        // Assert 
        assertEquals(2, investmentsForLoan1.size());
        assertTrue(investmentsForLoan1.stream()
                .allMatch(inv -> inv.getLoanId().equals("LOAN001")));
    }

    @Test
    void testGetInvestmentsByLoanId_NotFound() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        // Act 
        List<Investment> investmentsForLoan = aggregate.getInvestmentsByLoanId("LOAN_NOT_EXIST");

        // Assert 
        assertEquals(0, investmentsForLoan.size());
    }

    @Test
    void testHasSufficientSaldo_True() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);
        Money investmentAmount = new Money(new BigDecimal("20000000"), "IDR");

        // Act 
        boolean hasSufficientSaldo = aggregate.hasSufficientSaldo(investmentAmount);

        // Assert 
        assertTrue(hasSufficientSaldo);
    }

    @Test
    void testHasSufficientSaldo_False() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);
        aggregate.addInvestment(new Investment("INV001", "L001", "LOAN001",
                new Money(new BigDecimal("40000000"), "IDR")));

        Money investmentAmount = new Money(new BigDecimal("20000000"), "IDR");

        // Act 
        boolean hasSufficientSaldo = aggregate.hasSufficientSaldo(investmentAmount);

        // Assert 
        assertFalse(hasSufficientSaldo);
    }

    @Test
    void testHasSufficientSaldo_ExactlyEnough() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("50000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);
        Money investmentAmount = new Money(new BigDecimal("50000000"), "IDR");

        // Act 
        boolean hasSufficientSaldo = aggregate.hasSufficientSaldo(investmentAmount);

        // Assert 
        assertTrue(hasSufficientSaldo);
    }

    // Test Active Investments Count

    @Test
    void testGetActiveInvestmentsCount() {
        // Arrange 
        Lender lender = new Lender(
                "L001", "Budi", "08123456789", "Bandung",
                new KTP("Budi", "1234567890123456"), "Entrepreneur",
                new Money(new BigDecimal("100000000"), "IDR"));
        LenderAggregate aggregate = LenderAggregate.create(lender);

        aggregate.addInvestment(new Investment("INV001", "L001", "LOAN001",
                new Money(new BigDecimal("5000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV002", "L001", "LOAN002",
                new Money(new BigDecimal("10000000"), "IDR")));
        aggregate.addInvestment(new Investment("INV003", "L001", "LOAN003",
                new Money(new BigDecimal("8000000"), "IDR")));

        // Act 
        long activeCount = aggregate.getActiveInvestmentsCount();

        // Assert 
        assertEquals(3, activeCount);
    }
}
