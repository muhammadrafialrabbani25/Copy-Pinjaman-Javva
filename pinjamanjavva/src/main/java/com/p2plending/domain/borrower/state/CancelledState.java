package com.p2plending.domain.borrower.state;

import com.p2plending.domain.shared.LoanStatus;

/**
 * CANCELLED state: Pinjaman dibatalkan. Final state.
 * Transisi: Tidak ada (final)
 */
public class CancelledState extends AbstractLoanState {

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.CANCELLED;
    }
}
