package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.shared.LoanStatus;

/**
 * PENDING state: Pinjaman baru dibuat, belum melalui verifikasi credit score.
 * Transisi valid: → VERIFIED (approve), → CANCELLED
 */
public class PendingState extends AbstractLoanState {

    @Override
    public void verify(LoanAggregate context) {
        context.transitionToState(new VerifiedState());
    }

    @Override
    public void cancel(LoanAggregate context) {
        context.transitionToState(new CancelledState());
    }

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.PENDING;
    }
}
