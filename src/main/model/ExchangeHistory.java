package model;

import java.util.ArrayList;

// Represents a list of exchanges done in the past, with each element being an Exchange object
public class ExchangeHistory {
    private ArrayList<Exchange> history;

    /**
     * MODIFIES: this
     * EFFECTS: initialize history as an empty arraylist
     */
    public ExchangeHistory() {
        history = new ArrayList<>();
    }

    /**
     * REQUIRES: the Exchange object has already completed an exchange
     * MODIFIES: this
     * EFFECTS: add an exchange to history
     */
    public void add(Exchange exc) {
        history.add(exc);
    }

    /**
     * MODIFIES: this
     * EFFECTS: remove all Exchange items from history
     */
    public void clear() {
        history.clear();
    }

    public ArrayList<Exchange> getHistory() {
        return this.history;
    }

    /**
     * EFFECTS: prints the exchange history in console
     */
    public void view() {
        if (history.size() == 0) {
            System.out.println("No exchange was recorded.");
        } else {
            System.out.println("Exchange History:");
            for (Exchange exc : history) {
                System.out.println("----------------------------------------");
                System.out.println("From Currency: " + exc.getFromCurrency());
                System.out.println("From Value: " + exc.getFromValue());
                System.out.println("To Currency: " + exc.getToCurrency());
                System.out.println("To Value: " + exc.getToValue());
                System.out.println("----------------------------------------");
            }
        }
    }
}