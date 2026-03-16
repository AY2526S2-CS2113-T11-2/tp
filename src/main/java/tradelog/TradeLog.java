package tradelog;

import java.util.Scanner;

import tradelog.logic.command.ListCommand;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Main entry point for the TradeLog application.
 */
public class TradeLog {

    private final TradeList tradeList;
    private final Ui ui;
    private final Storage storage;

    /** Constructs a TradeLog instance with fresh data structures. */
    public TradeLog() {
        tradeList = new TradeList();
        ui = new Ui();
        storage = new Storage();
    }

    /** Starts the main input loop. */
    public void run() {
        ui.showWelcome();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("list")) {
                new ListCommand().execute(tradeList, ui, storage);
            }
        }
    }

    /**
     * Main entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new TradeLog().run();
    }
}
