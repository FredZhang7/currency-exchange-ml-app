package model;

import java.util.HashMap;

public class ExchangeRates {
    private HashMap<String, Double> excRates;

    public ExchangeRates() {
        excRates = new HashMap<>();
        initializeExchangeRates();
    }

    /**
     * MODIFIES: this
     * EFFECTS: 31 standardized currency values are added to rates
     */
    @SuppressWarnings("methodlength")
    private void initializeExchangeRates() {
        excRates.put("British Pound Sterling (GBP)", 1.0);
        excRates.put("United States Dollar (USD)", 0.8163863163863163);
        excRates.put("Japanese Yen (JPY)", 0.005477775664193774);
        excRates.put("Bulgarian Lev (BGN)", 0.441711831475611);
        excRates.put("Czech Koruna (CZK)", 0.03516649027110641);
        excRates.put("Danish Krone (DKK)", 0.11586020063301324);
        excRates.put("Hungarian Forint (HUF)", 0.002230109969538954);
        excRates.put("Polish Zloty (PLN)", 0.1895972786129705);
        excRates.put("Romanian Leu (RON)", 0.1739839690659363);
        excRates.put("Swedish Krona (SEK)", 0.07447092797724236);
        excRates.put("Swiss Franc (CHF)", 0.9009281468349151);
        excRates.put("Icelandic Krona (ISK)", 0.005904989747095009);
        excRates.put("Norwegian Krone (NOK)", 0.07514460922889575);
        excRates.put("Turkish Lira (TRY)", 0.029445146953062953);
        excRates.put("Australian Dollar (AUD)", 0.5223727173781594);
        excRates.put("Brazilian Real (BRL)", 0.1593558621707371);
        excRates.put("Canadian Dollar (CAD)", 0.6004726489191631);
        excRates.put("Chinese Yuan (CNY)", 0.11184908982625134);
        excRates.put("Hong Kong Dollar (HKD)", 0.104406361790583);
        excRates.put("Indonesian Rupiah (IDR)", 5.190225146067078e-05);
        excRates.put("Israeli New Shekel (ILS)", 0.2067290435282012);
        excRates.put("Indian Rupee (INR)", 0.00980762790275247);
        excRates.put("South Korean Won (KRW)", 0.0006051414962174278);
        excRates.put("Mexican Peso (MXN)", 0.04487186146286735);
        excRates.put("Malaysian Ringgit (MYR)", 0.17259704713004215);
        excRates.put("New Zealand Dollar (NZD)", 0.49051782875312283);
        excRates.put("Philippine Peso (PHP)", 0.01436290483474097);
        excRates.put("Singapore Dollar (SGD)", 0.5974412171507607);
        excRates.put("Thai Baht (THB)", 0.022232802326479142);
        excRates.put("South African Rand (ZAR)", 0.04260345108173018);
        excRates.put("Euro (EUR)", 0.8639);
    }

    public HashMap<String, Double> getExcRates() {
        return this.excRates;
    }
}
