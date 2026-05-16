package com.p2plending.domain.borrower.entity;

import java.time.LocalDateTime;

import com.p2plending.domain.shared.Money;

public class Borrower {
    private final String id;
    private final String nama;
    private final String noTelepon;
    private final String alamat;
    private final KTP ktp;
    private final Money gaji;
    private final String pekerjaan;
    private final int creditScore;
    private int cancellationCount;
    private LocalDateTime lastBlockedDate;

    public Borrower(String id, String nama, String noTelepon, String alamat, KTP ktp, Money gaji, String pekerjaan,
            int creditScore) {

        this.id = id;

        if (nama == null) {
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.nama = nama;
        this.noTelepon = noTelepon;
        this.alamat = alamat;

        if (ktp == null) {
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.ktp = ktp;

        if (gaji == null) {
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.gaji = gaji;
        this.pekerjaan = pekerjaan;

        if (creditScore < 600) {
            throw new IllegalArgumentException("masukan data yang valid");
        } else if (creditScore > 1000) {
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.creditScore = creditScore;

        this.cancellationCount = 0;
        this.lastBlockedDate = null;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public String getAlamat() {
        return alamat;
    }

    public KTP getKtp() {
        return ktp;
    }

    public Money getGaji() {
        return gaji;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getCancellationCount() {
        return cancellationCount;
    }

    public void setCancellationCount(int cancellationCount) {
        this.cancellationCount = cancellationCount;
    }

    public LocalDateTime getLastBlockedDate() {
        return lastBlockedDate;
    }

    public void setLastBlockedDate(LocalDateTime lastBlockedDate) {
        this.lastBlockedDate = lastBlockedDate;
    }
}
