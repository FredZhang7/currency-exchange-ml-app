package model;

import java.util.Map;

// Represents an exchange from one currency to another, containing currency names and exchanged values
public class Exchange {
    private String fromCurrency;
    private String toCurrency;
    private double fromValue;
    private double toValue;
    private Map<String, Double> excRates;

    /**
     * REQUIRES: fromCurrency and toCurrency are valid key values of excRates
     * MODIFIES: this
     * EFFECTS: initializes all fields
     */
    public Exchange(String fromCurrency, String toCurrency, double fromValue, Map<String, Double> excRates) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromValue = fromValue;
        this.excRates = excRates;
    }

    /**
     * MODIFIES: this
     * EFFECTS: returns a double representing the value of the converted currency,
     *          and this value overrides the current toValue field
     */
    public double exchange() {
        double fromRate = excRates.get(this.fromCurrency);
        double toRate = excRates.get(this.toCurrency);
        double converted = fromRate / toRate * this.fromValue;
        this.toValue = converted;
        return converted;
    }

    public String getFromCurrency() {
        return this.fromCurrency;
    }

    public String getToCurrency() {
        return this.toCurrency;
    }

    public double getFromValue() {
        return this.fromValue;
    }

    public double getToValue() {
        return this.toValue;
    }

    public Map<String, Double> getExcRates() {
        return this.excRates;
    }

    public void setToValue(double toValue) {
        this.toValue = toValue;
    }
}