package com.p2plending.domain.lender.entity;

import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.shared.Money;

public class Lender {
    private final String id;
    private final String nama;
    private final String noTelepon;
    private final String alamat;
    private final KTP ktp;
    private final String pekerjaan;
    private final Money saldo;
    
    
    public Lender(String id, String nama, String noTelepon, String alamat, KTP ktp, String pekerjaan, Money saldo) {
        this.id = id;

        if (nama == null){
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.nama = nama;
        this.noTelepon = noTelepon;
        this.alamat = alamat;

        if (ktp == null){
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.ktp = ktp;
        this.pekerjaan = pekerjaan;

        if (saldo == null){
            throw new IllegalArgumentException("masukan data yang valid");
        }
        this.saldo = saldo;
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


    public String getPekerjaan() {
        return pekerjaan;
    }


    public Money getSaldo() {
        return saldo;
    }
    
    
    


}