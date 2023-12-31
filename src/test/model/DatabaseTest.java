package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import java.sql.SQLException;

import java.text.ParseException;

public class DatabaseTest {
    private Database db;
    private Map<String, Map<String, String>> data;
    private Map<String, String> usdHistory;
    private Map<String, String> cadHistory;

    @BeforeEach
    public void setup() throws SQLException {
        db = new Database("mysql", "fred", "Freddy77!");
        db.connect();
        db.runSqlScript("./data/testCreateDatabase.sql");

        db = new Database("testDB", "fred", "Freddy77!");
        db.connect();
        db.runSqlScript("./data/createDatabase.sql");

        usdHistory = new HashMap<>();
        usdHistory.put("January 3, 2023", "1.25");
        usdHistory.put("January 2, 2023", "1.24");
        usdHistory.put("January 1, 2023", "1.23");

        cadHistory = new HashMap<>();
        cadHistory.put("January 1, 2023", "1.00");
        cadHistory.put("January 2, 2023", "1.00");
        cadHistory.put("January 3, 2023", "1.00");

        data = new HashMap<>();
        data.put("USD", usdHistory);
        data.put("CAD", cadHistory);

        db.recordData(data);
    }

    @Test
    public void testGetSortedExchangeRates() throws SQLException, ParseException {
        List<String> expected = Arrays.asList("1.23", "1.24", "1.25");

        assertEquals(usdHistory, db.getCurrencyHistory("USD"));
        assertEquals(cadHistory, db.getCurrencyHistory("CAD"));

        List<String> actual = db.getSortedExchangeRates("USD", "CAD");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllSortedExchangeRateHistories() throws SQLException, ParseException {
        // USD vs CAD rate
        List<String> expected = Arrays.asList("1.23", "1.24", "1.25");
        List<String> actual = db.getAllSortedExchangeRateHistories(data).get(0);
        assertEquals(expected, actual);

        // CAD vs USD rate
        expected = Arrays.asList(1 / 1.23 + "", 1 / 1.24 + "", 1 / 1.25 + "");
        actual = db.getAllSortedExchangeRateHistories(data).get(1);
        assertEquals(expected, actual);
    }

    @AfterEach
    public void teardown() {
        db.teardown();
    }
}
