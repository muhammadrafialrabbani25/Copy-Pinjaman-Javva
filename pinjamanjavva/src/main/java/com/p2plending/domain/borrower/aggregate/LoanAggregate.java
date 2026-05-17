package com.p2plending.domain.borrower.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.borrower.state.CancelledState;
import com.p2plending.domain.borrower.state.LoanState;
import com.p2plending.domain.borrower.state.PendingState;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;

/**
 * LoanAggregate: Context untuk State Pattern dalam mengelola lifecycle
 * pinjaman.
 * Mengelola state transitions melalui LoanState interface.
 */
public class LoanAggregate {

    private static final int MIN_CREDIT_SCORE = 600;
    private static final int SALARY_MULTIPLIER = 3;

    private LoanApplication loan;
    private Borrower borrower;
    private List<Investment> investments;
    private LoanState currentState;

    private LoanAggregate(LoanApplication loan, Borrower borrower) {
        this.loan = loan;
        this.borrower = borrower;
        this.investments = new ArrayList<>();
        this.currentState = new PendingState();
    }

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
     * Reconstruct an existing aggregate from repository
     */
    public static LoanAggregate load(LoanApplication loan, Borrower borrower) {
        LoanAggregate aggregate = new LoanAggregate(loan, borrower);
        // Determine state from existing loan status
        switch (loan.getStatus()) {
            case VERIFIED: aggregate.currentState = new com.p2plending.domain.borrower.state.VerifiedState(); break;
            case FUNDING: aggregate.currentState = new com.p2plending.domain.borrower.state.FundingState(); break;
            case FUNDED: aggregate.currentState = new com.p2plending.domain.borrower.state.FundedState(); break;
            case DISBURSED: aggregate.currentState = new com.p2plending.domain.borrower.state.DisbursedState(); break;
            case CANCELLED: aggregate.currentState = new com.p2plending.domain.borrower.state.CancelledState(); break;
            case EXPIRED_FUNDING: aggregate.currentState = new com.p2plending.domain.borrower.state.ExpiredFundingState(); break;
            default: aggregate.currentState = new PendingState(); break;
        }
        return aggregate;
    }

    /**
     * Validasi credit score borrower
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
     * Get current status melalui state
     */
    public LoanStatus getStatus() {
        return currentState.getStatus();
    }

    /**
     * Get current state (untuk testing/debugging)
     */
    public LoanState getCurrentState() {
        return currentState;
    }

    /**
     * Delegate verify ke current state
     */
    public void verify() {
        currentState.verify(this);
    }

    /**
     * Delegate openFunding ke current state
     */
    public void openFunding() {
        currentState.openFunding(this);
    }

    /**
     * Delegate addInvestment ke current state
     */
    public void addInvestment(Investment investment) {
        currentState.addInvestment(this, investment);
    }

    /**
     * Check funding completion dan transition jika diperlukan
     */
    public void checkFundingComplete() {
        currentState.checkFundingComplete(this);
    }

    /**
     * Delegate disburse ke current state
     */
    public void disburse() {
        currentState.disburse(this);
    }

    /**
     * Delegate cancel ke current state
     */
    public void cancel() {
        currentState.cancel(this);
    }

    /**
     * Delegate expireFunding ke current state
     */
    public void expireFunding() {
        currentState.expireFunding(this);
    }

    /**
     * Cancel loan dengan check 20% investment threshold untuk penalty.
     * - Jika investment < 20%: no penalty
     * - Jika investment >= 20%: apply penalty (counter+1, potentially block)
     * Transition ke CANCELLED state.
     */
    public void cancelWithPenaltyCheck() {
        LoanCancellationService cancellationService = new LoanCancellationService();
        Money totalInvested = calculateTotalInvestment();

        // Check apakah ada penalty (investment >= 20%)
        boolean hasPenalty = cancellationService.shouldApplyPenalty(loan.getAmount(), totalInvested);

        if (hasPenalty) {
            // Increment cancellation count
            int newCount = cancellationService.incrementCancellationCount(borrower.getCancellationCount());
            borrower.setCancellationCount(newCount);

            // Check apakah sudah 3x untuk set block
            if (newCount >= 3) {
                LocalDate blockUntil = cancellationService.calculateBlockUntilDate(LocalDate.now(), newCount);
                borrower.setLastBlockedDate(blockUntil.atStartOfDay());
            }
        }

        // Set cancellation date
        loan.setCancelledDate(LocalDate.now().atStartOfDay());

        // Transition ke CANCELLED state
        transitionToState(new CancelledState());
    }

    /**
     * Internal method untuk transition ke state baru
     */
    public void transitionToState(LoanState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("New state must not be null");
        }
        this.currentState = newState;
        this.loan.updateStatus(newState.getStatus());
    }

    /**
     * Internal method untuk menambah investment (dipanggil oleh FundingState)
     */
    public void addInvestmentInternal(Investment investment) {
        if (investment == null) {
            throw new IllegalArgumentException("Investment must not be null");
        }
        investments.add(investment);
    }

    /**
     * Check apakah funding sudah mencapai target (20% dari loan amount)
     */
    public boolean isFundingComplete() {
        Money totalInvested = calculateTotalInvestment();
        Money minRequired = loan.getAmount().multiply(new BigDecimal("0.20"));
        return totalInvested.isGreaterThanOrEqual(minRequired);
    }

    /**
     * Calculate total investment dari semua lenders
     */
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