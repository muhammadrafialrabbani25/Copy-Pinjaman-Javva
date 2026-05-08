package com.p2plending.domain.shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class TenorTest {

    @Test // test 1
    void shouldCreateTenorWithOneMonth(){
        Tenor tenor = Tenor.ONE_MONTH;

        assertEquals(1, tenor.getMonths());
    }

    @Test // test 2
    void shouldCreateTenorWithThreeMonths() {
        Tenor tenor = Tenor.THREE_MONTHS;

        assertEquals(3, tenor.getMonths());
    }

    @Test // test 3
    void shouldCreateTenorWithSixMonths() {
        Tenor tenor = Tenor.SIX_MONTHS;

        assertEquals(6, tenor.getMonths());
    }

    @Test // test 4
    void shouldCreateTenorWithTwelveMonths() {
        Tenor tenor = Tenor.TWELVE_MONTHS;

        assertEquals(12, tenor.getMonths());
    }

    @Test // test 5
    void shouldReturnCorrectMonths() {
        Tenor tenor = Tenor.TWELVE_MONTHS;

        assertEquals(12, tenor.getMonths()); 
    }

}
