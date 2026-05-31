package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.PaymentDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.repository.PaymentRepository;
import com.p2plending.domain.borrower.service.PaymentScheduleService;

import java.util.List;
import java.util.stream.Collectors;

public class GetPaymentScheduleUseCaseImpl implements GetPaymentScheduleUseCase {

    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentScheduleService paymentScheduleService;

    public GetPaymentScheduleUseCaseImpl(BorrowerRepository borrowerRepository,
                                         LoanRepository loanRepository,
                                         PaymentRepository paymentRepository,
                                         PaymentScheduleService paymentScheduleService) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
        this.paymentRepository = paymentRepository;
        this.paymentScheduleService = paymentScheduleService;
    }

    @Override
    public List<PaymentDTO> execute(String borrowerId, String loanId) {
        // 1. Validasi
        borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower tidak ditemukan"));

        LoanApplication loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan tidak ditemukan"));

        if (!loan.getBorrowerId().equals(borrowerId)) {
            throw new IllegalArgumentException("Loan ini bukan milik borrower tersebut");
        }

        // 2. Ambil jadwal dari repository
        List<Payment> payments = paymentRepository.findByLoanId(loanId);

        // 3. Jika kosong, generate jadwal baru menggunakan PaymentScheduleService (misal saat baru saja di-disburse)
        if (payments == null || payments.isEmpty()) {
            double interestRate = 0.03; // Default 3% per month
            payments = paymentScheduleService.generatePaymentSchedule(
                    loan.getId(), 
                    loan.getAmount(), 
                    loan.getTenor(), 
                    interestRate
            );
            
            // Simpan ke database
            for (Payment p : payments) {
                paymentRepository.save(p);
            }
        }

        // 4. Return DTO list
        return payments.stream()
                .map(PaymentDTO::new)
                .collect(Collectors.toList());
    }
}
