package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.shared.LoanStatus;

/**
 * EXPIRED_FUNDING state: Deadline 5 hari untuk funding telah terlewat, funding tidak berhasil.
 * Transisi valid: → CANCELLED
 */
public class ExpiredFundingState extends AbstractLoanState {

    @Override
    public void cancel(LoanAggregate context) {
        context.transitionToState(new CancelledState());
    }

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.EXPIRED_FUNDING;
    }
}
