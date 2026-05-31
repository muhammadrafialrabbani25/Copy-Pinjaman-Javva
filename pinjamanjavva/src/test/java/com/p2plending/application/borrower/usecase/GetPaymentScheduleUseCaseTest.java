package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.PaymentDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.entity.Payment;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.repository.PaymentRepository;
import com.p2plending.domain.borrower.service.PaymentScheduleService;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPaymentScheduleUseCaseTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentScheduleService paymentScheduleService;

    @InjectMocks
    private GetPaymentScheduleUseCaseImpl useCase;

    private Borrower borrower;
    private LoanApplication loan;

    @BeforeEach
    void setUp() {
        borrower = mock(Borrower.class);
        
        loan = mock(LoanApplication.class);
        lenient().when(loan.getId()).thenReturn("L1");
        lenient().when(loan.getBorrowerId()).thenReturn("B1");
        lenient().when(loan.getAmount()).thenReturn(new Money(new BigDecimal("3000"), "IDR"));
        lenient().when(loan.getTenor()).thenReturn(Tenor.THREE_MONTHS);
    }

    @Test
    void testExecute_ExistingSchedule() {
        // Arrange
        when(borrowerRepository.findById("B1")).thenReturn(Optional.of(borrower));
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));
        
        Payment p1 = new Payment("P1", "L1", 1, new Money(new BigDecimal("1030"), "IDR"), LocalDate.now());
        when(paymentRepository.findByLoanId("L1")).thenReturn(Arrays.asList(p1));

        // Act
        List<PaymentDTO> result = useCase.execute("B1", "L1");

        // Assert
        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getId());
        verify(paymentScheduleService, never()).generatePaymentSchedule(any(), any(), any(), anyDouble());
    }

    @Test
    void testExecute_GenerateNewSchedule() {
        // Arrange
        when(borrowerRepository.findById("B1")).thenReturn(Optional.of(borrower));
        when(loanRepository.findById("L1")).thenReturn(Optional.of(loan));
        when(paymentRepository.findByLoanId("L1")).thenReturn(new ArrayList<>());
        
        Payment p1 = new Payment("P1", "L1", 1, new Money(new BigDecimal("1030"), "IDR"), LocalDate.now());
        when(paymentScheduleService.generatePaymentSchedule(eq("L1"), any(Money.class), eq(Tenor.THREE_MONTHS), eq(0.03)))
                .thenReturn(Arrays.asList(p1));

        // Act
        List<PaymentDTO> result = useCase.execute("B1", "L1");

        // Assert
        assertEquals(1, result.size());
        assertEquals("P1", result.get(0).getId());
        verify(paymentRepository, times(1)).save(p1);
    }
}
