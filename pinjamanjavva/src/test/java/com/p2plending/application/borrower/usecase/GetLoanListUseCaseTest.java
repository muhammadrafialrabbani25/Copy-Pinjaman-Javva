package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.borrower.entity.LoanApplication;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetLoanListUseCaseTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private GetLoanListUseCaseImpl getLoanListUseCase;

    @Test
    public void shouldReturnLoanListForBorrower() {
        Borrower borrower = new Borrower(
            "BR001", "Budi", "08123456789", "Jakarta",
            new KTP("Budi", "3304031709846920"),
            new Money(BigDecimal.valueOf(5000000), "IDR"),
            "Pegawai", 750
        );
        borrower.setCancellationCount(2);

        LoanApplication loan1 = new LoanApplication(
            "LN001", "BR001", new Money(BigDecimal.valueOf(10000000), "IDR"), Tenor.TWELVE_MONTHS, 750
        );
        LoanApplication loan2 = new LoanApplication(
            "LN002", "BR001", new Money(BigDecimal.valueOf(5000000), "IDR"), Tenor.SIX_MONTHS, 750
        );

        when(borrowerRepository.findById("BR001")).thenReturn(Optional.of(borrower));
        when(loanRepository.findByBorrowerId("BR001")).thenReturn(Arrays.asList(loan1, loan2));

        List<LoanDTO> result = getLoanListUseCase.execute("BR001");

        assertEquals(2, result.size());
        assertEquals("LN001", result.get(0).getId());
        assertEquals(2, result.get(0).getCancellationCount());
        assertEquals("LN002", result.get(1).getId());
        assertEquals(2, result.get(1).getCancellationCount());
        verify(borrowerRepository).findById("BR001");
        verify(loanRepository).findByBorrowerId("BR001");
    }

    @Test
    public void shouldReturnEmptyListWhenBorrowerHasNoLoan() {
        Borrower borrower = new Borrower(
            "BR999", "Andi", "08123456788", "Bandung",
            new KTP("Andi", "3304031709846921"),
            new Money(BigDecimal.valueOf(4000000), "IDR"),
            "Karyawan", 700
        );

        when(borrowerRepository.findById("BR999")).thenReturn(Optional.of(borrower));
        when(loanRepository.findByBorrowerId("BR999")).thenReturn(Collections.emptyList());

        List<LoanDTO> result = getLoanListUseCase.execute("BR999");

        assertEquals(0, result.size());
        verify(borrowerRepository).findById("BR999");
        verify(loanRepository).findByBorrowerId("BR999");
    }


}