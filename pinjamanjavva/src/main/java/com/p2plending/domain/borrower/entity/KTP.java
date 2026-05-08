package com.p2plending.domain.borrower.entity;

public class KTP {
    private final String nama;
    private final String nomorKtp;

    public KTP(String nomorKTP, String nama) {
        if(nama == null){
            throw new IllegalArgumentException("masukan nama anda");
        }
        this.nama = nama;
        if (nomorKTP == null) {
            throw new IllegalArgumentException("nomot ktp tidak valid");
        }
        if(nomorKTP.length() != 16){
            throw new IllegalArgumentException("nomor ktp tidak valid");
        }
        this.nomorKtp = nomorKTP;
    }

    public String getNama() {
        return nama;
    }

    public String getNomorKtp() {
        return nomorKtp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nama == null) ? 0 : nama.hashCode());
        result = prime * result + ((nomorKtp == null) ? 0 : nomorKtp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KTP other = (KTP) obj;
        if (nama == null) {
            if (other.nama != null)
                return false;
        } else if (!nama.equals(other.nama))
            return false;
        if (nomorKtp == null) {
            if (other.nomorKtp != null)
                return false;
        } else if (!nomorKtp.equals(other.nomorKtp))
            return false;
        return true;
    }

    
}