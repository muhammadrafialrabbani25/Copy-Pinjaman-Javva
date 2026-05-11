package com.p2plending.application.shared;

import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisburseUseCaseTest {

    private LoanRepository loanRepository;
    private DisburseUseCaseImpl disburseUseCase;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        disburseUseCase = new DisburseUseCaseImpl(loanRepository);
    }

    @Test
    void execute_WhenFunded_ShouldSetStatusToDisbursed() throws Exception {
        // Arrange
        String loanId = "LOAN-123";
        DisburseLoanCommand command = new DisburseLoanCommand(loanId);
        
        LoanApplication mockLoan = new LoanApplication(loanId, "BOR-1", new Money(new BigDecimal("1000000"), "IDR"), Tenor.ONE_MONTH, 700);
        
        // Use reflection to set status to FUNDED
        java.lang.reflect.Field field = LoanApplication.class.getDeclaredField("status");
        field.setAccessible(true);
        field.set(mockLoan, LoanStatus.FUNDED);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(mockLoan));

        // Act
        disburseUseCase.execute(command);

        // Assert
        ArgumentCaptor<LoanApplication> loanCaptor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(loanRepository).save(loanCaptor.capture());
        
        LoanApplication savedLoan = loanCaptor.getValue();
        assertEquals(LoanStatus.DISBURSED, savedLoan.getStatus());
    }

    @Test
    void execute_WhenLoanNotFound_ShouldThrowException() {
        // Arrange
        String loanId = "LOAN-123";
        DisburseLoanCommand command = new DisburseLoanCommand(loanId);
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            disburseUseCase.execute(command);
        });
        assertEquals("Loan tidak ditemukan: " + loanId, exception.getMessage());
    }

    @Test
    void execute_WhenLoanNotFunded_ShouldThrowException() {
        // Arrange
        String loanId = "LOAN-123";
        DisburseLoanCommand command = new DisburseLoanCommand(loanId);
        
        LoanApplication mockLoan = new LoanApplication(loanId, "BOR-1", new Money(new BigDecimal("1000000"), "IDR"), Tenor.ONE_MONTH, 700);
        // Status defaults to PENDING
        
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(mockLoan));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            disburseUseCase.execute(command);
        });
        assertEquals("Hanya loan dengan status FUNDED yang dapat di-disburse", exception.getMessage());
    }
}
