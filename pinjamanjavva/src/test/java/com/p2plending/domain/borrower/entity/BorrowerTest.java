package com.p2plending.domain.borrower.entity;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.shared.Money;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class BorrowerTest {
    @Test
    void shouldCreateBorrowerWithValidData() {
        String id = "B001";
        String nama = "Budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 750;

        Borrower borrower = new Borrower(id, nama, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore);

        assertEquals(id, borrower.getId());
        assertEquals(nama, borrower.getNama());
        assertEquals(noTelepon, borrower.getNoTelepon());
        assertEquals(alamat, borrower.getAlamat());
        assertEquals(ktp, borrower.getKtp());
        assertEquals(gaji, borrower.getGaji());
        assertEquals(pekerjaan, borrower.getPekerjaan());
        assertEquals(creditScore, borrower.getCreditScore());

    }

    @Test // test 2
    void shouldThrowExceptionWhenNamaIsNull() {
        String id = "B001";
        String nama = null;
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 750;

        assertThrows(IllegalArgumentException.class, () -> {
            Borrower borrower = new Borrower(id, null, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore);
           });

    }

    @Test // test 3
    void shouldThrowExceptionWhenKTPIsNull() {
        String id = "B001";
        String nama = "budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = null;
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 750;

        assertThrows(IllegalArgumentException.class, () -> {
            Borrower borrower = new Borrower(id, nama, noTelepon, alamat, null, gaji, pekerjaan, creditScore);
        });
    }

    @Test //test 4
    void shouldThrowExceptionWhenGajiIsNull() {
        String id = "B001";
        String nama = "budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = null;
        String pekerjaan = "Engineer";
        int creditScore = 750;

        assertThrows(IllegalArgumentException.class, () -> {
            Borrower borrower = new Borrower(id, nama, noTelepon, alamat, ktp, null, pekerjaan, creditScore);
        });
    }

    @Test // test 5
    void shouldThrowExceptionWhenCreditScoreBelowMinimum() {
        String id = "B001";
        String nama = "Budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 500;

        assertThrows(IllegalArgumentException.class, () -> {
            Borrower borrower = new Borrower(id, nama, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore);
        });
    }

    @Test // test 6
    void shouldThrowExceptionWhenCreditScoreAboveMaximum() {
        String id = "B001";
        String nama = "Budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 1500;

        assertThrows(IllegalArgumentException.class, () -> {
            Borrower borrower = new Borrower(id, nama, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore);
        });
    }

    @Test // test 7
    void shouldHaveZeroCancellationCountWhenCreated() {
        String id = "B001";
        String nama = "Budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 750;

        Borrower borrower = new Borrower(id, nama, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore);
        
        assertEquals(0, borrower.getCancellationCount());
    }

    @Test // test 8
    void shouldHaveNullLastBlockedDateWhenCreated() {
        String id = "B001";
        String nama = "Budi";
        String noTelepon = "08123456789";
        String alamat = "Bandung";
        KTP ktp = new KTP("Budi", "1234567890123456");
        Money gaji = new Money(new BigDecimal("10000000"), "IDR");
        String pekerjaan = "Engineer";
        int creditScore = 750;

        Borrower borrower = new Borrower(id, nama, noTelepon, alamat, ktp, gaji, pekerjaan, creditScore);

        assertNull(borrower.getLastBlockedDate());
    }
}