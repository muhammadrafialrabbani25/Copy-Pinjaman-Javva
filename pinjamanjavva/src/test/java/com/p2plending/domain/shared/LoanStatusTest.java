package com.p2plending.domain.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoanStatusTest {
    @Test // test 1
    void shouldAllowPendingToVerified(){
        assertTrue(LoanStatus.PENDING.canTransitionTo(LoanStatus.VERIFIED));
    }

    @Test // test 2
    void shouldAllowPendingToCancelled() {
        assertTrue(LoanStatus.PENDING.canTransitionTo(LoanStatus.CANCELLED));
    }

    @Test // test 3
    void shouldAllowVerifiedToFunding() {
        assertTrue(LoanStatus.VERIFIED.canTransitionTo(LoanStatus.FUNDING));
    }

    @Test // test 4
    void shouldAllowFundingToFunded() {
        assertTrue(LoanStatus.FUNDING.canTransitionTo(LoanStatus.FUNDED));
    }

    @Test // test 5
    void shouldAllowFundingToExpiredFunding() {
        assertTrue(LoanStatus.FUNDING.canTransitionTo(LoanStatus.EXPIRED_FUNDING));
    }

    @Test // test 6
    void shouldAllowFundingToCancelled() {
        assertTrue(LoanStatus.FUNDING.canTransitionTo(LoanStatus.CANCELLED));
    }

    @Test // test 7
    void shouldAllowFundedToDisbursed() {
        assertTrue(LoanStatus.FUNDED.canTransitionTo(LoanStatus.DISBURSED));
    }

    @Test // test 8
    void shouldAllowFundedToCancelled() {
        assertTrue(LoanStatus.FUNDED.canTransitionTo(LoanStatus.CANCELLED));
    }

    @Test // test 9
    void shouldNotAllowDisbursedToCancelled() {
        assertFalse(LoanStatus.DISBURSED.canTransitionTo(LoanStatus.CANCELLED));
    }

    @Test // test 10
    void shouldNotAllowCancelledToVerified() {
        assertFalse(LoanStatus.CANCELLED.canTransitionTo(LoanStatus.VERIFIED));
    }
    
    @Test // test 11
    void shouldAllowExpiredFundingToCancelled() {
        assertTrue(LoanStatus.EXPIRED_FUNDING.canTransitionTo(LoanStatus.CANCELLED));
    }
}
