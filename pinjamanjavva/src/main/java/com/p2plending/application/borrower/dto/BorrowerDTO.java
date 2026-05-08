package com.p2plending.application.borrower.dto;

public class BorrowerDTO {
    private final String id;
    private final String name;
    private final String ktpNumber;
    private final long monthlySalary;
    private final int creditScore;

    public BorrowerDTO(String id, String name, String ktpNumber, long monthlySalary, int creditScore) {
        this.id = id;
        this.name = name;
        this.ktpNumber = ktpNumber;
        this.monthlySalary = monthlySalary;
        this.creditScore = creditScore;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKtpNumber() {
        return ktpNumber;
    }

    public long getMonthlySalary() {
        return monthlySalary;
    }
    public int getCreditScore() {
        return creditScore;
    }
}