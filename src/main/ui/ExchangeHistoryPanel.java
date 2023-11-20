package ui;

import model.Exchange;
import model.ExchangeHistory;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Represents the panel for displaying the list of past exchanges
public class ExchangeHistoryPanel extends JPanel {
    private JTextArea historyArea;
    private JButton displayButton;
    private JButton loadButton;
    private JButton saveButton;
    private JButton clearButton;
    private JTextField numExcField;
    private JCheckBox showLatestCheckBox;
    private Font font;

    private ExchangeHistory excHistory;
    private JsonReader reader;
    private JsonWriter writer;

    /**
     * MODIFIES: this
     * EFFECTS: initializes all field variables, adds action listeners, and defines the panel's layout
     */
    public ExchangeHistoryPanel(ExchangeHistory excHistory, Font font, String historyPath) {
        this.excHistory = excHistory;
        this.font = font;

        reader = new JsonReader(historyPath);
        writer = new JsonWriter(historyPath);

        initializeComponents();
        changeFonts();

        setLayout(new BorderLayout());

        JPanel cp = new JPanel();
        cp.add(displayButton);
        cp.add(numExcField);
        cp.add(showLatestCheckBox);
        cp.add(loadButton);
        cp.add(saveButton);
        cp.add(clearButton);
        add(new JScrollPane(historyArea), BorderLayout.CENTER);
        add(cp, BorderLayout.SOUTH);
    }

    /**
     * MODIFIES: this
     * EFFECTS: initializes all Java swing components of this panel
     */
    private void initializeComponents() {
        historyArea = new JTextArea();
        numExcField = new JTextField(8);
        displayButton = new JButton("Show History");
        displayButton.addActionListener(new DisplayClickHandler());
        loadButton = new JButton("Load History");
        loadButton.addActionListener(new LoadClickHandler());
        saveButton = new JButton("Save History");
        saveButton.addActionListener(new SaveClickHandler());
        clearButton = new JButton("Clear History");
        clearButton.addActionListener(new ClearClickHandler());
        showLatestCheckBox = new JCheckBox("Show the latest exchanges");
    }

    /**
     * MODIFIES: this
     * EFFECTS: changes the font of all Java swing components of this panel
     */
    private void changeFonts() {
        historyArea.setFont(font);
        numExcField.setFont(font);
        displayButton.setFont(font);
        showLatestCheckBox.setFont(font);
        loadButton.setFont(font);
        saveButton.setFont(font);
        clearButton.setFont(font);
    }

    /**
     * MODIFIES: this
     * EFFECTS: adds the fields of an Exchange object to historyArea as Strings
     */
    private void addExchangeStringToHistoryArea(Exchange exc) {
        historyArea.append("----------------------------------------\n");
        historyArea.append("From Currency: " + exc.getFromCurrency() + "\n");
        historyArea.append("From Value: " + exc.getFromValue() + "\n");
        historyArea.append("To Currency: " + exc.getToCurrency() + "\n");
        historyArea.append("To Value: " + exc.getToValue() + "\n");
        historyArea.append("----------------------------------------\n");
    }

    /**
     * EFFECTS: returns a capitalized version of a String
     */
    private String capitalize(String s) {
        if (s.length() == 0) {
            return "";
        } else {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }

    /**
     * EFFECTS: creates a pop-up image showing the app status
     */
    private void showAppStatusPopUp(String status) {
        JFrame frame = new JFrame();
        frame.setSize(842, 512);
        frame.setVisible(false);

        // Reference: https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html
        Icon imageIcon = new ImageIcon("./data/" + status + ".png");
        JOptionPane.showMessageDialog(frame, "", "App Status: "
                + capitalize(status), JOptionPane.INFORMATION_MESSAGE, imageIcon);

        frame.removeAll();
    }

    /**
     * EFFECTS: returns a new List with the Exchange items in exchanges in reverse
     */
    private List<Exchange> reverseList(List<Exchange> exchanges) {
        List<Exchange> reversed = new ArrayList<>();
        for (int i = exchanges.size() - 1; i >= 0; i--) {
            reversed.add(exchanges.get(i));
        }
        return reversed;
    }

    // Represents the action listener for clicking on the display button
    private class DisplayClickHandler implements ActionListener {
        /**
         * MODIFIES: this
         * EFFECTS: show the earliest or latest exchanges in the past
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Exchange> exchanges = excHistory.getHistory();
                if (showLatestCheckBox.isSelected()) {
                    exchanges = reverseList(exchanges);
                }

                int numExc = Integer.parseInt(numExcField.getText());
                int maxLength = Math.max(0, Math.min(numExc, exchanges.size()));

                historyArea.setText("");
                historyArea.append("----------------------------------------\n");
                historyArea.append("Showing history (" + maxLength + " out of " + exchanges.size() + ")\n");

                for (int i = 0; i < maxLength; i++) {
                    addExchangeStringToHistoryArea(exchanges.get(i));
                }
            } catch (NumberFormatException exception) {
                showAppStatusPopUp("numberFormatException");
            }
        }
    }

    // Represents the action listener for clicking on the load button
    private class LoadClickHandler implements ActionListener {
        /**
         * MODIFIES: this
         * EFFECTS: loads the previously saved exchange history
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                excHistory.setHistory(reader.readExchangeHistory().getHistory());
            } catch (IOException exception) {
                showAppStatusPopUp("iOException");
                return;
            }

            showAppStatusPopUp("running");
        }
    }

    // Represents the action listener for clicking on the save button
    private class SaveClickHandler implements ActionListener {
        /**
         * EFFECTS: saves excHistory to the JSON file in historyPath
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                writer.open();
                writer.write(excHistory);
                writer.close();
            } catch (IOException exception) {
                showAppStatusPopUp("iOException");
                return;
            }

            showAppStatusPopUp("running");
        }
    }

    // Represents the action listener for clicking on the clear history button
    private class ClearClickHandler implements ActionListener {
        /**
         * MODIFIES: this
         * EFFECTS: clears the exchange history
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            excHistory.clear();
        }
    }
}
