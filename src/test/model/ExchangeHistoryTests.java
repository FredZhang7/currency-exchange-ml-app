package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeHistoryTests {
    private Exchange exc1;
    private Exchange exc2;
    private Exchange exc3;
    private ExchangeHistory excHistory;
    // Source: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html
    private HashMap<String, Double> excRates;

    @BeforeEach
    public void initialize() {
        excRates = new ExchangeRates().getExcRates();
        exc1 = new Exchange("British Pound Sterling (GBP)","United States Dollar (USD)", 0, excRates);
        exc2 = new Exchange("Indonesian Rupiah (IDR)", "British Pound Sterling (GBP)", 1000, excRates);
        exc3 = new Exchange("British Pound Sterling (GBP)","United States Dollar (USD)", 1, excRates);
        exchangeTests();
        excHistory = new ExchangeHistory();
    }

    @Test
    public void exchangeTests() {
        double amount1 = exc1.exchange();
        assertEquals(0, amount1);
        assertEquals(0, exc1.getToValue());
        double amount2 = exc2.exchange();
        assertEquals(5.190225146067078e-05 / 1.0 * 1000, amount2);
        assertEquals(5.190225146067078e-05 / 1.0 * 1000, exc2.getToValue());
        double amount3 = exc3.exchange();
        assertEquals(1.0 / 0.8163863163863163, amount3);
        assertEquals(1.0 / 0.8163863163863163, exc3.getToValue());
    }

    @Test
    public void addExchangeTest() {
        excHistory.add(exc1);
        assertEquals(exc1, excHistory.getHistory().get(0));
        excHistory.add(exc2);
        assertEquals(exc2, excHistory.getHistory().get(1));
        excHistory.add(exc3);
        assertEquals(exc3, excHistory.getHistory().get(2));
    }

    @Test
    public void clearHistoryTest() {
        assertEquals(0, excHistory.getHistory().size());
        excHistory.clear();
        assertEquals(0, excHistory.getHistory().size());

        excHistory.add(exc1);
        excHistory.add(exc2);
        assertEquals(2, excHistory.getHistory().size());

        excHistory.clear();
        assertEquals(0, excHistory.getHistory().size());
    }

    @Test
    public void viewHistoryTest() {
        excHistory.view();
        assertEquals(0, excHistory.getHistory().size());

        excHistory.add(exc1);
        excHistory.add(exc2);
        excHistory.view();
        assertEquals(2, excHistory.getHistory().size());
    }
}