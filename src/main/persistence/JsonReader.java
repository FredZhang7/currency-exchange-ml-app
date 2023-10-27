package persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import model.*;

import org.json.*;

// Represents a reader that reads exchange data from a JSON file
public class JsonReader {
    private String source;

    /**
     * EFFECTS: initializes the location of the Json file in source
     */
    public JsonReader(String source) {
        this.source = source;
    }

    /**
     * EFFECTS: parses and add JSON data to an ExchangeHistory object and returns it
     */
    public ExchangeHistory readExchangeHistory() throws IOException {
        ExchangeHistory exchangeHistory = new ExchangeHistory();

        String jsonString = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonString);

        JSONArray jsonArray = jsonObject.getJSONArray("exchanges");
        for (Object json : jsonArray) {
            // Source: https://stleary.github.io/JSON-java/index.html
            JSONObject exchangeJson = (JSONObject) json;
            Exchange exchange = parseExchange(exchangeJson);
            exchangeHistory.add(exchange);
        }

        return exchangeHistory;
    }

    /**
     * EFFECTS: parses jsonObject into an Exchange object and returns it
     */
    private Exchange parseExchange(JSONObject jsonObject) {
        String fromCurrency = jsonObject.getString("fromCurrency");
        String toCurrency = jsonObject.getString("toCurrency");
        double fromValue = jsonObject.getDouble("fromValue");
        double toValue = jsonObject.getDouble("toValue");

        JSONObject excRatesJson = jsonObject.getJSONObject("excRates");
        HashMap<String, Double> excRates = new HashMap<>();
        jsonToHashMap(excRates, excRatesJson);

        Exchange exchange = new Exchange(fromCurrency, toCurrency, fromValue, excRates);
        exchange.setToValue(toValue);
        return exchange;
    }

    /**
     * REQUIRES: json data contains keys that can be parsed as String
     *           and values that can be parsed as Double
     * EFFECTS: parses JSON data from source into a HashMap and return it
     * throws an IOException if an error occurs when reading data from file
     */
    public HashMap<String, Double> readHashMap() throws IOException {
        String jsonString = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonString);

        HashMap<String, Double> map = new HashMap<>();
        jsonToHashMap(map, jsonObject);

        return map;
    }

    /**
     * MODIFIES: map
     * EFFECTS: iterates over the keys in jsonObject and add the key-value pairs into map
     */
    private void jsonToHashMap(HashMap<String, Double> map, JSONObject jsonObject) {
        // Source: https://stleary.github.io/JSON-java/index.html
        for (String key : jsonObject.keySet()) {
            map.put(key, jsonObject.getDouble(key));
        }
    }

    /**
     * Source: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence
     * EFFECTS: reads source file as string and returns it
     */
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }
}
