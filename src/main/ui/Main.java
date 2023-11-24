package ui;


import model.Database;

public class Main {
    public static void main(String[] args) {
        //new ExchangeConsoleApp();
        Database database = new Database("currency_exchange", "fred", "Freddy77!");
        database.connect();
    }
}
