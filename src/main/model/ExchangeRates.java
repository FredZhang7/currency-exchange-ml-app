package model;

import persistence.JsonReader;

import java.io.IOException;
import java.util.HashMap;

// Represents the currency exchange rates of each exchange
public class ExchangeRates {
    private HashMap<String, Double> excRates;

    /**
     * MODIFIES: this
     * EFFECTS: initializes excRates as a new HashMap,
     *          and loads currency values to excRates
     */
    public ExchangeRates() throws IOException {
        excRates = new HashMap<>();
        initializeExchangeRates();
    }

    /**
     * MODIFIES: this
     * EFFECTS: loads 31 standardized currency values to excRates
     */
    @SuppressWarnings("methodlength")
    private void initializeExchangeRates() throws IOException {
        JsonReader jsonReader = new JsonReader("./data/exchange_rates.json");
        excRates = jsonReader.readHashMap();
    }

    public HashMap<String, Double> getExcRates() {
        return this.excRates;
    }
}
