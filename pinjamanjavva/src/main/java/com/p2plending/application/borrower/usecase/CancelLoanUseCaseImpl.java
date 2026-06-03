package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.CancelLoanCommand;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.repository.InvestmentRepository;
import com.p2plending.domain.shared.Money;

import java.util.List;
import java.util.Optional;

public class CancelLoanUseCaseImpl implements CancelLoanUseCase {
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;
    private final LoanCancellationService loanCancellationService;
    private final InvestmentRepository investmentRepository;

    public CancelLoanUseCaseImpl(BorrowerRepository borrowerRepository,
                                LoanRepository loanRepository,
                                LoanCancellationService loanCancellationService,
                                InvestmentRepository investmentRepository) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
        this.loanCancellationService = loanCancellationService;
        this.investmentRepository = investmentRepository;
    }

    @Override
    public void execute(CancelLoanCommand command) {
        // 1. Cari borrower
        Optional<Borrower> borrowerOpt = borrowerRepository.findById(command.getBorrowerId());
        if (!borrowerOpt.isPresent()) {
            throw new IllegalArgumentException("Borrower tidak ditemukan: " + command.getBorrowerId());
        }
        Borrower borrower = borrowerOpt.get();

        // 2. Cari loan
        Optional<LoanApplication> loanOpt = loanRepository.findById(command.getLoanId());
        if (!loanOpt.isPresent()) {
            throw new IllegalArgumentException("Loan tidak ditemukan: " + command.getLoanId());
        }
        LoanApplication loan = loanOpt.get();

        // 3. Validasi bahwa loan milik borrower ini
        if (!loan.getBorrowerId().equals(command.getBorrowerId())) {
            throw new IllegalArgumentException("Loan tidak milik borrower ini");
        }

        // 4. Cek apakah bisa cancel (minimum investasi 20% sudah tercapai + belum mencapai max cancel)
        Money fundedAmount = command.getFundedAmount();
        if (!loanCancellationService.canCancelLoan(
                loan.getAmount(),
                fundedAmount,
                borrower.getCancellationCount())) {
            throw new IllegalArgumentException("Tidak bisa membatalkan loan: minimum investasi belum tercapai atau sudah mencapai batas pembatalan");
        }

        // 5. Update loan status dan handle penalti secara penuh melalui Aggregate
        com.p2plending.domain.borrower.aggregate.LoanAggregate aggregate = 
            com.p2plending.domain.borrower.aggregate.LoanAggregate.load(loan, borrower);
        
        // Load existing investments agar aggregate bisa menghitung total investasi (butuh untuk denda)
        List<Investment> existingInvestments = investmentRepository.findByLoanId(loan.getId());
        for (Investment inv : existingInvestments) {
            aggregate.addInvestmentInternal(inv);
        }

        // Delegate ke Aggregate yang sudah dibuat Kemal
        aggregate.cancelWithPenaltyCheck();

        // 6. Simpan perubahan ke repositori
        loanRepository.save(loan);
        borrowerRepository.save(borrower);
    }
}