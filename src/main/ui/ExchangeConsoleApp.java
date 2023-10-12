package ui;

import model.Exchange;
import model.ExchangeHistory;
import model.ExchangeRates;

import java.util.HashMap;
import java.util.Scanner;

public class ExchangeConsoleApp {
    private Scanner scanner;
    private ExchangeHistory excHistory;
    private HashMap<String, Double> excRates;

    public ExchangeConsoleApp() {
        // Source: https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
        scanner = new Scanner(System.in);

        excHistory = new ExchangeHistory();

        // Source: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html
        excRates = new ExchangeRates().getExcRates();

        roundStart();

        while (true) {
            // Source: https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
            String command = scanner.nextLine();

            if (command.equals("/start")) {
                slashStart();
            } else if (command.equals("/history")) {
                slashHistory();
            } else if (command.equals("/clear")) {
                slashClear();
            } else if (command.equals("/end")) {
                slashEnd();
                break;
            } else if (command.length() == 0) {
                continue;
            } else {
                slashUnrecognized();
            }
        }
    }

    public void roundStart() {
        System.out.println("Use command /start to start a transaction, \n"
                + "/history to view the history of transactions, \n"
                + "/clear to clear the history of transactions, \n"
                + "/end to stop the app");
    }

    public void slashStart() {
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

    public void slashHistory() {
        excHistory.view();
    }

    public void slashClear() {
        excHistory.clear();
    }

    public void slashEnd() {
        System.out.println("You stopped the app. Re-run the code to restart.");
    }

    public void slashUnrecognized() {
        System.out.println("Unrecognizable command. Command must be one of "
                + "/start, /list, /clear, and /end");
    }
}
