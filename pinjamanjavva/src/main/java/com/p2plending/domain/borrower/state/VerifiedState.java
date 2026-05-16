package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.shared.LoanStatus;

/**
 * VERIFIED state: Credit score sudah verified dan disetujui, siap untuk funding round.
 * Transisi valid: → FUNDING (openFunding), → CANCELLED
 */
public class VerifiedState extends AbstractLoanState {

    @Override
    public void openFunding(LoanAggregate context) {
        context.transitionToState(new FundingState());
    }

    @Override
    public void cancel(LoanAggregate context) {
        context.transitionToState(new CancelledState());
    }

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.VERIFIED;
    }
}
