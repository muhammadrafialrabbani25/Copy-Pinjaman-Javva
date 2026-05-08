package com.p2plending.domain.borrower.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.Money;

/**
 * Unit tests untuk LoanCancellationService (TDD)
 * Test pembatalan pinjaman dengan validasi investasi minimum dan blocking
 */
class LoanCancellationServiceTest {

    private LoanCancellationService service = new LoanCancellationService();

    // ============ Test Pembatalan Pertama (≥20% funded) ============

    @Test
    void testCancelLoan_FirstCancellation_MinimumFundingMet() {
        // Arrange (Persiapan)
        Borrower borrower = new Borrower(
                "B001", "Budi", "08123456789", "Bandung",
                new KTP("1234567890123456"), new BigDecimal("10000000"), "Engineer",
                750
        );
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("2000000"), "IDR"); // 20% dari 10M
        int currentCancellationCount = 0;

        // Act (Eksekusi)
        boolean canCancel = service.canCancelLoan(loanAmount, fundedAmount, currentCancellationCount);

        // Assert (Verifikasi)
        assertTrue(canCancel);
    }

    @Test
    void testCancelLoan_FirstCancellation_BelowMinimumFunding() {
        // Arrange (Persiapan)
        Borrower borrower = new Borrower(
                "B001", "Budi", "08123456789", "Bandung",
                new KTP("1234567890123456"), new BigDecimal("10000000"), "Engineer",
                750
        );
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("1000000"), "IDR"); // 10% dari 10M (< 20%)
        int currentCancellationCount = 0;

        // Act (Eksekusi)
        boolean canCancel = service.canCancelLoan(loanAmount, fundedAmount, currentCancellationCount);

        // Assert (Verifikasi)
        assertFalse(canCancel);
    }

    // ============ Test Pembatalan Maksimal (3x) ============

    @Test
    void testCancelLoan_SecondCancellation_Allowed() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("2000000"), "IDR"); // 20%
        int currentCancellationCount = 1; // Sudah 1x cancel

        // Act (Eksekusi)
        boolean canCancel = service.canCancelLoan(loanAmount, fundedAmount, currentCancellationCount);

        // Assert (Verifikasi)
        assertTrue(canCancel);
    }

    @Test
    void testCancelLoan_ThirdCancellation_Allowed() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("2000000"), "IDR"); // 20%
        int currentCancellationCount = 2; // Sudah 2x cancel

        // Act (Eksekusi)
        boolean canCancel = service.canCancelLoan(loanAmount, fundedAmount, currentCancellationCount);

        // Assert (Verifikasi)
        assertTrue(canCancel);
    }

    @Test
    void testCancelLoan_FourthCancellation_NotAllowed() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("2000000"), "IDR"); // 20%
        int currentCancellationCount = 3; // Sudah 3x cancel (maksimal)

        // Act (Eksekusi)
        boolean canCancel = service.canCancelLoan(loanAmount, fundedAmount, currentCancellationCount);

        // Assert (Verifikasi)
        assertFalse(canCancel);
    }

    // ============ Test Perhitungan Tanggal Block ============

    @Test
    void testCalculateBlockUntilDate_After3rdCancellation() {
        // Arrange (Persiapan)
        LocalDate cancellationDate = LocalDate.of(2026, 5, 9); // Hari pembatalan
        int cancellationCount = 3; // Pembatalan ke-3

        // Act (Eksekusi)
        LocalDate blockUntil = service.calculateBlockUntilDate(cancellationDate, cancellationCount);

        // Assert (Verifikasi)
        LocalDate expectedBlockUntil = LocalDate.of(2026, 9, 8); // 4 bulan kemudian
        assertEquals(expectedBlockUntil, blockUntil);
    }

    @Test
    void testCalculateBlockUntilDate_LessThan3Cancellations() {
        // Arrange (Persiapan)
        LocalDate cancellationDate = LocalDate.of(2026, 5, 9);
        int cancellationCount = 2; // Pembatalan ke-2 (tidak memicu block)

        // Act (Eksekusi)
        LocalDate blockUntil = service.calculateBlockUntilDate(cancellationDate, cancellationCount);

        // Assert (Verifikasi)
        assertFalse(blockUntil.isAfter(cancellationDate)); // Tidak ada block date
    }

    // ============ Test Update Cancellation Count ============

    @Test
    void testIncrementCancellationCount_FromZero() {
        // Arrange (Persiapan)
        int currentCount = 0;

        // Act (Eksekusi)
        int newCount = service.incrementCancellationCount(currentCount);

        // Assert (Verifikasi)
        assertEquals(1, newCount);
    }

    @Test
    void testIncrementCancellationCount_FromTwo() {
        // Arrange (Persiapan)
        int currentCount = 2;

        // Act (Eksekusi)
        int newCount = service.incrementCancellationCount(currentCount);

        // Assert (Verifikasi)
        assertEquals(3, newCount);
    }

    // ============ Test Validasi Minimum Investasi (20%) ============

    @Test
    void testIsMinimumInvestmentMet_Exactly20Percent() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("2000000"), "IDR"); // Exactly 20%

        // Act (Eksekusi)
        boolean isMet = service.isMinimumInvestmentMet(loanAmount, fundedAmount);

        // Assert (Verifikasi)
        assertTrue(isMet);
    }

    @Test
    void testIsMinimumInvestmentMet_AboveMinimum() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("3000000"), "IDR"); // 30% > 20%

        // Act (Eksekusi)
        boolean isMet = service.isMinimumInvestmentMet(loanAmount, fundedAmount);

        // Assert (Verifikasi)
        assertTrue(isMet);
    }

    @Test
    void testIsMinimumInvestmentMet_BelowMinimum() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("1000000"), "IDR"); // 10% < 20%

        // Act (Eksekusi)
        boolean isMet = service.isMinimumInvestmentMet(loanAmount, fundedAmount);

        // Assert (Verifikasi)
        assertFalse(isMet);
    }

    @Test
    void testIsMinimumInvestmentMet_ZeroFunding() {
        // Arrange (Persiapan)
        Money loanAmount = new Money(new BigDecimal("10000000"), "IDR");
        Money fundedAmount = new Money(new BigDecimal("0"), "IDR"); // 0% funding

        // Act (Eksekusi)
        boolean isMet = service.isMinimumInvestmentMet(loanAmount, fundedAmount);

        // Assert (Verifikasi)
        assertFalse(isMet);
    }
}
