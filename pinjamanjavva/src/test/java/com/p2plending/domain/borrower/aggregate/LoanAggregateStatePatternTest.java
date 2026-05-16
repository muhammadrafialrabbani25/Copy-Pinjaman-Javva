package com.p2plending.domain.borrower.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.borrower.state.CancelledState;
import com.p2plending.domain.borrower.state.DisbursedState;
import com.p2plending.domain.borrower.state.ExpiredFundingState;
import com.p2plending.domain.borrower.state.FundedState;
import com.p2plending.domain.borrower.state.FundingState;
import com.p2plending.domain.borrower.state.PendingState;
import com.p2plending.domain.borrower.state.VerifiedState;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

class LoanAggregateStatePatternTest {

    private LoanAggregate loanAggregate;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        // Setup borrower
        borrower = new Borrower(
                "BORR001",
                "Iman Santoso",
                "081234567890",
                "Jakarta Selatan",
                new KTP("Iman Santoso", "1234567890123456"),
                new Money(new BigDecimal("60000000"), "IDR"),
                "Software Engineer",
                800);

        // Create loan aggregate
        Money loanAmount = new Money(new BigDecimal("30000000"), "IDR");
        loanAggregate = LoanAggregate.create(borrower, loanAmount, Tenor.SIX_MONTHS);
    }

    // Initial State Tests

    @Test
    void testInitialState_ShouldBePending() {
        // Assert
        assertEquals(LoanStatus.PENDING, loanAggregate.getStatus());
        assertInstanceOf(PendingState.class, loanAggregate.getCurrentState());
    }

    // PENDING → VERIFIED Transition

    @Test
    void testVerifyTransition_PendingToVerified() {
        // Arrange
        assertEquals(LoanStatus.PENDING, loanAggregate.getStatus());

        // Act
        loanAggregate.verify();

        // Assert
        assertEquals(LoanStatus.VERIFIED, loanAggregate.getStatus());
        assertInstanceOf(VerifiedState.class, loanAggregate.getCurrentState());
    }

    @Test
    void testVerifyTransition_FromVerified_ShouldThrow() {
        // Arrange
        loanAggregate.verify();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            loanAggregate.verify();
        });
    }

    // PENDING → CANCELLED Transition

    @Test
    void testCancelTransition_PendingToCancelled() {
        // Arrange
        assertEquals(LoanStatus.PENDING, loanAggregate.getStatus());

        // Act
        loanAggregate.cancel();

        // Assert
        assertEquals(LoanStatus.CANCELLED, loanAggregate.getStatus());
        assertInstanceOf(CancelledState.class, loanAggregate.getCurrentState());
    }

    // VERIFIED → FUNDING Transition

    @Test
    void testOpenFundingTransition_VerifiedToFunding() {
        // Arrange
        loanAggregate.verify();
        assertEquals(LoanStatus.VERIFIED, loanAggregate.getStatus());

        // Act
        loanAggregate.openFunding();

        // Assert
        assertEquals(LoanStatus.FUNDING, loanAggregate.getStatus());
        assertInstanceOf(FundingState.class, loanAggregate.getCurrentState());
    }

    @Test
    void testOpenFundingTransition_FromPending_ShouldThrow() {
        // Arrange & Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            loanAggregate.openFunding();
        });
    }

    // VERIFIED → CANCELLED Transition

    @Test
    void testCancelTransition_VerifiedToCancelled() {
        // Arrange
        loanAggregate.verify();

        // Act
        loanAggregate.cancel();

        // Assert
        assertEquals(LoanStatus.CANCELLED, loanAggregate.getStatus());
    }

    // FUNDING → FUNDED Transition (via addInvestment)

    @Test
    void testFundingCompleteTransition_FundingToFunded_Min20Percent() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();
        assertEquals(LoanStatus.FUNDING, loanAggregate.getStatus());

        // Create lender and investment (20% = 6,000,000)
        Money investmentAmount = new Money(new BigDecimal("6000000"), "IDR");
        Investment investment = new Investment(
                "INV001",
                "LEND001",
                loanAggregate.getLoan().getId(),
                investmentAmount);

        // Act
        loanAggregate.addInvestment(investment);

        // Assert
        assertEquals(LoanStatus.FUNDED, loanAggregate.getStatus());
        assertInstanceOf(FundedState.class, loanAggregate.getCurrentState());
    }

    @Test
    void testFundingIncompleteTransition_StayInFunding() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();

        // Create investment < 20% (e.g., 10% = 3,000,000)
        Money investmentAmount = new Money(new BigDecimal("3000000"), "IDR");
        Investment investment = new Investment(
                "INV001",
                "LEND001",
                loanAggregate.getLoan().getId(),
                investmentAmount);

        // Act
        loanAggregate.addInvestment(investment);

        // Assert
        assertEquals(LoanStatus.FUNDING, loanAggregate.getStatus());
        assertInstanceOf(FundingState.class, loanAggregate.getCurrentState());
    }

    // FUNDING → CANCELLED Transition

    @Test
    void testCancelTransition_FundingToCancelled() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();

        // Act
        loanAggregate.cancel();

        // Assert
        assertEquals(LoanStatus.CANCELLED, loanAggregate.getStatus());
    }

    // FUNDING → EXPIRED_FUNDING Transition

    @Test
    void testExpireFundingTransition_FundingToExpiredFunding() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();
        assertEquals(LoanStatus.FUNDING, loanAggregate.getStatus());

        // Act
        loanAggregate.expireFunding();

        // Assert
        assertEquals(LoanStatus.EXPIRED_FUNDING, loanAggregate.getStatus());
        assertInstanceOf(ExpiredFundingState.class, loanAggregate.getCurrentState());
    }

    // EXPIRED_FUNDING → CANCELLED Transition

    @Test
    void testCancelTransition_ExpiredFundingToCancelled() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();
        loanAggregate.expireFunding();

        // Act
        loanAggregate.cancel();

        // Assert
        assertEquals(LoanStatus.CANCELLED, loanAggregate.getStatus());
    }

    // FUNDED → DISBURSED Transition

    @Test
    void testDisburseTransition_FundedToDisbursed() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();
        Money investmentAmount = new Money(new BigDecimal("6000000"), "IDR");
        Investment investment = new Investment(
                "INV001",
                "LEND001",
                loanAggregate.getLoan().getId(),
                investmentAmount);
        loanAggregate.addInvestment(investment);
        assertEquals(LoanStatus.FUNDED, loanAggregate.getStatus());

        // Act
        loanAggregate.disburse();

        // Assert
        assertEquals(LoanStatus.DISBURSED, loanAggregate.getStatus());
        assertInstanceOf(DisbursedState.class, loanAggregate.getCurrentState());
    }

    // FUNDED → CANCELLED Transition

    @Test
    void testCancelTransition_FundedToCancelled() {
        // Arrange
        loanAggregate.verify();
        loanAggregate.openFunding();
        Money investmentAmount = new Money(new BigDecimal("6000000"), "IDR");
        Investment investment = new Investment(
                "INV001",
                "LEND001",
                loanAggregate.getLoan().getId(),
                investmentAmount);
        loanAggregate.addInvestment(investment);

        // Act
        loanAggregate.cancel();

        // Assert
        assertEquals(LoanStatus.CANCELLED, loanAggregate.getStatus());
    }

    // Invalid Transitions

    @Test
    void testDisburse_FromPending_ShouldThrow() {
        assertThrows(UnsupportedOperationException.class, () -> {
            loanAggregate.disburse();
        });
    }

    @Test
    void testDisburse_FromFunding_ShouldThrow() {
        loanAggregate.verify();
        loanAggregate.openFunding();

        assertThrows(UnsupportedOperationException.class, () -> {
            loanAggregate.disburse();
        });
    }

    // Full Happy Path

    @Test
    void testFullHappyPath_PendingToDisburseToFinal() {
        // Arrange & Act
        assertEquals(LoanStatus.PENDING, loanAggregate.getStatus());

        loanAggregate.verify();
        assertEquals(LoanStatus.VERIFIED, loanAggregate.getStatus());

        loanAggregate.openFunding();
        assertEquals(LoanStatus.FUNDING, loanAggregate.getStatus());

        Money investmentAmount = new Money(new BigDecimal("6000000"), "IDR");
        Investment investment = new Investment(
                "INV001",
                "LEND001",
                loanAggregate.getLoan().getId(),
                investmentAmount);
        loanAggregate.addInvestment(investment);
        assertEquals(LoanStatus.FUNDED, loanAggregate.getStatus());

        loanAggregate.disburse();
        assertEquals(LoanStatus.DISBURSED, loanAggregate.getStatus());

        // Assert - No further transitions in MVP
        assertThrows(UnsupportedOperationException.class, () -> {
            loanAggregate.disburse();
        });
    }
}
