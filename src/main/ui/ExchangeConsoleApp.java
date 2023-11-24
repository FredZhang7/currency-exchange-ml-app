package ui;

import model.Exchange;
import model.ExchangeHistory;
import model.LocalExchangeRates;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

// Represents an app for currency exchanges in the console
public class ExchangeConsoleApp {
    private Scanner scanner;
    private ExchangeHistory excHistory;
    private Map<String, Double> excRates;
    private JsonReader reader;
    private JsonWriter writer;

    /**
     * EFFECTS: initializes a new ExchangeConsoleApp with a Scanner for user input, an empty ExchangeHistory,
     *          and a HashMap of exchange rates; starts a new round and waits for console commands
     */
    public ExchangeConsoleApp() throws IOException {
        // Source: https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
        scanner = new Scanner(System.in);

        String historyPath = "./data/exchange_history.json";
        reader = new JsonReader(historyPath);
        writer = new JsonWriter(historyPath);

        excHistory = new ExchangeHistory();
        excRates = new LocalExchangeRates().getExcRates();

        roundStart();
        runCommandListener();
    }

    /**
     * EFFECTS: listens for console commands and calls the corresponding method;
     *          if the command is unrecognized, it calls slashUnrecognized()
     */
    private void runCommandListener() throws IOException {
        while (true) {
            // Source: https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
            String command = scanner.nextLine();

            if (command.equals("/load")) {
                load();
            } else if (command.equals("/start")) {
                slashStart();
            } else if (command.equals("/history")) {
                slashHistory();
            } else if (command.equals("/clear")) {
                slashClear();
            } else if (command.equals("/end")) {
                slashEnd();
            } else if (command.length() != 0) {
                slashUnrecognized();
            }
        }
    }

    /**
     * EFFECTS: prints the available commands to the console.
     */
    private void roundStart() {
        System.out.println("Use the command /load to load the previously saved exchange history, \n"
                + "/start to start a transaction, \n"
                + "/history to view the history of transactions, \n"
                + "/clear to clear the history of transactions, \n"
                + "/end to save the history and stop the app");
    }

    /**
     * MODIFIES: this
     * EFFECTS: loads the previously saved exchange history into excHistory
     */
    private void load() throws IOException {
        excHistory = reader.readExchangeHistory();
    }

    /**
     * MODIFIES: this
     * EFFECTS: starts a new currency exchange transaction from console input,
     *          calculates the conversion, and adds it to the exchange history
     */
    private void slashStart() {
        // Source: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html
        HashMap<Integer, String> keyCurrencyPair = new HashMap<>();
        int counter = 1;
        for (String currency : excRates.keySet()) {
            System.out.println(currency + ": [" + counter + "]");
            keyCurrencyPair.put(counter, currency);
            counter++;
        }

        System.out.print("\nEnter the corresponding integer of a currency to convert from: ");
        String fromCurrency = keyCurrencyPair.get(scanner.nextInt());

        System.out.print("\nEnter the corresponding integer of a currency to convert to: ");
        String toCurrency = keyCurrencyPair.get(scanner.nextInt());

        System.out.print("\nEnter the amount of money to convert: ");
        double amount = scanner.nextDouble();

        Exchange exc = new Exchange(fromCurrency, toCurrency, amount, excRates);
        exc.exchange();
        System.out.println("\nConverted amount: " + exc.getToValue());
        excHistory.add(exc);
    }

    /**
     * EFFECTS: prints the exchange history in console
     */
    private void slashHistory() {
        if (excHistory.getHistory().size() == 0) {
            System.out.println("No exchange was recorded.");
        } else {
            System.out.println("Exchange History:");
            for (Exchange exc : excHistory.getHistory()) {
                System.out.println("----------------------------------------");
                System.out.println("From Currency: " + exc.getFromCurrency());
                System.out.println("From Value: " + exc.getFromValue());
                System.out.println("To Currency: " + exc.getToCurrency());
                System.out.println("To Value: " + exc.getToValue());
                System.out.println("----------------------------------------");
            }
        }
    }

    /**
     * MODIFIES: this
     * EFFECTS: clears the exchange history
     */
    private void slashClear() {
        excHistory.clear();
    }

    /**
     * EFFECTS: saves the current ExchangeHistory to historyPath,
     *          prints a message indicating that the app has stopped,
     *          and lastly, exits the program
     */
    private void slashEnd() throws IOException {
        writer.open();
        writer.write(excHistory);
        writer.close();
        System.out.println("You stopped the app. Re-run the code to restart.");
        System.exit(0);
    }

    /**
     * EFFECTS: prints a message indicating that the command was not recognized
     */
    private void slashUnrecognized() {
        System.out.println("Unrecognizable command. Command must be one of "
                + "/start, /list, /clear, and /end");
    }
}
