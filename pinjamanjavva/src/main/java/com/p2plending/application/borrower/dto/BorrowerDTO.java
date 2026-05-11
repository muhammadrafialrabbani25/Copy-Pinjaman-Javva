package com.p2plending.application.borrower.dto;

import java.math.BigDecimal;

public class BorrowerDTO {
    private final String id;
    private final String nama;
    private final String nomorKtp;
    private final BigDecimal gaji;
    private final int creditScore;

    public BorrowerDTO(String id, String nama, String nomorKtp, BigDecimal gaji, int creditScore) {
        this.id = id;
        this.nama = nama;
        this.nomorKtp = nomorKtp;
        this.gaji = gaji;
        this.creditScore = creditScore;
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

    public BigDecimal getGaji() {
        return gaji;
    }

    public int getCreditScore() {
        return creditScore;
    }
}