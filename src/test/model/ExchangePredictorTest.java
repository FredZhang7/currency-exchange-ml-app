package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExchangePredictorTest {
    private ExchangeRatePredictor predictor;
    private Database db;

    @BeforeEach
    void init() {
        db = new Database("currency_exchange", "fred", "Freddy77!");
        db.connect();
        predictor = new ExchangeRatePredictor(db, "./data/currency_history.tsv");
    }

    @Test
    public void testDownloadData() throws SQLException, IOException, ParseException {
        predictor.generateTrainData();
        String query = Files.readAllLines(Path.of("./data/testCurrencyRowsCount.sql")).get(0);
        ResultSet rs = db.getStatement().executeQuery(query);
        int count = 0;
        while (rs.next()) {
            if (rs.getInt("Count") == 1187) {
                count++;
            }
        }
        assertEquals(37, count);
    }
}
