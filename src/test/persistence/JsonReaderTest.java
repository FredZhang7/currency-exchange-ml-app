package persistence;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

// This class is highly influenced by JsonSerializationDemo/blob/master/src/test/persistence/JsonReaderTest.java
public class JsonReaderTest {
    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/___.json");
        try {
            reader.readExchangeHistory();
            fail("Expected IOException");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyHashMap() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyHashMap.json");
        try {
            HashMap<String, Double> map = reader.readHashMap();
            assertEquals(0, map.size());
        } catch (IOException e) {
            fail("Couldn't parse empty HashMap from file");
        }
    }

    @Test
    void testReaderEmptyExchangeHistory() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyExchangeHistory.json");
        try {
            ExchangeHistory excHistory = reader.readExchangeHistory();
            assertEquals(0, excHistory.getHistory().size());
        } catch (IOException e) {
            fail("Couldn't parse empty ExchangeHistory from file");
        }
    }

    @Test
    void testReaderGeneralHashMap() {
        JsonReader reader = new JsonReader("./data/exchange_rates.json");
        try {
            HashMap<String, Double> map = reader.readHashMap();
            assertEquals(31, map.size());
            assertEquals(1.0, map.get("British Pound Sterling (GBP)"));
            assertEquals(0.8639, map.get("Euro (EUR)"));
        } catch (IOException e) {
            fail("Couldn't parse general HashMap from JSON file");
        }
    }

    @Test
    void testReaderGeneralExchangeHistory() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralExchangeHistory.json");
        try {
            ExchangeHistory excHistory = reader.readExchangeHistory();
            ArrayList<Exchange> list = excHistory.getHistory();
            Exchange exc = list.get(0);
            assertEquals(1, list.size());
            assertEquals("British Pound Sterling (GBP)", exc.getFromCurrency());
            assertEquals("United States Dollar (USD)", exc.getToCurrency());
            assertEquals(100.0, exc.getFromValue());
            assertEquals(81.63863163863163, exc.getToValue());
            assertEquals(0.8163863163863163, exc.getExcRates().get("United States Dollar (USD)"));
        } catch (IOException e) {
            fail("Couldn't parse general HashMap from JSON file");
        }
    }
}
