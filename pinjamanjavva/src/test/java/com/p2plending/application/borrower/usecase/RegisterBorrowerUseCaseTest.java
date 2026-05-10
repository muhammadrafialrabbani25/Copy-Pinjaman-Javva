package com.p2plending.application.borrower.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import com.p2plending.application.borrower.dto.BorrowerDTO;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.borrower.repository.BorrowerRepository;
import com.p2plending.domain.shared.Money;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class RegisterBorrowerUseCaseTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private RegisterBorrowerUseCase registerBorrowerUseCase;

    @Test
    public void testRegisterBorrower() {
        RegisterBorrowerCommand command = 
            new RegisterBorrowerCommand("Luis", "08123456789", "3304012345677890", "batujajar", 5000000, 750);
        
        KTP ktp = new KTP("Luis", "3304031709846920");
        Money gaji = new Money(BigDecimal.valueOf(5000000), "IDR");
        Borrower borrower = new Borrower("BR001", "Luis", "08123456789", "batujajar", ktp, gaji, "Pegawai", 750);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        BorrowerDTO result = registerBorrowerUseCase.execute(command);

        assertEquals("Luis", result.getNama());
        assertEquals(BigDecimal.valueOf(5000000), result.getGaji());
        assertEquals(750, result.getCreditScore());

        verify(borrowerRepository).save(any(Borrower.class));
    }
}
