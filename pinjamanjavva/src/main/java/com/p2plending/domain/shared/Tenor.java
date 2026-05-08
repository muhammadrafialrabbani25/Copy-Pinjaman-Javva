package com.p2plending.domain.shared;


enum Tenor {
    ONE_MONTH(1),
    THREE_MONTHS(3),
    SIX_MONTHS(6),
    TWELVE_MONTHS(12);   
        
    Tenor(int months) {
        this.months = months;
    }

    private final int months;

    public int getMonths() {
        return months;
    }
}
