package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.CancelLoanCommand;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

public class CancelLoanUseCaseImpl implements CancelLoanUseCase {
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;
    private final LoanCancellationService loanCancellationService;

    public CancelLoanUseCaseImpl(BorrowerRepository borrowerRepository,
                                LoanRepository loanRepository,
                                LoanCancellationService loanCancellationService) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
        this.loanCancellationService = loanCancellationService;
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

        Money fundedAmount = command.getFundedAmount();
        // 4. Cek apakah bisa cancel (minimum investasi 20% sudah tercapai + belum mencapai max cancel)
        if (!loanCancellationService.canCancelLoan(
        loan.getAmount(),
        fundedAmount,
        borrower.getCancellationCount())) {
    throw new IllegalArgumentException("Tidak bisa membatalkan loan: minimum investasi belum tercapai atau sudah mencapai batas pembatalan");
}

        // 5. Update loan status jadi CANCELLED
        setLoanStatus(loan, LoanStatus.CANCELLED);
        setLoanCancelledDate(loan, LocalDateTime.now());

        // 6. Simpan loan
        loanRepository.save(loan);

        // 7. Naikkan cancellation count borrower
        int newCount = loanCancellationService.incrementCancellationCount(borrower.getCancellationCount());
        setBorrowerCancellationCount(borrower, newCount);

        // 8. Jika sudah 3x cancel, set lastBlockedDate
        if (newCount >= 3) {
            setBorrowerLastBlockedDate(borrower, LocalDateTime.now());
        }

        // 9. Simpan borrower
        borrowerRepository.save(borrower);
    }

    private void setLoanStatus(LoanApplication loan, LoanStatus status) {
        try {
            Field field = LoanApplication.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(loan, status);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set status loan", e);
        }
    }

    private void setLoanCancelledDate(LoanApplication loan, LocalDateTime date) {
        try {
            Field field = LoanApplication.class.getDeclaredField("cancelledDate");
            field.setAccessible(true);
            field.set(loan, date);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set cancelledDate", e);
        }
    }

    private void setBorrowerCancellationCount(Borrower borrower, int count) {
        try {
            Field field = Borrower.class.getDeclaredField("cancellationCount");
            field.setAccessible(true);
            field.set(borrower, count);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set cancellationCount", e);
        }
    }

    private void setBorrowerLastBlockedDate(Borrower borrower, LocalDateTime date) {
        try {
            Field field = Borrower.class.getDeclaredField("lastBlockedDate");
            field.setAccessible(true);
            field.set(borrower, date);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set lastBlockedDate", e);
        }
    }
}