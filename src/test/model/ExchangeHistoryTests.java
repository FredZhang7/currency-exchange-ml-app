package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeHistoryTests {
    private Exchange exc1;
    private Exchange exc2;
    private Exchange exc3;
    private ExchangeHistory excHistory;
    // Source: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html
    private Map<String, Double> excRates;

    @BeforeEach
    public void initialize() throws IOException {
        excRates = new LocalExchangeRates().getExcRates();
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
        assertEquals(exc1.getFromCurrency(), "British Pound Sterling (GBP)");
        assertEquals(exc1.getToCurrency(), "United States Dollar (USD)");
        assertEquals(exc1.getFromValue(), 0);

        double amount2 = exc2.exchange();
        assertEquals(5.190225146067078e-05 / 1.0 * 1000, amount2);
        assertEquals(5.190225146067078e-05 / 1.0 * 1000, exc2.getToValue());

        double amount3 = exc3.exchange();
        assertEquals(1.0 / 0.8163863163863163, amount3);
        assertEquals(1.0 / 0.8163863163863163, exc3.getToValue());

        exc3.setToValue(100);
        assertEquals(100, exc3.getToValue());
        assertEquals(excRates, exc3.getExcRates());
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
    public void setHistoryTest() {
        ExchangeHistory tmpHistory = new ExchangeHistory();
        excHistory.setHistory(tmpHistory.getHistory());
        assertEquals(tmpHistory.getHistory(), excHistory.getHistory());
    }
}