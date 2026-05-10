package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.RegisterLenderCommand;
import com.p2plending.application.lender.dto.LenderDTO;

public interface RegisterLenderUseCase {
    LenderDTO execute(RegisterLenderCommand command);
}