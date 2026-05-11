package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetLoanDetailsUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private GetLoanDetailsUseCaseImpl getLoanDetailsUseCase;

    @Test
    public void shouldReturnLoanDetailsWhenLoanExists() {
        LoanApplication loan = new LoanApplication(
            "LN001",
            "BR001",
            new Money(BigDecimal.valueOf(10000000), "IDR"),
            Tenor.TWELVE_MONTHS,
            750
        );

        when(loanRepository.findById("LN001")).thenReturn(Optional.of(loan));

        LoanDTO result = getLoanDetailsUseCase.execute("LN001");

        assertEquals("LN001", result.getId());
        assertEquals("BR001", result.getBorrowerId());
        assertEquals(10000000L, result.getAmount());
        assertEquals(12, result.getTermInMonths());

        verify(loanRepository).findById("LN001");
    }

    @Test
    public void shouldThrowExceptionWhenLoanNotFound() {
        when(loanRepository.findById("LN999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> getLoanDetailsUseCase.execute("LN999")
        );

        assertEquals("Loan tidak ditemukan: LN999", ex.getMessage());

        verify(loanRepository).findById("LN999");
    }
}