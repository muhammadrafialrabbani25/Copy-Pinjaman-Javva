package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.InvestCommand;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.lender.repository.InvestmentRepository;
import com.p2plending.domain.lender.repository.LenderRepository;
import com.p2plending.domain.lender.service.InvestmentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;

import java.math.BigDecimal;

public class InvestLoanUseCaseImpl implements InvestLoanUseCase {

    private final LenderRepository lenderRepository;
    private final LoanRepository loanRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentService investmentService;

    public InvestLoanUseCaseImpl(LenderRepository lenderRepository,
                               LoanRepository loanRepository,
                               InvestmentRepository investmentRepository,
                               InvestmentService investmentService) {
        this.lenderRepository = lenderRepository;
        this.loanRepository = loanRepository;
        this.investmentRepository = investmentRepository;
        this.investmentService = investmentService;
    }

    @Override
    public void execute(InvestCommand command) {
        // 1. Validasi input
        if (command.getAmount() == null || command.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nominal investasi harus lebih dari 0");
        }

        // 2. Cari lender
        Lender lender = lenderRepository.findById(command.getLenderId())
            .orElseThrow(() -> new IllegalArgumentException("Lender tidak ditemukan: " + command.getLenderId()));

        // 3. Cari loan
        LoanApplication loan = loanRepository.findById(command.getLoanId())
            .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan: " + command.getLoanId()));

        // 4. Validasi loan status FUNDING
        if (loan.getStatus() != LoanStatus.FUNDING) {
            throw new IllegalArgumentException("Loan tidak dalam status FUNDING");
        }

        // 5. Validasi saldo lender cukup
        Money investmentAmount = new Money(command.getAmount(), "IDR");
        if (!lender.getSaldo().isGreaterThanOrEqual(investmentAmount)) {
            throw new IllegalArgumentException("Saldo lender tidak cukup untuk investasi");
        }

        // 6. Validasi minimum investasi 20%
        if (!investmentService.validateMinimumInvestment(loan.getAmount(), investmentAmount)) {
            throw new IllegalArgumentException("Investasi harus minimal 20% dari loan amount");
        }

        // 7. Buat investment baru
        String investmentId = "INV-" + System.currentTimeMillis();
        Investment investment = new Investment(
            investmentId,
            command.getLenderId(),
            command.getLoanId(),
            investmentAmount
        );

        // 8. Simpan investment
        investmentRepository.save(investment);

        // 8.5. Load aggregate & existing investments, lalu tambahkan investment baru
        com.p2plending.domain.borrower.aggregate.LoanAggregate aggregate = 
            com.p2plending.domain.borrower.aggregate.LoanAggregate.load(loan, null);
        java.util.List<Investment> existingInvestments = investmentRepository.findByLoanId(loan.getId());
        for (Investment inv : existingInvestments) {
            aggregate.addInvestmentInternal(inv);
        }
        aggregate.addInvestment(investment); // Ini akan mengecek funding complete dan bisa mengubah status
        loanRepository.save(loan); // Simpan perubahan status jika ada

        // 9. Kurangi saldo lender
        Money newBalance = lender.getSaldo().subtract(investmentAmount);
        Lender updatedLender = new Lender(
            lender.getId(),
            lender.getNama(),
            lender.getNoTelepon(),
            lender.getAlamat(),
            lender.getKtp(),
            lender.getPekerjaan(),
            newBalance
        );
        lenderRepository.save(updatedLender);
    }
}