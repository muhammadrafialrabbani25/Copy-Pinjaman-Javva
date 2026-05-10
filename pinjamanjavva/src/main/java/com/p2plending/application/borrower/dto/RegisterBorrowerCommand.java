package com.p2plending.application.borrower.dto;

public class RegisterBorrowerCommand {
    private final String name;
    private final String noTelepon;
    private final String ktpNumber;
    private final String alamat;
    private final long monthlySalary;
    private final int creditScore;

    public RegisterBorrowerCommand(String name, String noTelepon, String ktpNumber, String alamat, 
                                   long monthlySalary, int creditScore) {
        this.name = name;
        this.noTelepon = noTelepon;
        this.ktpNumber = ktpNumber;
        this.alamat = alamat;
        this.monthlySalary = monthlySalary;
        this.creditScore = creditScore;
    }

    public String getName() {
        return name;
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

    public long getMonthlySalary() {
        return monthlySalary;
    }

    public int getCreditScore() {
        return creditScore;
    }
}