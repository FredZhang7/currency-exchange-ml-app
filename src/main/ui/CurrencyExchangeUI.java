package ui;

import model.ExchangeHistory;
import model.LocalExchangeRates;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.Map;

// Represents Currency Exchange app's main window frame
public class CurrencyExchangeUI extends JFrame {
    private ExchangePanel exchangePanel;
    private ExchangeHistoryPanel historyPanel;

    public static void main(String[] args) throws IOException {
        new CurrencyExchangeUI();
    }

    /**
     * MODIFIES: this
     * EFFECTS: initializes all fields, creates the Exchange window, and displays it
     */
    private CurrencyExchangeUI() throws IOException {
        super("Currency Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);

        ExchangeHistory history = new ExchangeHistory();
        Map<String, Double> rates = new LocalExchangeRates().getExcRates();

        // Reference: https://stackoverflow.com/questions/71998881/replace-existing-font-definition-in-swing
        Font roboto = new Font("Roboto", Font.PLAIN, 14);

        String historyPath = "./data/exchange_history.json";

        exchangePanel = new ExchangePanel(history, rates, roboto);
        historyPanel = new ExchangeHistoryPanel(history, roboto, historyPath);

        add(exchangePanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
