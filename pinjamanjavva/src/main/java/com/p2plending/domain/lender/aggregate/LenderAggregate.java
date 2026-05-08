package com.p2plending.domain.lender.aggregate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.shared.Money;

/**
 * Mengelola investasi lender dan validasi transaksi
 */
public class LenderAggregate {

    /**
     * Berisi: id, nama, noTelepon, alamat, ktp, pekerjaan, saldo total
     */
    private final Lender lender;

    /**
     * Portfolio/history investasi yang dilakukan oleh lender
     * Diupdate setiap kali lender invest atau cancel
     */
    private final List<Investment> investments;

    private LenderAggregate(Lender lender) {
        this.lender = lender;
        this.investments = new ArrayList<>();
    }

    public static LenderAggregate create(Lender lender) {
        if (lender == null) {
            throw new IllegalArgumentException("Lender tidak boleh null");
        }
        return new LenderAggregate(lender);
    }

    public void addInvestment(Investment investment) {
        if (investment == null) {
            throw new IllegalArgumentException("Investment tidak boleh null");
        }
        this.investments.add(investment);
    }

    /**
     * Hitung total saldo lender (saldo awal dari entity)
     */
    public Money getTotalSaldo() {
        return lender.getSaldo();
    }

    /**
     * Hitung total jumlah investasi yang sudah dilakukan
     */
    public Money getTotalInvested() {
        BigDecimal total = investments.stream()
                .map(inv -> inv.getAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Money(total, lender.getSaldo().getCurrency());
    }

    /**
     * Hitung saldo yang masih tersedia untuk investasi baru atau top-up
     */
    public Money getAvailableSaldo() {
        BigDecimal available = lender.getSaldo().getAmount()
                .subtract(getTotalInvested().getAmount());

        return new Money(available, lender.getSaldo().getCurrency());
    }

    /**
     * Validasi apakah lender punya saldo cukup untuk investasi
     */
    public boolean hasSufficientSaldo(Money investmentAmount) {
        if (investmentAmount == null) {
            throw new IllegalArgumentException("Investment amount tidak boleh null");
        }
        Money availableSaldo = getAvailableSaldo();
        return availableSaldo.getAmount().compareTo(investmentAmount.getAmount()) >= 0;
    }

    /**
     * Retrieve daftar investasi berdasarkan loan ID
     */
    public List<Investment> getInvestmentsByLoanId(String loanId) {
        return investments.stream()
                .filter(inv -> inv.getLoanId().equals(loanId))
                .collect(Collectors.toList());
    }

    /**
     * Hitung jumlah investasi aktif dalam portfolio
     */
    public long getActiveInvestmentsCount() {
        return investments.stream()
                .filter(inv -> inv.getStatus() == Investment.InvestmentStatus.ACTIVE)
                .count();
    }

    // Getters

    public Lender getLender() {
        return lender;
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(investments);
    }
}
