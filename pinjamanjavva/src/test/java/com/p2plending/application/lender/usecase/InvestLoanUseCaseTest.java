package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.InvestCommand;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.lender.entity.Investment;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.lender.repository.InvestmentRepository;
import com.p2plending.domain.lender.repository.LenderRepository;
import com.p2plending.domain.lender.service.InvestmentService;
import com.p2plending.domain.shared.LoanStatus;
import com.p2plending.domain.shared.Money;
import com.p2plending.domain.shared.Tenor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvestLoanUseCaseTest {

    @Mock
    private LenderRepository lenderRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private InvestmentService investmentService;

    @InjectMocks
    private InvestLoanUseCaseImpl investLoanUseCase;

    @Test
    public void shouldInvestSuccessfully() {
        Lender lender = new Lender(
            "LD001", "Budi", "08987654321", "Jakarta",
            new KTP("Budi", "3304031709846921"), "Pegawai",
            new Money(BigDecimal.valueOf(5000000), "IDR")
        );

        LoanApplication loan = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        loan.updateStatus(LoanStatus.FUNDING);

        InvestCommand command = new InvestCommand("LD001", "LN001", BigDecimal.valueOf(2000000));

        when(lenderRepository.findById("LD001")).thenReturn(Optional.of(lender));
        when(loanRepository.findById("LN001")).thenReturn(Optional.of(loan));
        when(investmentService.validateMinimumInvestment(any(), any())).thenReturn(true);
        when(investmentRepository.findByLoanId("LN001")).thenReturn(java.util.Collections.emptyList());
        when(lenderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        investLoanUseCase.execute(command);

        verify(investmentRepository).save(any(Investment.class));
        verify(loanRepository).save(any(LoanApplication.class));
        verify(lenderRepository).save(any(Lender.class));
    }

    @Test
    public void shouldThrowWhenLenderNotFound() {
        InvestCommand command = new InvestCommand("LD999", "LN001", BigDecimal.valueOf(2000000));
        
        when(lenderRepository.findById("LD999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(command));

        verify(investmentRepository, never()).save(any());
        verify(loanRepository, never()).findById(any());
    }

    @Test
    public void shouldThrowWhenLoanNotFound() {
        Lender lender = new Lender(
            "LD001", "Budi", "08987654321", "Jakarta",
            new KTP("Budi", "3304031709846921"), "Pegawai",
            new Money(BigDecimal.valueOf(5000000), "IDR")
        );

        InvestCommand command = new InvestCommand("LD001", "LN999", BigDecimal.valueOf(2000000));

        when(lenderRepository.findById("LD001")).thenReturn(Optional.of(lender));
        when(loanRepository.findById("LN999")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(command));

        verify(investmentRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenLoanNotFunding() {
        Lender lender = new Lender(
            "LD001", "Budi", "08987654321", "Jakarta",
            new KTP("Budi", "3304031709846921"), "Pegawai",
            new Money(BigDecimal.valueOf(5000000), "IDR")
        );

        LoanApplication loan = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        loan.updateStatus(LoanStatus.PENDING);

        InvestCommand command = new InvestCommand("LD001", "LN001", BigDecimal.valueOf(2000000));

        when(lenderRepository.findById("LD001")).thenReturn(Optional.of(lender));
        when(loanRepository.findById("LN001")).thenReturn(Optional.of(loan));

        assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(command));

        verify(investmentRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenLenderBalanceInsufficient() {
        Lender lender = new Lender(
            "LD001", "Budi", "08987654321", "Jakarta",
            new KTP("Budi", "3304031709846921"), "Pegawai",
            new Money(BigDecimal.valueOf(1000000), "IDR")
        );

        LoanApplication loan = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        loan.updateStatus(LoanStatus.FUNDING);

        InvestCommand command = new InvestCommand("LD001", "LN001", BigDecimal.valueOf(5000000));

        when(lenderRepository.findById("LD001")).thenReturn(Optional.of(lender));
        when(loanRepository.findById("LN001")).thenReturn(Optional.of(loan));

        assertThrows(IllegalArgumentException.class, () -> investLoanUseCase.execute(command));

        verify(investmentRepository, never()).save(any());
    }


}