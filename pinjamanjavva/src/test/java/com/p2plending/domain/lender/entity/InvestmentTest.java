package com.p2plending.domain.lender.entity;

import com.p2plending.domain.shared.Money;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;


public class InvestmentTest {
    @Test // test 1
    void shouldCreateInvestmentWithValidData(){
        String id = "I001";
        String lenderId = "L001";
        String loanId = "P001";
        Money amount = new Money(new BigDecimal("6000000"), "IDR");

        Investment investment = new Investment(id, lenderId, loanId, amount);

        assertEquals(id, investment.getId());
        assertEquals(lenderId, investment.getLenderId());
        assertEquals(loanId, investment.getLoanId());
        assertEquals(amount, investment.getAmount());

    }
    
    @Test // test 2
    void shouldHaveActiveStatusWhenCreated(){
        String id = "I001";
        String lenderId = "L001";
        String loanId = "P001";
        Money amount = new Money(new BigDecimal("6000000"), "IDR");

        Investment investment = new Investment(id, lenderId, loanId, amount);

        assertEquals(id, investment.getId());
        assertEquals(lenderId, investment.getLenderId());
        assertEquals(loanId, investment.getLoanId());
        assertEquals(amount, investment.getAmount());
        assertEquals("ACTIVE", investment.getStatus().name());
        
        
    }
    
    @Test // test 3
    void shouldThrowExceptionWhenAmountIsNull(){
        String id = "I001";
        String lenderId = "L001";
        String loanId = "P001";
        Money amount = null;

        assertThrows(IllegalArgumentException.class, () -> {
            Investment investment = new Investment(id, lenderId, loanId, amount);
        });

        
    }
}
