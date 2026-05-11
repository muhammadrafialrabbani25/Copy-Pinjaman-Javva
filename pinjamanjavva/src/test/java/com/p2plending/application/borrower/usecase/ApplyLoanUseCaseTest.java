package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.borrower.repository.LoanRepository;
import com.p2plending.domain.borrower.service.LoanApprovalService;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ApplyLoanUseCaseTest {
    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanApprovalService loanApprovalService;

    @InjectMocks
    private ApplyLoanUseCaseImpl applyLoanUseCase;

    @Test
    public void shouldApplyLoanSuccessfully() {
       ApplyLoanCommand command = new ApplyLoanCommand("BR001", 10000000, 12);

        KTP ktp = new KTP("Luis", "3304031709846920");
        Money gaji = new Money(BigDecimal.valueOf(5000000), "IDR");
        Borrower borrower = new Borrower("BR001", "Luis", "08123456789", "batujajar", ktp, gaji, "Pegawai", 750);
        when(borrowerRepository.findById("BR001")).thenReturn(Optional.of(borrower));
        when(loanApprovalService.verifyKTP(any())).thenReturn(true);
        when(loanApprovalService.verifyCreditScore(750)).thenReturn(true);
        when(loanApprovalService.calculateLoanLimit(gaji)).thenReturn(new Money(BigDecimal.valueOf(15000000), "IDR"));

        LoanDTO result = applyLoanUseCase.execute(command);

        assertEquals("BR001", result.getBorrowerId());
        assertEquals(10000000, result.getAmount());
        assertEquals(12, result.getTermInMonths());
        assertNotNull(result.getId());

        verify(borrowerRepository).findById("BR001");
        verify(loanRepository).save(any(LoanApplication.class));
    
    }
}       

