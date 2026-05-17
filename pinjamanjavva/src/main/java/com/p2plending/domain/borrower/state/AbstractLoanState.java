package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.lender.entity.Investment;

/**
 * Base abstract class untuk concrete loan states.
 * Menyediakan default behavior yang throw exception untuk methods yang tidak valid di state tersebut.
 */
public abstract class AbstractLoanState implements LoanState {

    @Override
    public void verify(LoanAggregate context) {
        throw new UnsupportedOperationException(
                "Cannot verify loan in " + getStatus() + " state");
    }

    @Override
    public void openFunding(LoanAggregate context) {
        throw new UnsupportedOperationException(
                "Cannot open funding in " + getStatus() + " state");
    }

    @Override
    public void addInvestment(LoanAggregate context, Investment investment) {
        throw new UnsupportedOperationException(
                "Cannot add investment in " + getStatus() + " state");
    }

    @Override
    public void checkFundingComplete(LoanAggregate context) {
        throw new UnsupportedOperationException(
                "Cannot check funding completion in " + getStatus() + " state");
    }

    @Override
    public void disburse(LoanAggregate context) {
        throw new UnsupportedOperationException(
                "Cannot disburse loan in " + getStatus() + " state");
    }

    @Override
    public void cancel(LoanAggregate context) {
        throw new UnsupportedOperationException(
                "Cannot cancel loan in " + getStatus() + " state");
    }

    @Override
    public void expireFunding(LoanAggregate context) {
        throw new UnsupportedOperationException(
                "Cannot expire funding in " + getStatus() + " state");
    }
}
