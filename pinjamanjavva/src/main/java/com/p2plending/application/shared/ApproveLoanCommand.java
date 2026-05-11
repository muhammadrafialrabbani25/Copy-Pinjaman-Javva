package com.p2plending.application.shared;

public class ApproveLoanCommand {
    private final String loanId;
    private final boolean approve;

    public ApproveLoanCommand(String loanId, boolean approve) {
        this.loanId = loanId;
        this.approve = approve;
    }

    public String getLoanId() {
        return loanId;
    }

    public boolean isApprove() {
        return approve;
    }
}
