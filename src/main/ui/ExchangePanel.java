package ui;

import model.Exchange;
import model.ExchangeHistory;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.Arrays;
import java.util.HashMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


// Represents the panel for the user interface for currency exchange operations
public class ExchangePanel extends JPanel {
    private JComboBox<String> fromCurrencyDropDown;
    private JComboBox<String> toCurrencyDropDown;
    private JTextField fromValueField;
    private JTextField toValueField;
    private JButton exchangeButton;
    private JLabel fromLabel;
    private JLabel toLabel;
    private JLabel amountLabel;
    private Font font;

    private ExchangeHistory history;
    private HashMap<String, Double> rates;

    /**
     * REQUIRES: fontSize > 0
     * MODIFIES: this
     * EFFECTS: initializes all field variables, adds an action listener, defines the panel's layout
     */
    public ExchangePanel(ExchangeHistory history, HashMap<String, Double> rates, Font font) {
        this.history = history;
        this.rates = rates;
        this.font = font;

        initializeComponents();
        changeFonts();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        defineExchangeLayout(c);
    }

    /**
     * MODIFIES: this
     * EFFECTS: initializes all Java swing components of this panel
     */
    private void initializeComponents() {
        // Reference: https://docs.oracle.com/javase/8/docs/api/java/util/Set.html
        String[] currencies = rates.keySet().toArray(new String[0]);
        // Reference: https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html
        Arrays.sort(currencies);

        // Reference: https://docs.oracle.com/javase/8/docs/api/javax/swing/JComboBox.html
        fromCurrencyDropDown = new JComboBox<>(currencies);
        toCurrencyDropDown = new JComboBox<>(currencies);
        fromValueField = new JTextField(15);
        toValueField = new JTextField(15);
        exchangeButton = new JButton("Exchange");
        exchangeButton.addActionListener(new ExchangeClickHandler());
        fromLabel = new JLabel("From:", JLabel.CENTER);
        toLabel = new JLabel("To:", JLabel.CENTER);
        amountLabel = new JLabel("Amount:", JLabel.CENTER);
    }

    /**
     * MODIFIES: this
     * EFFECTS: changes the font of all Java swing components of this panel
     */
    private void changeFonts() {
        fromCurrencyDropDown.setFont(font);
        toCurrencyDropDown.setFont(font);
        fromValueField.setFont(font);
        toValueField.setFont(font);
        exchangeButton.setFont(font);
        fromLabel.setFont(font);
        toLabel.setFont(font);
        amountLabel.setFont(font);
    }

    /**
     * MODIFIES: c
     * EFFECTS: defines the layout of the Exchange components
     */
    private void defineExchangeLayout(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 0;
        add(fromLabel, c);
        c.gridx = 1;
        add(fromCurrencyDropDown, c);

        c.gridx = 0;
        c.gridy = 1;
        add(toLabel, c);
        c.gridx = 1;
        add(toCurrencyDropDown, c);

        c.gridx = 0;
        c.gridy = 2;
        add(amountLabel, c);
        c.gridx = 1;
        add(fromValueField, c);

        c.gridx = 0;
        c.gridy = 3;
        add(exchangeButton, c);
        c.gridx = 1;
        add(toValueField, c);
    }

    /**
     * EFFECTS: creates a pop-up that tells the user to enter an amount for the exchange
     */
    private void createEmptyExchangePopup() {
        // Reference: https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html
        JOptionPane.showMessageDialog(this, "There's nothing to exchange! Please enter a number!");
    }

    // Represents the action listener for clicking on the exchange button
    private class ExchangeClickHandler implements ActionListener {
        /**
         * MODIFIES: this
         * EFFECTS: calculates the exchanged currency, creates an Exchange, and add it to history
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (fromValueField.getText().isEmpty()) {
                createEmptyExchangePopup();
            } else {
                // JComboBox reference: https://docs.oracle.com/javase/8/docs/api/javax/swing/JComboBox.html
                String fromCurrency = (String) fromCurrencyDropDown.getSelectedItem();
                String toCurrency = (String) toCurrencyDropDown.getSelectedItem();
                double amount = Double.parseDouble(fromValueField.getText());

                Exchange exc = new Exchange(fromCurrency, toCurrency, amount, rates);
                exc.exchange();
                toValueField.setText("" + exc.getToValue());
                history.add(exc);
            }
        }
    }
}
