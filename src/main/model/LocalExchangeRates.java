package model;

import persistence.JsonReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Represents the currency exchange rates of each exchange
public class LocalExchangeRates {
    private Map<String, Double> excRates;

    /**
     * MODIFIES: this
     * EFFECTS: initializes excRates as a new HashMap,
     *          and loads currency values to excRates
     */
    public LocalExchangeRates() throws IOException {
        excRates = new HashMap<>();
        this.initializeExchangeRates();
    }

    /**
     * MODIFIES: this
     * EFFECTS: loads 31 standardized currency values to excRates
     */
    private void initializeExchangeRates() throws IOException {
        JsonReader jsonReader = new JsonReader("./data/exchange_rates.json");
        excRates = jsonReader.readHashMap();
    }

    public Map<String, Double> getExcRates() {
        return this.excRates;
    }
}
