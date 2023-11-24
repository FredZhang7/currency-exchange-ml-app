package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.SQLException;

import java.text.ParseException;

import java.util.List;
import java.util.Map;

// Represents a predictor for exchange rates between two currencies using a TensorFlow model
public class ExchangeRatePredictor {
    private String fromCurrency;
    private String toCurrency;
    private Database db;
    private TsvHandler tsvHandler;

    /**
     * MODIFIES: this
     * EFFECTS: initializes all field variables
     */
    public ExchangeRatePredictor(String fromCurrency, String toCurrency, Database db, String tsvPath) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.db = db;
        tsvHandler = new TsvHandler(tsvPath);
    }

    /**
     * MODIFIES: this
     * EFFECTS: downloads all TSVs available, combines them into one,
     *          gets all sorted exchange histories from the database,
     *          and saves the query to a TSV
     */
    public void setup() throws IOException, ParseException, SQLException {
        tsvHandler.downloadAndCombineTSVs("2003-04-30", "2023-11-23");
        Map<String, Map<String, String>> data = tsvHandler.loadCombinedTSV();
        List<List<String>> allHistories = db.getAllSortedExchangeRateHistories(data);
        tsvHandler.saveToTSV(allHistories);
    }

    /**
     * MODIFIES: this
     * EFFECTS: runs the Python script to train a Bidirectional LSTM model for predictive analysis
     */
    public void train() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python", "./scripts/train_bi_lstm.py", tsvHandler.getTrainPath());
        Process p = pb.start();
    }

    /**
     * EFFECTS: predicts the next exchange rate using the model trained on all historical sorted exchange rates
     */
    public double predict() throws IOException, SQLException, ParseException {
        List<String> sortedExchangeRates = db.getSortedExchangeRates(fromCurrency, toCurrency);

        ProcessBuilder pb = new ProcessBuilder("python", "./scripts/test_bi_lstm.py", sortedExchangeRates.toString());
        Process p = pb.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return Double.parseDouble(in.readLine());
    }
}

