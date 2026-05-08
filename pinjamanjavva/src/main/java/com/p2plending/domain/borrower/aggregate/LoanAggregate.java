package com.p2plending.domain.borrower.aggregate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

/**
 * Manages loan creation, state transitions, and investment tracking
 */
public class LoanAggregate {

    private static final int MIN_CREDIT_SCORE = 600;
    private static final int MAX_CREDIT_SCORE = 1000;
    private static final int SALARY_MULTIPLIER = 3;

    private LoanApplication loan;
    private Borrower borrower;
    private List<Investment> investments;

    private LoanAggregate(LoanApplication loan, Borrower borrower) {
        this.loan = loan;
        this.borrower = borrower;
        this.investments = new ArrayList<>();
    }

    /**
     * Factory Method (Factory Pattern)
     * Validate borrower eligibility and create loan with PENDING status
     */
    public static LoanAggregate create(Borrower borrower, Money amount, Tenor tenor) {
        validateBorrowerEligibility(borrower);
        validateLoanAmount(borrower, amount);

        // Membuat LoanApplication dengan PENDING status
        LoanApplication loanApp = new LoanApplication(
                UUID.randomUUID().toString(),
                borrower.getId(),
                amount,
                tenor,
                borrower.getCreditScore());

        return new LoanAggregate(loanApp, borrower);
    }

    /**
     * validasi creditscore borrower
     * 
     * @throws IllegalArgumentException if credit score < 600
     */
    private static void validateBorrowerEligibility(Borrower borrower) {
        if (borrower.getCreditScore() < MIN_CREDIT_SCORE) {
            throw new IllegalArgumentException(
                    String.format("Borrower credit score %d is below minimum required %d",
                            borrower.getCreditScore(), MIN_CREDIT_SCORE));
        }
    }

    /**
     * Validasi limit pinjaman
     * 
     * @throws IllegalArgumentException if loan amount > 3x salary
     */
    private static void validateLoanAmount(Borrower borrower, Money amount) {
        Money maxLoan = new Money(
                borrower.getGaji().getAmount().multiply(new BigDecimal(SALARY_MULTIPLIER)),
                borrower.getGaji().getCurrency());

        if (amount.getAmount().compareTo(maxLoan.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    String.format("Loan amount IDR %s exceeds maximum limit IDR %s (3x salary)",
                            amount.getAmount(), maxLoan.getAmount()));
        }
    }

    /**
     * Get loan status (State Pattern - delegated to LoanStatus)
     */
    public LoanStatus getStatus() {
        return loan.getStatus();
    }

    public void addInvestment(Investment investment) {
        investments.add(investment);
        checkFundingComplete();
    }

    private void checkFundingComplete() {
        Money totalInvested = calculateTotalInvestment();
        if (totalInvested.isGreaterThanOrEqual(loan.getAmount())) {
            transitionTo(LoanStatus.FUNDED);
        }
    }

    private void transitionTo(LoanStatus nextStatus) {
        if (!loan.getStatus().canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                    "Cannot transition from " + loan.getStatus() + " to " + nextStatus);
        }
    }

    private Money calculateTotalInvestment() {
        Money total = new Money(BigDecimal.ZERO, "IDR");
        for (Investment inv : investments) {
            total = total.add(inv.getAmount());
        }
        return total;
    }

    // Getters
    public LoanApplication getLoan() {
        return loan;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(investments);
    }
}