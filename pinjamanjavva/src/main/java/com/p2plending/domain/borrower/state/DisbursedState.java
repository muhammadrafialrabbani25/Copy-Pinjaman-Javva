package com.p2plending.domain.borrower.state;

import com.p2plending.domain.shared.LoanStatus;

/**
 * DISBURSED state: Dana sudah dicairkan ke borrower, pinjaman aktif.
 * Transisi: Tidak ada (dalam scope MVP)
 */
public class DisbursedState extends AbstractLoanState {

    @Override
    public LoanStatus getStatus() {
        return LoanStatus.DISBURSED;
    }
}
