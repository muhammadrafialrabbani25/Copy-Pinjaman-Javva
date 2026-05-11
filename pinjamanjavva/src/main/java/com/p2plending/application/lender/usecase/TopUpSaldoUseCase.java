package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.LenderDTO;
import java.math.BigDecimal;

public interface TopUpSaldoUseCase {
    LenderDTO execute(String lenderId, BigDecimal amount);
}