package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.MakePaymentCommand;
import com.p2plending.application.borrower.dto.PaymentDTO;

public interface MakePaymentUseCase {
    PaymentDTO execute(MakePaymentCommand command);
}
