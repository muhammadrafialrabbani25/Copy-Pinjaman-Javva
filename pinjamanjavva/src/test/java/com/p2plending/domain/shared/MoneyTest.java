package com.p2plending.domain.shared;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    void ShouldMakeMoneyWithValidAmount(){
        BigDecimal amount = new BigDecimal("50000");
        String currency = "IDR";

        //when 
        Money money = new Money(amount, currency);

        //then
        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative(){
        BigDecimal amount = new BigDecimal("-10000");
        String currency = "IDR";

        // then 
        assertThrows(IllegalArgumentException.class, () ->{
            new Money(new BigDecimal("-10000"), "IDR");
        });
    }

    @Test
    void shouldReturnNewMoneyWhenAdded(){
        BigDecimal amount1 = new BigDecimal("2000");
        BigDecimal amount2 = new BigDecimal("3000");
        String currency = "IDR";


        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        Money result = money1.add(money2);

        assertEquals(new BigDecimal("5000"), result.getAmount());
        assertEquals(currency, result.getCurrency());
    }

    @Test
    void shouldReturnNewWhenSubtracted(){
        BigDecimal amount1 = new BigDecimal("5000");
        BigDecimal amount2 = new BigDecimal("2000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        Money result = money1.subtract(money2);

        assertEquals(new BigDecimal("3000"), result.getAmount());
        assertEquals(currency, result.getCurrency());
    }

    @Test
    void shouldThrowExceptionWhenSubtractResultIsNegative(){
        BigDecimal amount1 = new BigDecimal("1000");
        BigDecimal amount2 = new BigDecimal("2000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        
        assertThrows(IllegalArgumentException.class, () -> {
            Money result = money1.subtract(money2);
        });
    }


}
