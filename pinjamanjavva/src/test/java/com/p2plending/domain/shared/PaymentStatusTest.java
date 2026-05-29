package com.p2plending.domain.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentStatusTest {
    @Test
    void shouldHavePendingStatus() {
        PaymentStatus status = PaymentStatus.PENDING;

        assertEquals("PENDING", status.name());
    }

    @Test
    void shouldHavePaidStatus() {
        PaymentStatus status = PaymentStatus.PAID;

        assertEquals("PAID", status.name());
    }

    @Test
    void shouldHaveOverdueStatus() {
        PaymentStatus status = PaymentStatus.OVERDUE;

        assertEquals("OVERDUE", status.name());
    }
}
