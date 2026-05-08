package com.p2plending.domain.lender.entity;

import org.junit.jupiter.api.Test;

import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.Money;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class LenderTest {
    @Test // 1
    void shouldCreateLenderWithValidData(){

        String id = "L001";
        String nama = "Budi";
        String noTelepon = "081322789987";
        String alamat = "bandung";
        KTP ktp = new KTP(nama, "1234567890123456");
        String pekerjaan = "tukang cilok";
        Money saldo = new Money(new BigDecimal("100000"), "IDR");

        Lender lender = new Lender(id, nama, noTelepon, alamat, ktp, pekerjaan, saldo);

        assertEquals(id, lender.getId());
        assertEquals(nama, lender.getNama());
        assertEquals(noTelepon, lender.getNoTelepon());
        assertEquals(alamat, lender.getAlamat());
        assertEquals(ktp, lender.getKtp());
        assertEquals(pekerjaan, lender.getPekerjaan());
        assertEquals(saldo, lender.getSaldo());

    }    
    
    @Test // 2
    void shouldThrowExceptionWhenNamaIsNull()  {
        String id = "L001";
        String nama = null;
        String noTelepon = "081322789987";
        String alamat = "bandung";
        KTP ktp = new KTP("budi", "1234567890123456");
        String pekerjaan = "tukang cilok";
        Money saldo = new Money(new BigDecimal("100000"), "IDR");

        assertThrows(IllegalArgumentException.class, () -> {
        Lender lender = new Lender(id, nama, noTelepon, alamat, ktp, pekerjaan, saldo);
        });
    }

    @Test // 3
    void shouldThrowExceptionWhenKTPIsNull(){
        String id = "L001";
        String nama = "budi";
        String noTelepon = "081322789987";
        String alamat = "bandung";
        KTP ktp = null;
        String pekerjaan = "tukang cilok";
        Money saldo = new Money(new BigDecimal("100000"), "IDR");

        assertThrows(IllegalArgumentException.class, () -> {
            Lender lender = new Lender(id, nama, noTelepon, alamat, ktp, pekerjaan, saldo);
        });
    }

    @Test // 4
    void shouldThrowExceptionWhenSaldoIsNull() {
        String id = "L001";
        String nama = "budi";
        String noTelepon = "081322789987";
        String alamat = "bandung";
        KTP ktp = new KTP(nama, "1234567890123456");
        String pekerjaan = "tukang cilok";
        Money saldo = null;

        assertThrows(IllegalArgumentException.class, () -> {
            Lender lender = new Lender(id, nama, noTelepon, alamat, ktp, pekerjaan, saldo);
        });

    }

}
