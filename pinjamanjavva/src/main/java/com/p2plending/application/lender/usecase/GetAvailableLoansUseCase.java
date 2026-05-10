package com.p2plending.application.lender.usecase;

import com.p2plending.application.lender.dto.AvailableLoanDTO;
import java.util.List;

public interface GetAvailableLoansUseCase {
    List<AvailableLoanDTO> execute();
}