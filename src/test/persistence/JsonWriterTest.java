package persistence;


import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// This class is highly influenced by JsonSerializationDemo/blob/master/src/test/persistence/JsonWriterTest.java
public class JsonWriterTest {
    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyExchangeHistory() {
        try {
            ExchangeHistory exchangeHistory = new ExchangeHistory();
            String path = "./data/testWriterEmptyExchangeHistory.json";
            JsonWriter writer = new JsonWriter(path);
            writer.open();
            writer.write(exchangeHistory);
            writer.close();

            JsonReader reader = new JsonReader(path);
            exchangeHistory = reader.readExchangeHistory();
            assertEquals(0, exchangeHistory.getHistory().size());
        } catch (IOException e) {
            fail("Unexpected IOException when parsing an empty exchange history object from JSON");
        }
    }

    @Test
    void testWriterGeneralExchangeHistory() {
        try {
            ExchangeHistory exchangeHistory = new ExchangeHistory();
            LocalExchangeRates rates = new LocalExchangeRates();
            String fromCurrency = "Canadian Dollar (CAD)";
            String toCurrency = "Chinese Yuan (CNY)";
            double fromValue = 100;
            Exchange exc1 = new Exchange(fromCurrency, toCurrency, fromValue, rates.getExcRates());
            exc1.exchange();
            double toValue = exc1.getToValue();
            exchangeHistory.add(exc1);

            String path = "./data/testWriterGeneralExchangeHistory.json";
            JsonWriter writer = new JsonWriter(path);
            writer.open();
            writer.write(exchangeHistory);
            writer.close();

            JsonReader reader = new JsonReader(path);
            exchangeHistory = reader.readExchangeHistory();
            assertEquals(1, exchangeHistory.getHistory().size());
            Exchange readExc1 = exchangeHistory.getHistory().get(0);
            assertEquals(fromCurrency, readExc1.getFromCurrency());
            assertEquals(toCurrency, readExc1.getToCurrency());
            assertEquals(fromValue, readExc1.getFromValue());
            assertEquals(toValue, readExc1.getToValue());
            assertEquals(0.022232802326479142, readExc1.getExcRates().get("Thai Baht (THB)"));
        } catch (IOException e) {
            fail("Unexpected IOException when parsing a general ExchangeHistory object from JSON");
        }
    }
}
