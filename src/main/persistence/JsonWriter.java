package persistence;

import model.ExchangeHistory;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Represents a writer that writes exchange history to a JSON file
public class JsonWriter {
    private String savePath;
    private PrintWriter writer;
    private static final int TAB = 4;

    /**
     * EFFECTS: constructs JsonWriter and initializes the save path
     */
    public JsonWriter(String savePath) {
        this.savePath = savePath;
    }

    /**
     * Source: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence
     * MODIFIES: this
     * EFFECTS: opens writer; throws FileNotFoundException if destination file cannot be opened for writing
     */
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(savePath));
    }

    /**
     * Source: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence
     * MODIFIES: this
     * EFFECTS: writes JSON representation of exchange history to file
     */
    public void write(ExchangeHistory excHistory) {
        JSONObject json = excHistory.toJson();
        writer.print(json.toString(TAB));
    }

    /**
     * Source: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo/blob/master/src/main/persistence
     * MODIFIES: this
     * EFFECTS: closes the writer
     */
    public void close() {
        writer.close();
    }
}
