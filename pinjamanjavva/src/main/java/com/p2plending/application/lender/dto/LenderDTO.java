package com.p2plending.application.lender.dto;

import java.math.BigDecimal;

public class LenderDTO {
    private final String id;
    private final String nama;
    private final String nomorKtp;
    private final BigDecimal saldo;

    public LenderDTO(String id, String nama, String nomorKtp, BigDecimal saldo) {
        this.id = id;
        this.nama = nama;
        this.nomorKtp = nomorKtp;
        this.saldo = saldo;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNomorKtp() {
        return nomorKtp;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}