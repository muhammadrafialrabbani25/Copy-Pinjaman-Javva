package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.MakePaymentCommand;
import com.p2plending.application.borrower.dto.PaymentDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.repository.PaymentRepository;
import com.p2plending.domain.borrower.service.RepaymentService;
import com.p2plending.domain.shared.LoanStatus;

import java.util.Optional;

public class MakePaymentUseCaseImpl implements MakePaymentUseCase {

    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final RepaymentService repaymentService;

    public MakePaymentUseCaseImpl(BorrowerRepository borrowerRepository,
                                  LoanRepository loanRepository,
                                  PaymentRepository paymentRepository,
                                  RepaymentService repaymentService) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
        this.paymentRepository = paymentRepository;
        this.repaymentService = repaymentService;
    }

    @Override
    public PaymentDTO execute(MakePaymentCommand command) {
        // 1. Validasi Borrower
        borrowerRepository.findById(command.getBorrowerId())
                .orElseThrow(() -> new IllegalArgumentException("Borrower tidak ditemukan"));

        // 2. Validasi Payment
        Payment payment = paymentRepository.findById(command.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Payment tidak ditemukan"));

        // 3. Validasi Loan
        LoanApplication loan = loanRepository.findById(payment.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan"));

        // Cek kepemilikan loan
        if (!loan.getBorrowerId().equals(command.getBorrowerId())) {
            throw new IllegalArgumentException("Loan ini bukan milik borrower tersebut");
        }

        // Cek status loan
        if (loan.getStatus() != LoanStatus.DISBURSED) {
            throw new IllegalStateException("Hanya loan berstatus DISBURSED yang dapat dibayar cicilannya");
        }

        // 3. Proses Pembayaran via RepaymentService
        repaymentService.validatePaymentAmount(command.getAmount());
        
        // Update denda if overdue
        repaymentService.checkAndUpdateStatus(payment);

        // Validasi amount yang dibayar harus sama dengan amount cicilan + denda
        java.math.BigDecimal totalToPay = payment.getAmount().getAmount().add(payment.getDenda());
        if (command.getAmount().getAmount().compareTo(totalToPay) < 0) {
            throw new IllegalArgumentException("Jumlah pembayaran kurang dari total tagihan (pokok + bunga + denda)");
        }

        repaymentService.makePayment(payment);

        // 4. Save Payment
        paymentRepository.save(payment);

        // 5. Return DTO
        return new PaymentDTO(payment);
    }
}
