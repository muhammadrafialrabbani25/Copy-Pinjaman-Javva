package com.p2plending.application.borrower.usecase;

import com.p2plending.application.borrower.dto.LoanDTO;
import java.util.List;

public interface GetLoanListUseCase {
    List<LoanDTO> execute(String borrowerId);
}