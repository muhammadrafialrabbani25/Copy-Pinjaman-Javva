package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.LenderDTO;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.lender.repository.LenderRepository;
import com.p2plending.domain.shared.Money;

import java.math.BigDecimal;

public class TopUpSaldoUseCaseImpl implements TopUpSaldoUseCase {

    private static final BigDecimal ADMIN_FEE_RATE = new BigDecimal("0.02");
    private final LenderRepository lenderRepository;

    public TopUpSaldoUseCaseImpl(LenderRepository lenderRepository) {
        this.lenderRepository = lenderRepository;
    }

    @Override
    public LenderDTO execute(String lenderId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nominal top up harus lebih dari 0");
        }

        Lender lender = lenderRepository.findById(lenderId)
            .orElseThrow(() -> new IllegalArgumentException("Lender tidak ditemukan: " + lenderId));

        BigDecimal adminFee = amount.multiply(ADMIN_FEE_RATE);
        BigDecimal netTopUp = amount.subtract(adminFee);
        BigDecimal newBalanceAmount = lender.getSaldo().getAmount().add(netTopUp);

        Lender updatedLender = new Lender(
            lender.getId(),
            lender.getNama(),
            lender.getNoTelepon(),
            lender.getAlamat(),
            lender.getKtp(),
            lender.getPekerjaan(),
            new Money(newBalanceAmount, "IDR")
        );

        Lender saved = lenderRepository.save(updatedLender);

        return new LenderDTO(
            saved.getId(),
            saved.getNama(),
            saved.getKtp().getNomorKtp(),
            saved.getSaldo().getAmount()
        );
    }
}