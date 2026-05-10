package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.LenderDTO;
import com.p2plending.domain.borrower.entity.KTP;
import com.p2plending.domain.lender.entity.Lender;
import com.p2plending.domain.lender.repository.LenderRepository;
import com.p2plending.domain.shared.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopUpSaldoUseCaseTest {

    @Mock
    private LenderRepository lenderRepository;

    @InjectMocks
    private TopUpSaldoUseCaseImpl topUpSaldoUseCase;

    @Test
    public void shouldTopUpSaldoWith2PercentAdminFee() {
        Lender lender = new Lender(
            "LD001",
            "Budi",
            "08987654321",
            "Jakarta",
            new KTP("Budi", "3304031709846921"),
            "Pegawai",
            new Money(new BigDecimal("1000000"), "IDR")
        );

        when(lenderRepository.findById("LD001")).thenReturn(Optional.of(lender));
        when(lenderRepository.save(any(Lender.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LenderDTO result = topUpSaldoUseCase.execute("LD001", new BigDecimal("500000"));

        ArgumentCaptor<Lender> captor = ArgumentCaptor.forClass(Lender.class);
        verify(lenderRepository).save(captor.capture());

        assertEquals(new BigDecimal("1490000.00"), captor.getValue().getSaldo().getAmount());
        assertEquals(new BigDecimal("1490000.00"), result.getSaldo());
    }

    @Test
    public void shouldThrowWhenLenderNotFound() {
        when(lenderRepository.findById("LD999")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> topUpSaldoUseCase.execute("LD999", new BigDecimal("100000"))
        );

        assertEquals("Lender tidak ditemukan: LD999", ex.getMessage());
        verify(lenderRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAmountInvalid() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> topUpSaldoUseCase.execute("LD001", BigDecimal.ZERO)
        );

        assertEquals("Nominal top up harus lebih dari 0", ex.getMessage());
        verify(lenderRepository, never()).findById(any());
        verify(lenderRepository, never()).save(any());
    }
}