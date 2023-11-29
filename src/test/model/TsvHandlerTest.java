package model;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.ParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TsvHandlerTest {
    private TsvHandler tsvHandler;
    private String tsvPath;

    @BeforeEach
    public void setUp() throws IOException {
        tsvPath = "./data/tsvHandlerTest.tsv";
        tsvHandler = new TsvHandler(tsvPath);
    }

    @Test
    public void testGetTsvPath() {
        assertEquals(tsvPath, tsvHandler.getTsvPath());
    }

    @Test
    public void testGetTrainPath() {
        assertEquals("./data/tsvHandlerTest_train.tsv", tsvHandler.getTrainPath());
    }

    @Test
    public void testParseTSV() throws IOException {
        String tsvData = new String(Files.readAllBytes(Paths.get("./data/onlineTsvTest.tsv")));
        Map<String, Map<String, String>> tsv = tsvHandler.parseOnlineTSV(tsvData);

        assertTrue(tsv.containsKey("Chinese yuan"));
        assertEquals("NA", tsv.get("Chinese yuan").get("October 02, 2023"));
        assertEquals("NA", tsv.get("Chinese yuan").get("October 03, 2023"));
        assertEquals("NA", tsv.get("Chinese yuan").get("October 04, 2023"));
        assertEquals("NA", tsv.get("Chinese yuan").get("October 05, 2023"));

        assertTrue(tsv.containsKey("Euro(1)"));
        assertEquals("1.053000", tsv.get("Euro(1)").get("October 02, 2023"));
        assertEquals("1.046900", tsv.get("Euro(1)").get("October 03, 2023"));
        assertEquals("1.049700", tsv.get("Euro(1)").get("October 04, 2023"));
        assertEquals("1.052600", tsv.get("Euro(1)").get("October 05, 2023"));
    }

    @Test
    public void testDownloadAndCombineTSVs() throws IOException, ParseException {
        String tsvPath = tsvHandler.getTsvPath();
        tsvHandler.downloadAndCombineTSVs("2022-04-30", "2023-11-23", tsvPath);

        Map<String, Map<String, String>> combinedData = tsvHandler.loadCombinedTSV(tsvPath);

        assertTrue(combinedData.containsKey("Chinese yuan"));
        assertNotNull(combinedData.get("Chinese yuan").get("April 29, 2022"));

        assertTrue(combinedData.containsKey("Euro(1)"));
        assertEquals("1.091100", combinedData.get("Euro(1)").get("November 22, 2023"));

        assertTrue(combinedData.containsKey("Uruguayan peso"));
        assertNotNull(combinedData.get("Uruguayan peso").get("February 28, 2023"));
    }

    @Test
    public void testLoadCombinedTSV() throws IOException {
        List<String> lines = Arrays.asList("Currency\t2023-11-23", "USD\t1.00");
        Files.write(Path.of(tsvHandler.getTsvPath()), lines);

        Map<String, Map<String, String>> expected = new HashMap<>();
        expected.put("USD", new HashMap<>());
        expected.get("USD").put("2023-11-23", "1.00");
        assertEquals(expected, tsvHandler.loadCombinedTSV(tsvHandler.getTsvPath()));

        tsvHandler = new TsvHandler("./data/currency_history.tsv");
        expected = tsvHandler.loadCombinedTSV(tsvHandler.getTsvPath());
        int validCount = 0;
        for (Map.Entry<String, Map<String, String>> entry : expected.entrySet()) {
            if (entry.getValue().size() == 1187) {
                validCount++;
            }
        }
        assertEquals(37, validCount);
    }

    @Test
    public void testSaveToTSV() throws IOException {
        String trainPath = tsvHandler.getTrainPath();
        List<List<String>> allHistories = Arrays.asList(
                Arrays.asList("January 1, 2022", "April 2, 2022", "May 8, 2023"),
                Arrays.asList("1.33", "NA", "1.37")
        );

        tsvHandler.saveToTSV(allHistories, trainPath);

        List<String> lines = Files.readAllLines(Path.of(trainPath));
        assertEquals("January 1, 2022\tApril 2, 2022\tMay 8, 2023", lines.get(0));
        assertEquals("1.33\tNA\t1.37", lines.get(1));
    }

    @Test
    public void testWriteCombinedTSV() throws IOException {
        Map<String, Map<String, String>> combinedData = new HashMap<>();
        Map<String, String> usdHistory = new HashMap<>();
        usdHistory.put("November 16, 2023", "1.33");
        usdHistory.put("November 17, 2023", "1.37");
        combinedData.put("USD", usdHistory);

        Map<String, String> cadHistory = new HashMap<>();
        cadHistory.put("November 16, 2023", "NA");
        cadHistory.put("November 17, 2023", "1");
        combinedData.put("CAD", cadHistory);

        tsvHandler.writeCombinedTSV(combinedData, tsvHandler.getTsvPath());

        List<String> lines = Files.readAllLines(Path.of(tsvHandler.getTsvPath()));
        assertEquals("Currency\tDate\tValue", lines.get(0));
        assertEquals("USD\tNovember 16, 2023\t1.33", lines.get(1));
        assertEquals("USD\tNovember 17, 2023\t1.37", lines.get(2));
        assertEquals("CAD\tNovember 16, 2023\tNA", lines.get(3));
        assertEquals("CAD\tNovember 17, 2023\t1", lines.get(4));
    }
}
