package tradelog;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.Command;
import tradelog.logic.parser.Parser;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.storage.ProfileManager;
import tradelog.ui.Ui;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the TradeLog application.
 */
public class TradeLog {
    private static final Logger logger = Logger.getLogger(TradeLog.class.getName());

    private final TradeList tradeList;
    private final Ui ui;
    private final Storage storage;
    private final AtomicBoolean hasSavedTrades = new AtomicBoolean(false);

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
        registerShutdownHook();
    }

    /** Starts the main input loop. */
    public void run() {
        try {
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
                } catch (RuntimeException e) {
                    logger.log(Level.SEVERE, "Unexpected error while executing command.", e);
                    ui.showError("Unexpected internal error. Trades will still be saved.");
                }
            }
        } finally {
            saveTradesIfNeeded(true);
            ui.closeScanner();
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> saveTradesIfNeeded(false), "tradelog-save-on-shutdown"));
    }

    private void saveTradesIfNeeded(boolean shouldNotifyUser) {
        if (!hasSavedTrades.compareAndSet(false, true)) {
            return;
        }

        try {
            storage.saveTrades(tradeList);
            if (shouldNotifyUser) {
                ui.showMessage("Trades saved. Goodbye!");
            }
        } catch (TradeLogException e) {
            logger.log(Level.SEVERE, "Failed to save trades during shutdown.", e);
            if (shouldNotifyUser) {
                ui.showError(e.getMessage());
            }
        }
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
        try {
            new TradeLog("./data", "trades").run();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
