package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.CancelLoanCommand;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.service.LoanCancellationService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelLoanUseCaseTest {
    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanCancellationService loanCancellationService;

    @InjectMocks
    private CancelLoanUseCaseImpl cancelLoanUseCase;

    @Test
    public void shouldCancelLoanSuccessfully() {
        CancelLoanCommand command = new CancelLoanCommand("BR001", "LN001", new Money(BigDecimal.valueOf(2000000), "IDR"));

        KTP ktp = new KTP("Luis", "3304031709846920");
        Money gaji = new Money(BigDecimal.valueOf(5000000), "IDR");
        Borrower borrower = new Borrower("BR001", "Luis", "08123456789", "batujajar", ktp, gaji, "Pegawai", 750);
        
        LoanApplication loan = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        setLoanStatus(loan,LoanStatus.FUNDING);
        
        when(borrowerRepository.findById("BR001")).thenReturn(Optional.of(borrower));
        when(loanRepository.findById("LN001")).thenReturn(Optional.of(loan));
        when(loanCancellationService.canCancelLoan(any(Money.class), any(Money.class), anyInt())).thenReturn(true);
        when(loanCancellationService.incrementCancellationCount(anyInt())).thenReturn(1);

        cancelLoanUseCase.execute(command);

        ArgumentCaptor<Borrower> borrowerCaptor = ArgumentCaptor.forClass(Borrower.class);
        ArgumentCaptor<LoanApplication> loanCaptor = ArgumentCaptor.forClass(LoanApplication.class);

        verify(borrowerRepository).save(borrowerCaptor.capture());
        verify(loanRepository).save(loanCaptor.capture());

        assertEquals(1, borrowerCaptor.getValue().getCancellationCount());
        assertEquals(LoanStatus.CANCELLED, loanCaptor.getValue().getStatus());
        assertNotNull(loanCaptor.getValue().getCancelledDate());    
}
    @Test
    void shouldRejectCancellationWhenLoanCannotBeCancelled() {
        CancelLoanCommand command = new CancelLoanCommand(
    "BR001",
    "LN001",
    new Money(BigDecimal.valueOf(2000000), "IDR")
);

        KTP ktp = new KTP("Luis", "3304031709846920");
        Money gaji = new Money(BigDecimal.valueOf(5000000), "IDR");
        Borrower borrower = new Borrower("BR001", "Luis", "08123456789", "batujajar", ktp, gaji, "Pegawai", 750);
        
        LoanApplication loan = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        setLoanStatus(loan,LoanStatus.FUNDING);

        when(borrowerRepository.findById("BR001")).thenReturn(Optional.of(borrower));
        when(loanRepository.findById("LN001")).thenReturn(Optional.of(loan));
        when(loanCancellationService.canCancelLoan(any(Money.class), any(Money.class), anyInt())).thenReturn(false);

       assertThrows(IllegalArgumentException.class, () -> {
            cancelLoanUseCase.execute(command);
        });

        verify(borrowerRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }
    private void setLoanStatus(LoanApplication loan, LoanStatus status) {
    try {
        Field statusField = LoanApplication.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(loan, status);
    } catch (Exception e) {
        throw new RuntimeException("Gagal set status", e);
        }
    }
}