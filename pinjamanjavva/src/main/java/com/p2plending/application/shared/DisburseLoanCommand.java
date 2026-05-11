package com.p2plending.application.shared;

public class DisburseLoanCommand {
    private final String loanId;

    public DisburseLoanCommand(String loanId) {
        this.loanId = loanId;
    }

    public String getLoanId() {
        return loanId;
    }
}
