package com.p2plending.domain.borrower.service;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplicationTest;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.Money;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain Service: Loan Cancellation Logic
 * 
 * Responsibilities:
 * 1. Validate cancellation eligibility (state, 20% invested minimum)
 * 2. Refund logic: return full investment amount to all lenders
 * 3. Increment borrower cancellation counter
 * 4. Check block period (after 3x cancel)
 * 5. Update loan status to CANCELLED
 */
public class LoanCancellationService {

    // TODO: Inject InvestmentRepository, LenderRepository when infrastructure ready
    // private InvestmentRepository investmentRepository;
    // private LenderRepository lenderRepository;

    /**
     * Cancel a loan if it meets cancellation criteria
     * 
     * Business Rules:
     * - Only cancellable states: PENDING, VERIFIED, FUNDING (NOT FUNDED/DISBURSED)
     * - Only counted if investment >= 20% of loan amount
     * - After 3x cancel (with 20%+ invested): borrower blocked 4 months from new
     * apply
     * - Refund: return full investment amount to all lenders
     * - Admin fee (2%) already taken at top up, NOT refunded
     * 
     * @param loan     the loan to cancel
     * @param borrower the borrower cancelling
     * @param reason   cancellation reason (optional)
     * @return true if cancellation successful, false if not eligible
     */
    public boolean cancelLoan(LoanApplicationTest loan, Borrower borrower, String reason) {
        // TODO: Implement
        // 1. Validate loan state: PENDING, VERIFIED, FUNDING only
        // if (loan.getStatus() == FUNDED || loan.getStatus() == DISBURSED)
        // return false;

        // 2. Check if 20% invested (only then increment counter)
        // if (loan.isMinInvestedPercentageReached() && loan.getStatus() == FUNDING) {
        // // This cancellation COUNTS
        // }

        // 3. Validate borrower not in block period
        // if (borrower.isInBlockPeriod()) return false;

        // 4. Get all investments for this loan
        // List<Investment> investments =
        // investmentRepository.findByLoanId(loan.getId());

        // 5. Refund each investment
        // for (Investment inv : investments) {
        // refundInvestment(inv);
        // }

        // 6. Increment cancellation counter if 20%+ invested
        // if (loan.isMinInvestedPercentageReached()) {
        // borrower.incrementCancellationCount();
        // if (borrower.getCancellationCount() >= 3) {
        // borrower.setBlockedUntil(now + 4 months);
        // }
        // }

        // 7. Update loan status
        // loan.setCancelledDate(now);
        // loan.setStatus(CANCELLED);

        // 8. Publish LoanCancelledEvent

        return true;
    }

    /**
     * Check if borrower is currently in block period (after 3x cancellations)
     * 
     * @param borrower the borrower to check
     * @return true if blocked, false if can apply
     */
    public boolean isBlockedFromApplying(Borrower borrower) {
        // TODO: Implement
        // LocalDateTime lastBlocked = borrower.getLastBlockedDate();
        // if (lastBlocked == null) return false;

        // LocalDateTime blockExpiry = lastBlocked.plusMonths(4);
        // return LocalDateTime.now().isBefore(blockExpiry);

        return false;
    }

    /**
     * Refund a single investment to lender
     * 
     * Logic:
     * - Get investment amount
     * - Add back to lender saldo
     * - Update investment status to CANCELLED
     * - No need to refund admin fee (already taken at top up)
     * 
     * @param investment the investment to refund
     */
    public void refundInvestment(Investment investment) {
        // TODO: Implement
        // 1. Get lender
        // Lender lender = lenderRepository.findById(investment.getLenderId());

        // 2. Add investment amount back to saldo
        // Money newSaldo = lender.getSaldo().add(investment.getAmount());
        // lender.setSaldo(newSaldo);

        // 3. Update investment status
        // investment.setStatus(CANCELLED);

        // 4. Save lender (saldo updated)
        // lenderRepository.save(lender);
    }

    /**
     * Calculate remaining block period for borrower
     * 
     * @param borrower the borrower
     * @return days remaining, or 0 if not blocked
     */
    public long getRemainingBlockDays(Borrower borrower) {
        // TODO: Implement
        // LocalDateTime lastBlocked = borrower.getLastBlockedDate();
        // if (lastBlocked == null) return 0;

        // LocalDateTime blockExpiry = lastBlocked.plusMonths(4);
        // LocalDateTime now = LocalDateTime.now();

        // if (now.isAfter(blockExpiry)) return 0;

        // return ChronoUnit.DAYS.between(now, blockExpiry);

        return 0;
    }

    /**
     * Get total refund amount for a loan
     * 
     * @param loanId the loan ID
     * @return total amount to refund to all lenders
     */
    public Money calculateTotalRefundAmount(Long loanId) {
        // TODO: Implement
        // List<Investment> investments = investmentRepository.findByLoanId(loanId);
        // Money total = Money.of(0);
        // for (Investment inv : investments) {
        // total = total.add(inv.getAmount());
        // }
        // return total;

        return null;
    }

    // Helper methods (implement as needed)
    // private void publishLoanCancelledEvent(LoanApplication loan, Borrower
    // borrower, Money totalRefunded, List<String> affectedLenders)
    // private List<String> getAffectedLenderIds(Long loanId)
}
