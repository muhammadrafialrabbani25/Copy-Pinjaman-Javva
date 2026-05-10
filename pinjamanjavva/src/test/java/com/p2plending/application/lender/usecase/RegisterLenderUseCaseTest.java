package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.RegisterLenderCommand;
import com.p2plending.application.lender.dto.LenderDTO;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.lender.repository.LenderRepository;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterLenderUseCaseTest {

    @Mock
    private LenderRepository lenderRepository;

    @InjectMocks
    private RegisterLenderUseCaseImpl registerLenderUseCase;

    @Test
    public void testRegisterLender() {
        RegisterLenderCommand command = 
            new RegisterLenderCommand("Budi", "08987654321", "3304031709846921", "Jakarta", "Pegawai", BigDecimal.valueOf(50000000));

        KTP ktp = new KTP("Budi", "3304031709846921");
        Money saldo = new Money(BigDecimal.valueOf(50000000), "IDR");
        Lender lender = new Lender("LD001", "Budi", "08987654321", "Jakarta", ktp, "Pegawai", saldo);

        when(lenderRepository.save(any(Lender.class))).thenReturn(lender);

        LenderDTO result = registerLenderUseCase.execute(command);

        assertEquals("Budi", result.getNama());
        assertEquals(BigDecimal.valueOf(50000000), result.getSaldo());
        assertEquals("3304031709846921", result.getNomorKtp());

        verify(lenderRepository).save(any(Lender.class));
    }
}