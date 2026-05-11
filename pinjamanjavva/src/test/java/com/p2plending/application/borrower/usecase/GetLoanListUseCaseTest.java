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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetLoanListUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private GetLoanListUseCaseImpl getLoanListUseCase;

    @Test
    public void shouldReturnLoanListForBorrower() {
        LoanApplication loan1 = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        LoanApplication loan2 = new LoanApplication(
            "LN002", "BR001", new Money(BigDecimal.valueOf(5000000), "IDR"), Tenor.SIX_MONTHS, 750
        );

        when(loanRepository.findByBorrowerId("BR001")).thenReturn(Arrays.asList(loan1, loan2));

        List<LoanDTO> result = getLoanListUseCase.execute("BR001");

        assertEquals(2, result.size());
        assertEquals("LN001", result.get(0).getId());
        assertEquals("LN002", result.get(1).getId());
        verify(loanRepository).findByBorrowerId("BR001");
    }

    @Test
    public void shouldReturnEmptyListWhenBorrowerHasNoLoan() {
        when(loanRepository.findByBorrowerId("BR999")).thenReturn(Collections.emptyList());

        List<LoanDTO> result = getLoanListUseCase.execute("BR999");

        assertEquals(0, result.size());
        verify(loanRepository).findByBorrowerId("BR999");
    }
}