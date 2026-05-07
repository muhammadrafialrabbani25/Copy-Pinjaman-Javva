package com.p2plending.domain.shared;

public enum LoanStatus {
    PENDING,
    VERIFIED,
    FUNDING,
    FUNDED,
    DISBURSED,
    CANCELLED,
    EXPIRED_FUNDING;

    // Validate apakah loan bisa transition dari current state ke nextStatus

    public boolean canTransitionTo(LoanStatus nextStatus) {
        // PENDING: bisa ke VERIFIED atau CANCELLED
        if (this == PENDING) {
            return (nextStatus == VERIFIED || nextStatus == CANCELLED);
        }

        // VERIFIED: bisa ke FUNDING atau CANCELLED
        if (this == VERIFIED) {
            return (nextStatus == FUNDING || nextStatus == CANCELLED);
        }

        // FUNDING: bisa ke FUNDED, EXPIRED_FUNDING, atau CANCELLED
        if (this == FUNDING) {
            return (nextStatus == FUNDED || nextStatus == EXPIRED_FUNDING || nextStatus == CANCELLED);
        }

        // FUNDED: bisa ke DISBURSED atau CANCELLED
        if (this == FUNDED) {
            return (nextStatus == DISBURSED || nextStatus == CANCELLED);
        }

        // DISBURSED: uang sudah cair, tidak ada transition lagi
        if (this == DISBURSED) {
            return false;
        }

        if (this == CANCELLED) {
            return false;
        }

        // EXPIRED_FUNDING: hanya bisa ke CANCELLED
        if (this == EXPIRED_FUNDING) {
            return nextStatus == CANCELLED;
        }

        return false;
    }
}