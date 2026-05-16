package com.p2plending.domain.borrower.state;

import com.p2plending.domain.borrower.aggregate.LoanAggregate;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.shared.LoanStatus;

/**
 * Contract interface untuk State Pattern dalam mengelola lifecycle pinjaman.
 * Setiap konkret state merepresentasikan behavior dari loan pada state tersebut.
 */
public interface LoanState {

    /**
     * Verifikasi pinjaman dari PENDING → VERIFIED
     */
    void verify(LoanAggregate context);

    /**
     * Buka funding round dari VERIFIED → FUNDING
     */
    void openFunding(LoanAggregate context);

    /**
     * Tambah investment ke loan (di state FUNDING)
     */
    void addInvestment(LoanAggregate context, Investment investment);

    /**
     * Check apakah funding selesai dan transition FUNDING → FUNDED jika ya
     */
    void checkFundingComplete(LoanAggregate context);

    /**
     * Cairkan dana dari FUNDED → DISBURSED
     */
    void disburse(LoanAggregate context);

    /**
     * Batalkan pinjaman ke CANCELLED (dari berbagai state)
     */
    void cancel(LoanAggregate context);

    /**
     * Expire funding deadline dari FUNDING → EXPIRED_FUNDING
     */
    void expireFunding(LoanAggregate context);

    /**
     * Get status enum dari state ini
     */
    LoanStatus getStatus();
}
