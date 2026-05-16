package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.shared.LoanStatus;

/**
 * FUNDED state: Funding sudah selesai (min 20% tercapai), menunggu pencairan dana.
 * Transisi valid: → DISBURSED (disburse), → CANCELLED
 */
public class FundedState extends AbstractLoanState {

    @Override
    public void disburse(LoanAggregate context) {
        context.transitionToState(new DisbursedState());
    }

    @Override
    public void cancel(LoanAggregate context) {
        context.transitionToState(new CancelledState());
    }

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.FUNDED;
    }
}
