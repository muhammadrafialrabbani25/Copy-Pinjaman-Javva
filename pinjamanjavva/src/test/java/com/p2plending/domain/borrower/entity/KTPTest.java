package com.p2plending.domain.borrower.entity;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KTPTest {
    @Test 
    void shouldCreateKTPWithValidData(){
        String nomorKtp = "1234567890123456";
        String nama = "budi";

        KTP ktp = new KTP(nomorKtp,nama);

        assertEquals(nama, ktp.getNama());
        assertEquals(nomorKtp, ktp.getNomorKtp());
    }

    @Test
    void shouldThrowExceptionWhenNomorKTPIsNot16Digits() {
        String nomorKtp = "123456789012345";
        String nama = "budi";

        assertThrows(IllegalArgumentException.class, () -> {
            new KTP("123456789012345", nama);
        });
    }

    @Test
    void shouldThrowExceptionWhenNomorKTPIsNull() {
        String nomorKtp = null;
        String nama = "budi";

        assertThrows(IllegalArgumentException.class, () -> {
            new KTP(nama, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenNamaIsNull() {
        String nomorKtp = "1234567890123456";
        String nama = null;

        assertThrows(IllegalArgumentException.class, () -> {
            new KTP(null, nomorKtp);
        });
    
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreSame() {
        String nomorKtp2 = "1234567890123456";
        String nama2 = "budi";
        String nomorKtp = "1234567890123456";
        String nama = "budi";

        KTP ktp2 = new KTP(nomorKtp2, nama2);
        KTP ktp = new KTP(nomorKtp, nama);

        assertEquals(ktp2, ktp);
    }

    @Test
    void shouldNotBeEqualWhenNomorKTPIsDifferent() {
        String nomorKtp2 = "1234567890666666";
        String nama2 = "jaya";
        String nomorKtp = "1234567890123456";
        String nama = "budi";

        KTP ktp2 = new KTP(nomorKtp2, nama2);
        KTP ktp = new KTP(nomorKtp, nama);

        assertNotEquals(ktp2, ktp);
    }
}
