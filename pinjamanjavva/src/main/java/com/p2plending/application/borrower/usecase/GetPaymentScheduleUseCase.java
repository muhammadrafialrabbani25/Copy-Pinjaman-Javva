package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.PaymentDTO;
import java.util.List;

public interface GetPaymentScheduleUseCase {
    List<PaymentDTO> execute(String borrowerId, String loanId);
}
