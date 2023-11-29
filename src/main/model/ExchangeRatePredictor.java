package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


import java.sql.SQLException;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Represents a predictor for exchange rates between two currencies using a TensorFlow model
public class ExchangeRatePredictor {
    private Database db;
    private TsvHandler tsvHandler;

    /**
     * REQUIRES: db is connected
     * MODIFIES: this
     * EFFECTS: initializes all field variables
     */
    public ExchangeRatePredictor(Database db, String tsvPath) {
        this.db = db;
        tsvHandler = new TsvHandler(tsvPath);
    }

    /**
     * MODIFIES: this
     * EFFECTS: downloads all TSVs available, combines them into one,
     *          gets all sorted exchange histories from the database,
     *          and saves the query to a TSV
     */
    public void generateTrainData() throws IOException, ParseException, SQLException {
        String tsvPath = tsvHandler.getTsvPath();

        tsvHandler.downloadAndCombineTSVs("2019-02-28", "2023-11-30", tsvPath);
        Map<String, Map<String, String>> data = tsvHandler.loadCombinedTSV(tsvPath);
        db.runSqlScript("./data/createDatabase.sql");

        db.recordData(data);

        List<List<String>> allHistories = db.getAllSortedExchangeRateHistories(data);
        tsvHandler.saveToTSV(allHistories, tsvHandler.getTrainPath());
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
     * EFFECTS: downloads new data and merges with the dataset to create the validation data
     */
    public void generateValidationData() throws IOException, ParseException, SQLException {
        String tsvPath = tsvHandler.getTsvPath();
        String testPath = tsvHandler.getTestPath();

        tsvHandler.downloadAndCombineTSVs("2019-01-01", "2019-01-31", testPath);
        Map<String, Map<String, String>> data = tsvHandler.loadCombinedTSV(tsvPath);
        data.putAll(tsvHandler.loadCombinedTSV(testPath));

        db.recordData(data);

        List<List<String>> allHistories = db.getAllSortedExchangeRateHistories(data);
        allHistories.removeIf(history -> history.size() < 1187);

        List<List<String>> result = new ArrayList<>();

        for (List<String> history : allHistories) {
            for (int i = 0; i <= history.size() - 1187; i++) {
                result.add(history.subList(i, i + 1187));
            }
        }

        tsvHandler.saveToTSV(result, tsvHandler.getTestPath());
    }

    /**
     * EFFECTS: predicts the next exchange rate using the model trained on all historical sorted exchange rates
     */
    public double predict(String fromCurrency, String toCurrency) throws IOException, SQLException, ParseException {
        List<String> sortedExchangeRates = db.getSortedExchangeRates(fromCurrency, toCurrency);

        ProcessBuilder pb = new ProcessBuilder(
                "python",
                "./scripts/inference_bi_lstm.py",
                sortedExchangeRates.toString()
        );
        Process p = pb.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return Double.parseDouble(in.readLine());
    }
}

