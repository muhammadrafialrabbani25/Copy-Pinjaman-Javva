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

class ApproveLoanUseCaseTest {

    private LoanRepository loanRepository;
    private ApproveLoanUseCaseImpl approveLoanUseCase;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        approveLoanUseCase = new ApproveLoanUseCaseImpl(loanRepository);
    }

    @Test
    void execute_WhenApprove_ShouldSetStatusToFunding() {
        // Arrange
        String loanId = "LOAN-123";
        ApproveLoanCommand command = new ApproveLoanCommand(loanId, true);
        
        LoanApplication mockLoan = new LoanApplication(loanId, "BOR-1", new Money(new BigDecimal("1000000"), "IDR"), Tenor.ONE_MONTH, 700);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(mockLoan));

        // Act
        approveLoanUseCase.execute(command);

        // Assert
        ArgumentCaptor<LoanApplication> loanCaptor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(loanRepository).save(loanCaptor.capture());
        
        LoanApplication savedLoan = loanCaptor.getValue();
        assertEquals(LoanStatus.FUNDING, savedLoan.getStatus());
    }

    @Test
    void execute_WhenReject_ShouldSetStatusToCancelled() {
        // Arrange
        String loanId = "LOAN-123";
        ApproveLoanCommand command = new ApproveLoanCommand(loanId, false);
        
        LoanApplication mockLoan = new LoanApplication(loanId, "BOR-1", new Money(new BigDecimal("1000000"), "IDR"), Tenor.ONE_MONTH, 700);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(mockLoan));

        // Act
        approveLoanUseCase.execute(command);

        // Assert
        ArgumentCaptor<LoanApplication> loanCaptor = ArgumentCaptor.forClass(LoanApplication.class);
        verify(loanRepository).save(loanCaptor.capture());
        
        LoanApplication savedLoan = loanCaptor.getValue();
        assertEquals(LoanStatus.CANCELLED, savedLoan.getStatus());
    }

    @Test
    void execute_WhenLoanNotFound_ShouldThrowException() {
        // Arrange
        String loanId = "LOAN-123";
        ApproveLoanCommand command = new ApproveLoanCommand(loanId, true);
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            approveLoanUseCase.execute(command);
        });
        assertEquals("Loan tidak ditemukan: " + loanId, exception.getMessage());
    }

    @Test
    void execute_WhenLoanNotPending_ShouldThrowException() throws Exception {
        // Arrange
        String loanId = "LOAN-123";
        ApproveLoanCommand command = new ApproveLoanCommand(loanId, true);
        
        LoanApplication mockLoan = new LoanApplication(loanId, "BOR-1", new Money(new BigDecimal("1000000"), "IDR"), Tenor.ONE_MONTH, 700);
        
        // Use updateStatus instead of reflection
        mockLoan.updateStatus(LoanStatus.FUNDING);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(mockLoan));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            approveLoanUseCase.execute(command);
        });
        assertEquals("Hanya loan dengan status PENDING yang dapat diproses", exception.getMessage());
    }
}
