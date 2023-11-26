package model;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Date;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// represents a MySQL database with TSV data recording capabilities
public class Database {
    private Connection connection;
    private String name;
    private String username;
    private String password;
    private Statement statement;

    /**
     * MODIFIES: this
     * EFFECTS: initializes name, username, and password of the MySQL database
     */
    public Database(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    /**
     * EFFECTS: establishes a connection to a local MySQL database
     */
    public void connect() throws IllegalStateException {
        try {
            System.out.println("Connecting to MySQL database...");
            String url = "jdbc:mysql://localhost:3306/" + name + "?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, username, password);
            this.statement =  connection.createStatement();
            System.out.println(name + " database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Error! Cannot connect to the database!", e);
        }
    }

    /**
     * REQUIRES: data is returned from the parseTSV method
     * EFFECTS: only records non-duplicated data into the MySQL database
     */
    public void recordData(Map<String, Map<String, String>> data) throws SQLException {
        String insertRowQuery = "INSERT IGNORE INTO CurrencyData (Currency, Date, Value) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(insertRowQuery)) {
            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                String currency = entry.getKey();
                Map<String, String> row = entry.getValue();

                for (Map.Entry<String, String> dateValue : row.entrySet()) {
                    String date = dateValue.getKey();
                    String value = dateValue.getValue();

                    preparedStatement.setString(1, currency);
                    preparedStatement.setString(2, date);
                    preparedStatement.setString(3, value);

                    preparedStatement.execute();
                }
            }
        }
    }

    /**
     * REQUIRES: resultSet is not null and is positioned before the first row
     * MODIFIES: resultSet
     * EFFECTS: parses a ResultSet and returns a map of dates and corresponding currency values
     */
    private Map<String, String> parseHistory(ResultSet resultSet) throws SQLException, NullPointerException {
        Map<String, String> history = new HashMap<>();
        while (resultSet.next()) {
            String date = resultSet.getString("Date");
            String value = resultSet.getString("Value");
            history.put(date, value);
        }
        return history;
    }

    /**
     * REQUIRES: currency is not null and is a valid currency code
     * MODIFIES: this
     * EFFECTS: safely inserts parameters into MySQL query,
     *          retrieves the currency history,
     *          and returns the parsed data
     */
    protected Map<String, String> getCurrencyHistory(String currency) throws SQLException {
        String query = "SELECT Date, Value FROM CurrencyData WHERE Currency = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, currency);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return this.parseHistory(resultSet);
            }
        }
    }

    /**
     * EFFECTS: returns a map of dates and corresponding exchange rates between fromCurrency and toCurrency
     */
    protected Map<String, String> getExchangeRateHistory(String fromCurrency, String toCurrency) throws SQLException {
        Map<String, String> fromCurrencyHistory = this.getCurrencyHistory(fromCurrency);
        Map<String, String> toCurrencyHistory = this.getCurrencyHistory(toCurrency);

        Map<String, String> exchangeRateHistory = new HashMap<>();
        for (String date : fromCurrencyHistory.keySet()) {
            if (toCurrencyHistory.containsKey(date)) {
                String fromString = fromCurrencyHistory.get(date);
                String toString = toCurrencyHistory.get(date);
                try {
                    Double fromRate = Double.parseDouble(fromString);
                    Double toRate = Double.parseDouble(toString);
                    exchangeRateHistory.put(date, Double.toString(fromRate / toRate));
                } catch (NumberFormatException e) {
                    exchangeRateHistory.put(date, "NA");
                }
            }
        }

        return exchangeRateHistory;
    }

    /**
     * EFFECTS: returns a list of currency values sorted in an order of increasing dates
     */
    public List<String> getSortedExchangeRates(
            String fromCurrency,
            String toCurrency
    ) throws SQLException, ParseException {
        Map<String, String> exchangeRateHistory = this.getExchangeRateHistory(fromCurrency, toCurrency);

        Map<Date, String> dateExchangeRateMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        for (Map.Entry<String, String> entry : exchangeRateHistory.entrySet()) {
            Date date = sdf.parse(entry.getKey());
            dateExchangeRateMap.put(date, entry.getValue());
        }

        return new ArrayList<>(dateExchangeRateMap.values());
    }

    /**
     * REQUIRES: data is returned from the parseTSV method
     * EFFECTS: computes and returns all possible combinations of sorted exchange rate history
     */
    public List<List<String>> getAllSortedExchangeRateHistories(
            Map<String, Map<String, String>> data
    ) throws SQLException, ParseException {
        Set<String> currencies = data.keySet();

        List<List<String>> allHistories = new ArrayList<>();
        for (String fromCurrency : currencies) {
            for (String toCurrency : currencies) {
                if (!fromCurrency.equals(toCurrency)) {
                    List<String> history = this.getSortedExchangeRates(fromCurrency, toCurrency);
                    allHistories.add(history);
                }
            }
        }

        return allHistories;
    }

    /**
     * REQUIRES: a valid filePath with a valid sql script
     * EFFECTS: executes the sql script in filePath
     */
    public void runSqlScript(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder sql = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // Ignore comments in the SQL script
                if (line.startsWith("--")) {
                    continue;
                }

                sql.append(line);

                // If the line ends with a semicolon, execute the SQL command
                if (line.endsWith(";")) {
                    this.statement.execute(sql.toString());
                    sql = new StringBuilder();
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * MODIFIES: this
     * EFFECTS: debugs the current state of the database by checking the recorded data
     */
    public void printTable(String tableName) throws NullPointerException {
        try {
            String sql = "SELECT * FROM " + tableName;
            ResultSet rs = this.statement.executeQuery(sql);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            String header = "";
            for (int i = 1; i <= columnsNumber; i++) {
                header += rsmd.getColumnName(i) + ", \t";
            }
            System.out.println(header.substring(0, header.length() - 3));

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) {
                        System.out.print(",\t");
                    }
                    String columnValue = rs.getString(i);
                    System.out.print("\"" + columnValue + "\"");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
