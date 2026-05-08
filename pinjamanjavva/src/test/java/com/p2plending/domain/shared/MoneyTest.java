package com.p2plending.domain.shared;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test // test 1
    void ShouldMakeMoneyWithValidAmount() {
        BigDecimal amount = new BigDecimal("50000");
        String currency = "IDR";

        // when
        Money money = new Money(amount, currency);

        // then
        assertEquals(amount, money.getAmount());
        assertEquals(currency, money.getCurrency());
    }

    @Test // test 2
    void shouldThrowExceptionWhenAmountIsNegative() {
        BigDecimal amount = new BigDecimal("-10000");
        String currency = "IDR";

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(new BigDecimal("-10000"), "IDR");
        });
    }

    @Test // test 3
    void shouldReturnNewMoneyWhenAdded() {
        BigDecimal amount1 = new BigDecimal("2000");
        BigDecimal amount2 = new BigDecimal("3000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        Money result = money1.add(money2);

        assertEquals(new BigDecimal("5000"), result.getAmount());
        assertEquals(currency, result.getCurrency());
    }

    @Test // test 4
    void shouldReturnNewWhenSubtracted() {
        BigDecimal amount1 = new BigDecimal("5000");
        BigDecimal amount2 = new BigDecimal("2000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        Money result = money1.subtract(money2);

        assertEquals(new BigDecimal("3000"), result.getAmount());
        assertEquals(currency, result.getCurrency());
    }

    @Test // test 5
    void shouldThrowExceptionWhenSubtractResultIsNegative() {
        BigDecimal amount1 = new BigDecimal("1000");
        BigDecimal amount2 = new BigDecimal("2000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        assertThrows(IllegalArgumentException.class, () -> {
            Money result = money1.subtract(money2);
        });
    }

    @Test // test 6
    void shouldReturnTrueWhenMoneyIsGreaterThanOrEqual(){
        BigDecimal amount1 = new BigDecimal("5000");
        BigDecimal amount2 = new BigDecimal("2000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        assertTrue(money1.isGreaterThanOrEqual(money2));
    }

    @Test // test 7
    void shouldReturnFalseWhenMoneyIsGreaterThanOrEqual() {
        BigDecimal amount1 = new BigDecimal("5000");
        BigDecimal amount2 = new BigDecimal("2000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        assertFalse(money2.isGreaterThanOrEqual(money1));
    }

    @Test // test 8
    void shouldBeEqualWhenAmountAndCurrencyAreSame(){
        BigDecimal amount1 = new BigDecimal("5000");
        BigDecimal amount2 = new BigDecimal("5000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        assertEquals(money1, money2);   
    }

    @Test // test 9
    void shouldNotBeEqualWhenAmountIsDifferent(){
        BigDecimal amount1 = new BigDecimal("4000");
        BigDecimal amount2 = new BigDecimal("5000");
        String currency = "IDR";

        Money money1 = new Money(amount1, currency);
        Money money2 = new Money(amount2, currency);

        assertNotEquals(money1, money2);
    }

    @Test // test 10
    void shouldNotChangeOriginalWhenAdded(){
        BigDecimal amount = new BigDecimal("5000");
        BigDecimal add = new BigDecimal("2000");
        String currency = "IDR";

        Money money = new Money(amount, currency);
        Money addMoney = new Money(add, currency);

        Money result = money.add(addMoney);

        assertEquals(new BigDecimal("5000"), money.getAmount());
        assertEquals(new BigDecimal("7000"), result.getAmount());

    }

    @Test // test 11
    void shouldThrowExceptionWhenCurrencyIsNull(){
        BigDecimal amount = new BigDecimal("10000");
        String currency = null;

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(new BigDecimal("10000"), null);
        });

    }

}
