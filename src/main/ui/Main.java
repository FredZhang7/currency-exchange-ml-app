package ui;


import model.Database;
import model.ExchangeRatePredictor;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException, ParseException {
        //new ExchangeConsoleApp();
        Database db = new Database("currency_exchange", "fred", "Freddy77!");
        db.connect();
        ExchangeRatePredictor predictor = new ExchangeRatePredictor(db, "./data/currency_history.tsv");
        predictor.generateValidationData();
    }
}
