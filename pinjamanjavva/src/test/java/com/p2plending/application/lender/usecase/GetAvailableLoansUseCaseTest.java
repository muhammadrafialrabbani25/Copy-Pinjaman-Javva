package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.AvailableLoanDTO;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAvailableLoansUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private GetAvailableLoansUseCaseImpl getAvailableLoansUseCase;

    @Test
    public void shouldReturnOnlyFundingLoans() {
        LoanApplication fundingLoan = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        
        LoanApplication pendingLoan = new LoanApplication(
            "LN002", "BR002", new Money(BigDecimal.valueOf(5000000), "IDR"), Tenor.SIX_MONTHS, 700
        );
        setLoanStatus(fundingLoan, LoanStatus.FUNDING);
        setLoanStatus(pendingLoan, LoanStatus.PENDING);

        when(loanRepository.findAll()).thenReturn(Arrays.asList(fundingLoan, pendingLoan));

        List<AvailableLoanDTO> result = getAvailableLoansUseCase.execute();

        assertEquals(1, result.size());
        assertEquals("LN001", result.get(0).getLoanId());
        assertEquals(new BigDecimal("2000000.00"), result.get(0).getMinimumInvestmentAmount());

        verify(loanRepository).findAll();
    }

    @Test
    public void shouldReturnEmptyWhenNoFundingLoans() {
        when(loanRepository.findAll()).thenReturn(Collections.emptyList());

        List<AvailableLoanDTO> result = getAvailableLoansUseCase.execute();

        assertEquals(0, result.size());
        verify(loanRepository).findAll();
    }

    private void setLoanStatus(LoanApplication loan, LoanStatus status) {
        try {
            Field field = LoanApplication.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(loan, status);
        } catch (Exception e) {
            throw new RuntimeException("Gagal set status", e);
        }
    }
}