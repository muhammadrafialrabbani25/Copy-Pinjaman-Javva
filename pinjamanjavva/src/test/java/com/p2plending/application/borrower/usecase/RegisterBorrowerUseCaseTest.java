package com.p2plending.application.borrower.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import com.p2plending.application.borrower.dto.BorrowerDTO;
import com.p2plending.application.borrower.dto.RegisterBorrowerCommand;
import com.p2plending.domain.borrower.entity.Borrower;
import com.p2plending.domain.borrower.repository.BorrowerRepository;

import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class RegisterBorrowerUseCaseTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private RegisterBorrowerUseCase registerBorrowerUseCase;

    @Test
    public void testRegisterBorrower() {
        RegisterBorrowerCommand command = 
            new RegisterBorrowerCommand("luis", "1234567890", 5000000, 750);
        
        Borrower borrower = new Borrower("BR001", "luis", "1234567890", 5000000, 750);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(borrower);

        BorrowerDTO result = registerBorrowerUseCase.execute(command);

        assertEquals("luis", result.getName());
        assertEquals(5000000, result.getMonthlySalary());
        assertEquals(750, result.getCreditScore());

        verify(borrowerRepository).save(any(Borrower.class));
    }
}
