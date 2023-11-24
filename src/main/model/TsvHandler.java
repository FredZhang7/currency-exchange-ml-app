package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;
import java.text.SimpleDateFormat;
import java.text.ParseException;

// Represents a TSV handler to download and parse TSV files containing historical currency values
public class TsvHandler {
    private String tsvPath;
    private String trainPath;

    /**
     * REQUIRES: tsvPath ends in ".tsv"
     * MODIFIES: this
     * EFFECTS: initializes all fields
     */
    public TsvHandler(String tsvPath) {
        this.tsvPath = tsvPath;
        this.trainPath = tsvPath.split(".tsv")[0] + "_train.tsv";
    }

    /**
     * REQUIRES: a valid date string in the format "yyyy-MM-dd"
     * EFFECTS: downloads the TSV file from the specified URL and returns the data as a string
     */
    private String downloadTSV(String date) throws IOException {
        URL website = new URL("https://www.imf.org/external/np/fin/data/rms_mth.aspx?SelectDate="
                + date + "&reportType=REP&tsvflag=Y");
        BufferedReader in = new BufferedReader(new InputStreamReader(website.openStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
            sb.append(System.lineSeparator());
        }
        in.close();
        return sb.toString();
    }

    /**
     * REQUIRES: TSV data is downloaded with downloadTSV
     * EFFECTS: parses the TSV data and returns a Map of currencies with the corresponding Map of dates and values
     */
    public Map<String, Map<String, String>> parseTSV(String tsvData) {
        Map<String, Map<String, String>> data = new HashMap<>();
        List<String> lines = Arrays.asList(tsvData.split(System.lineSeparator()));
        String[] headers = null;

        for (int i = 1; i < 83; i++) {
            if (i == 1 || i == 44) {
                headers = lines.get(i).split("\t");
                continue;
            }

            if (i > 40 && i < 45) {
                continue;
            }

            Map<String, String> history = new HashMap<>();
            String[] values = lines.get(i).split("\t");

            for (int j = 1; j < values.length; j++) {
                history.put(headers[j], values[j]);
            }

            data.put(headers[0], history);
        }

        return data;
    }

    /**
     * REQUIRES: valid start and end date strings in the format "yyyy-MM-dd"
     * EFFECTS: downloads all TSV files from start to end date,
     *          combines them into one TSV file,
     *          and sorts the columns in increasing order of dates
     */
    public void downloadAndCombineTSVs(String startDate, String endDate) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar start = Calendar.getInstance();
        start.setTime(sdf.parse(startDate));
        Calendar end = Calendar.getInstance();
        end.setTime(sdf.parse(endDate));

        Map<String, Map<String, String>> combinedData = new HashMap<>();

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.MONTH, 1), date = start.getTime()) {
            String tsvData = this.downloadTSV(sdf.format(date));
            Map<String, Map<String, String>> data = this.parseTSV(tsvData);

            for (Map.Entry<String, Map<String, String>> entry : data.entrySet()) {
                String currency = entry.getKey();
                Map<String, String> history = entry.getValue();

                if (!combinedData.containsKey(currency)) {
                    combinedData.put(currency, new TreeMap<>());
                }

                combinedData.get(currency).putAll(history);
            }
        }

        this.writeCombinedTSV(combinedData);
    }

    /**
     * REQUIRES: combined data from downloadAndCombineTSVs
     * EFFECTS: writes the combined data to a TSV file
     */
    private void writeCombinedTSV(Map<String, Map<String, String>> combinedData) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] headers = combinedData.values().stream().findFirst().get().keySet().toArray(new String[0]);
        lines.add("Currency\t" + String.join("\t", headers));

        for (Map.Entry<String, Map<String, String>> entry : combinedData.entrySet()) {
            String currency = entry.getKey();
            Map<String, String> history = entry.getValue();

            List<String> values = new ArrayList<>();
            for (String header : headers) {
                values.add(history.get(header).toString());
            }

            lines.add(currency + "\t" + String.join("\t", values));
        }

        Files.write(Paths.get(tsvPath), lines);
    }

    /**
     * EFFECTS: loads the TSV file and returns a list of lists of strings
     */
    public Map<String, Map<String, String>> loadCombinedTSV() throws IOException {
        Map<String, Map<String, String>> data = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(tsvPath));
        String[] headers = null;

        for (int i = 0; i < lines.size(); i++) {
            if (i == 0) {
                headers = lines.get(i).split("\t");
                continue;
            }

            Map<String, String> history = new HashMap<>();
            String[] values = lines.get(i).split("\t");

            for (int j = 1; j < values.length; j++) {
                history.put(headers[j], values[j]);
            }

            data.put(headers[0], history);
        }

        return data;
    }

    /**
     * EFFECTS: converts the list of lists of strings into a string,
     *          and then save to CSV
     */
    public void saveToTSV(List<List<String>> allHistories) throws IOException {
        FileWriter csvWriter = new FileWriter(trainPath);

        for (List<String> row : allHistories) {
            csvWriter.append(String.join(",", row));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }


    public String getTsvPath() {
        return tsvPath;
    }

    public String getTrainPath() {
        return trainPath;
    }
}