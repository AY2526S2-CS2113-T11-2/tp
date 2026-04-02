package tradelog;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.Command;
import tradelog.logic.parser.Parser;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.storage.ProfileManager;
import tradelog.ui.Ui;

/**
 * Main entry point for the TradeLog application.
 */
public class TradeLog {

    private final TradeList tradeList;
    private final Ui ui;
    private final Storage storage;

    /**
     * Constructs a TradeLog instance, prompting the user for a password to find or
     * create a storage profile.
     *
     * @param baseDirectory The base directory where the storage files are located.
     * @param baseFileName The base name of the storage file.
     */
    public TradeLog(String baseDirectory, String baseFileName) {
        ui = new Ui();
        ProfileManager profileManager = new ProfileManager(baseDirectory, baseFileName, ui);
        storage = profileManager.getActiveStorage();
        tradeList = profileManager.getLoadedTrades();
    }

    /** Starts the main input loop. */
    public void run() {
        ui.showWelcome();
        String input;
        while ((input = ui.readCommand()) != null) {
            if (input.isEmpty()) {
                ui.showError("Command cannot be empty.");
                continue;
            }
            try {
                Command command = Parser.parseCommand(input);
                command.execute(tradeList, ui, storage);
                if (command.isExit()) {
                    break;
                }
            } catch (TradeLogException e) {
                ui.showError(e.getMessage());
            }
        }
        
        try {
            storage.saveTrades(tradeList);
            ui.showMessage("Trades saved. Goodbye!");
        } catch (TradeLogException e) {
            ui.showError(e.getMessage());
        }
        ui.closeScanner();
    }

    /**
     * Main entry point.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(
                    TradeLog.class.getResourceAsStream("/logging.properties"));
        } catch (Exception e) {
            // fall back to default logging if config fails
        }
        new TradeLog("./data", "trades").run();
    }
}
