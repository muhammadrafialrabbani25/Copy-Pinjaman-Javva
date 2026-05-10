package com.p2plending.application.lender.dto;

import java.math.BigDecimal;

public class RegisterLenderCommand {
    private final String nama;
    private final String noTelepon;
    private final String ktpNumber;
    private final String alamat;
    private final String pekerjaan;
    private final BigDecimal initialBalance;

    public RegisterLenderCommand(String nama, String noTelepon, String ktpNumber, 
                                 String alamat, String pekerjaan, BigDecimal initialBalance) {
        this.nama = nama;
        this.noTelepon = noTelepon;
        this.ktpNumber = ktpNumber;
        this.alamat = alamat;
        this.pekerjaan = pekerjaan;
        this.initialBalance = initialBalance;
    }

    public String getNama() {
        return nama;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public String getKtpNumber() {
        return ktpNumber;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
}