package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.ApplyLoanCommand;
import com.p2plending.application.borrower.dto.*;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand; 
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class ApplyLoanUseCaseTest {
    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private  

    @Test
    public void shouldApplyLoanSuccessfully() {
        ApplyLoanCommand command = new ApplyLoanCommand("BR001", 10000000, 12);
        Borrower borrower = new Borrower("BR001", "luis", "1234567890", 5000000, 750);
        when(borrowerRepository.findById("BR001")).thenReturn(java.util.Optional.of(borrower));

        LoanDTO result = applyLoanUseCase.execute(command);

        assertEquals("BR001", result.getBorrowerId());
        assertEquals(10000000, result.getAmount());
        assertEquals(12, result.getTermInMonths());
        assertEquals("BR001", result.getId());
        verify(borrowerRepository).findById("BR001");
        verify(loanRepository).save(any());
    }
}       

