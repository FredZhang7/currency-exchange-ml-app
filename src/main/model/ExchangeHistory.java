package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

// Represents a list of exchanges done in the past, with each element being an Exchange object
public class ExchangeHistory {
    private ArrayList<Exchange> history;

    /**
     * MODIFIES: this
     * EFFECTS: initializes history as an empty arraylist
     */
    public ExchangeHistory() {
        history = new ArrayList<>();
    }

    /**
     * REQUIRES: the Exchange object has already completed an exchange
     * MODIFIES: this
     * EFFECTS: adds an exchange to history
     */
    public void add(Exchange exc) {
        history.add(exc);
    }

    /**
     * MODIFIES: this
     * EFFECTS: removes all Exchange items from history
     */
    public void clear() {
        history.clear();
    }

    /**
     * EFFECTS: adds exchanges to a JSONObject and returns it
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("exchanges", exchangesToJsonArray());
        return json;
    }

    /**
     * EFFECTS: parses the list of exchange objects in history to a JSONArray of JSONObjects and returns it
     */
    private JSONArray exchangesToJsonArray() {
        JSONArray jsonArray = new JSONArray();

        for (Exchange exc : history) {
            JSONObject exchangeJson = new JSONObject();
            exchangeJson.put("fromCurrency", exc.getFromCurrency());
            exchangeJson.put("toCurrency", exc.getToCurrency());
            exchangeJson.put("fromValue", exc.getFromValue());
            exchangeJson.put("toValue", exc.getToValue());
            exchangeJson.put("excRates", exc.getExcRates());
            jsonArray.put(exchangeJson);
        }

        return jsonArray;
    }

    public ArrayList<Exchange> getHistory() {
        return this.history;
    }
}