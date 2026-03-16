package tradelog;

import tradelog.logic.command.Command;
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
        // main loop to be expanded by teammates (Parser, commands, etc.)
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
