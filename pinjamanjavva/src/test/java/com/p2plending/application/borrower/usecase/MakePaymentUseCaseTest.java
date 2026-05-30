package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.MakePaymentCommand;
import com.p2plending.application.borrower.dto.PaymentDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.repository.PaymentRepository;
import com.p2plending.domain.borrower.service.RepaymentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MakePaymentUseCaseTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RepaymentService repaymentService;

    @InjectMocks
    private MakePaymentUseCaseImpl useCase;

    private Borrower borrower;
    private LoanApplication loan;
    private Payment payment;

    @BeforeEach
    void setUp() {
        borrower = mock(Borrower.class);
        
        // Use reflection to bypass protected constructor or use standard setters if available.
        // In clean architecture, we usually mock the entities or use standard constructors.
        // For LoanApplication, we can create a dummy one.
        loan = mock(LoanApplication.class);
        lenient().when(loan.getId()).thenReturn("L1");
        lenient().when(loan.getBorrowerId()).thenReturn("B1");
        lenient().when(loan.getStatus()).thenReturn(LoanStatus.DISBURSED);

        payment = new Payment("P1", "L1", 1, new Money(new BigDecimal("1000"), "IDR"), LocalDate.now());
    }

    @Test
    void testMakePayment_Success() {
        // Arrange
        MakePaymentCommand command = new MakePaymentCommand("B1", "P1", new Money(new BigDecimal("1000"), "IDR"));

        when(borrowerRepository.findById("B1")).thenReturn(Optional.of(borrower));
        when(paymentRepository.findById("P1")).thenReturn(Optional.of(payment));
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));

        // Act
        PaymentDTO result = useCase.execute(command);

        // Assert
        assertNotNull(result);
        assertEquals("P1", result.getId());
        
        verify(repaymentService, times(1)).validatePaymentAmount(any(Money.class));
        verify(repaymentService, times(1)).checkAndUpdateStatus(payment);
        verify(repaymentService, times(1)).makePayment(payment);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testMakePayment_BorrowerNotFound() {
        // Arrange
        MakePaymentCommand command = new MakePaymentCommand("B1", "P1", new Money(new BigDecimal("1000"), "IDR"));
        when(borrowerRepository.findById("B1")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        assertEquals("Borrower tidak ditemukan", exception.getMessage());
    }

    @Test
    void testMakePayment_LoanNotDisbursed() {
        // Arrange
        MakePaymentCommand command = new MakePaymentCommand("B1", "P1", new Money(new BigDecimal("1000"), "IDR"));

        lenient().when(borrowerRepository.findById("B1")).thenReturn(Optional.of(borrower));
        lenient().when(paymentRepository.findById("P1")).thenReturn(Optional.of(payment));
        
        LoanApplication notDisbursedLoan = mock(LoanApplication.class);
        when(notDisbursedLoan.getBorrowerId()).thenReturn("B1");
        when(notDisbursedLoan.getStatus()).thenReturn(LoanStatus.FUNDED); // Not disbursed
        when(loanRepository.findById("L1")).thenReturn(Optional.of(notDisbursedLoan));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> useCase.execute(command));
        assertEquals("Hanya loan berstatus DISBURSED yang dapat dibayar cicilannya", exception.getMessage());
    }
}
