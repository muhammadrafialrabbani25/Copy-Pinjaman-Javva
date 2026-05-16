package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.LoanStatus;

/**
 * FUNDING state: Funding round sedang berjalan, menunggu min 20% investment tercapai dalam 5 hari.
 * Transisi valid: → FUNDED (checkFundingComplete), → EXPIRED_FUNDING
 * (expireFunding), → CANCELLED
 */
public class FundingState extends AbstractLoanState {

    @Override
    public void addInvestment(LoanAggregate context, Investment investment) {
        context.addInvestmentInternal(investment);
        context.checkFundingComplete();
    }

    @Override
    public void checkFundingComplete(LoanAggregate context) {
        if (context.isFundingComplete()) {
            context.transitionToState(new FundedState());
        }
    }

    @Override
    public void expireFunding(LoanAggregate context) {
        context.transitionToState(new ExpiredFundingState());
    }

    @Override
    public void cancel(LoanAggregate context) {
        context.transitionToState(new CancelledState());
    }

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.FUNDING;
    }
}
