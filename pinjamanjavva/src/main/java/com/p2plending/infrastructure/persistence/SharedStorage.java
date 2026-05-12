package com.p2plending.infrastructure.persistence;

import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.entity.Lender;
import java.util.HashMap;
import java.util.Map;

public class SharedStorage {

    private static final SharedStorage INSTANCE = new SharedStorage();

    private final Map<String, Borrower> borrowers = new HashMap<>();
    private final Map<String, LoanApplication> loans = new HashMap<>();
    private final Map<String, Lender> lenders = new HashMap<>();
    private final Map<String, Investment> investments = new HashMap<>();

    private SharedStorage() {}

    public static SharedStorage getInstance() {
        return INSTANCE;
    }

    public Map<String, Borrower> getBorrowers() { return borrowers; }
    public Map<String, LoanApplication> getLoans() { return loans; }
    public Map<String, Lender> getLenders() { return lenders; }
    public Map<String, Investment> getInvestments() { return investments; }
}