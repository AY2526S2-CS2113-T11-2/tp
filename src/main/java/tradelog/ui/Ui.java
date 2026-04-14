package tradelog.ui;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tradelog.logic.command.StrategyStats;
import tradelog.logic.parser.ParserUtil;
import tradelog.model.ModeManager;
import tradelog.model.Trade;
import tradelog.model.TradeList;

/**
 * Handles all user interaction output for TradeLog.
 */
public class Ui {

    private static final String DIVIDER = "-".repeat(80);
    private static final String COMMAND_LIST =
            "Commands: add, list, edit, delete, filter, compare, summary, encrypt, undo, mode, exit";
    private static final String STRATEGY_SHORTCUTS_HEADER = "Strategy shortcuts:";
    private static final Logger logger = Logger.getLogger(Ui.class.getName());
    private final java.util.Scanner scanner = new java.util.Scanner(System.in);

    /**
     * Reads a command from the user.
     *
     * @return The command entered by the user.
     */
    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return null;
    }

    /** Prints the welcome banner shown on startup. */
    public void showWelcome() {
        showLine();
        System.out.println("Welcome to TradeLog!");
        System.out.println(COMMAND_LIST);
        showStrategyShortcuts();
        showCurrentMode(); // New: Display current mode on startup
        showLine();
        logger.log(Level.INFO, "Welcome message displayed.");
    }

    /**
     * Displays the current operating mode (LIVE or BACKTEST).
     */
    public void showCurrentMode() {
        ModeManager modeManager = ModeManager.getInstance();
        assert modeManager != null : "ModeManager must be initialized before showing mode";

        String modeName = modeManager.isLive() ? "LIVE" : "BACKTEST";
        System.out.println("Current System Mode: [" + modeName + "]");
        logger.log(Level.INFO, "Current mode displayed: {0}", modeName);
    }

    private void showStrategyShortcuts() {
        Map<String, String> strategyShortcuts = ParserUtil.getStrategyShortcuts();
        assert strategyShortcuts != null : "Strategy shortcuts map should not be null";

        logger.log(Level.INFO, "Displaying {0} strategy shortcuts.",
                strategyShortcuts.size());

        System.out.println(STRATEGY_SHORTCUTS_HEADER);
        for (Map.Entry<String, String> strategyShortcut : strategyShortcuts.entrySet()) {
            System.out.println(strategyShortcut.getKey() + " = " + strategyShortcut.getValue());
        }
    }

    /** Prints the goodbye message shown when exiting the app. */
    public void showGoodbye() {
        showLine();
        System.out.println("Goodbye! May your profits be high and your losses small.");
        showLine();
        logger.log(Level.INFO, "Goodbye message displayed.");
    }

    /** Prints a horizontal divider line. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a general message to the user.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        assert message != null : "Message should not be null";
        System.out.println(message);
    }

    /**
     * Prints all trades in the given TradeList, numbered from 1.
     *
     * @param tradeList The list of trades to display.
     */
    public void printTradeList(TradeList tradeList) {
        assert tradeList != null : "TradeList should not be null";

        logger.log(Level.INFO, "Printing trade list. Trade count: {0}", tradeList.size());

        showLine();
        if (tradeList.isEmpty()) {
            System.out.println("No trades logged yet.");
        } else {
            for (int i = 0; i < tradeList.size(); i++) {
                System.out.println((i + 1) + ". " + tradeList.getTrade(i));
            }
        }
        showLine();
    }

    /**
     * Prints the trades at the specified original indices.
     *
     * @param tradeList The full list of trades.
     * @param indices   The zero-based indices of trades to print.
     */
    public void printIndexedTrades(TradeList tradeList, java.util.List<Integer> indices) {
        assert tradeList != null : "TradeList should not be null";
        assert indices != null : "Indices should not be null";

        logger.log(Level.INFO, "Printing {0} filtered trade(s).", indices.size());

        showLine();
        for (int index : indices) {
            System.out.println((index + 1) + ". " + tradeList.getTrade(index));
        }
        showLine();
    }

    /**
     * Prints the summary of a single trade.
     *
     * @param trade The trade to display.
     */
    public void printTrade(Trade trade) {
        assert trade != null : "Trade should not be null";

        logger.log(Level.INFO, "Printing trade: {0}", trade.getTicker());

        showLine();
        System.out.println(trade.toSummaryString());
        showLine();
    }

    public void showTradeAdded() {
        System.out.println("Trade successfully added.");
    }

    public void showTradeDeleted() {
        System.out.println("Trade successfully deleted.");
    }

    public void showTradeUpdated(int index) {
        assert index > 0 : "Index should be a positive integer";
        System.out.println("Trade " + index + " updated successfully.");
    }

    public void showSummaryEmpty() {
        showLine();
        System.out.println("No trades available to generate a summary.");
        showLine();
    }

    public void showSummary(int totalTrades, double winRate, double averageWin,
                            double averageLoss, double expectedValue, double totalR) {
        assert totalTrades > 0 : "Total trades should be greater than zero";

        logger.log(Level.INFO, "Displaying summary for {0} trades.", totalTrades);

        String evSign = expectedValue >= 0 ? "+" : "-";
        String totalRSign = totalR >= 0 ? "+" : "-";
        showLine();
        System.out.println("Overall Performance:\n");
        System.out.println("Total Trades: " + totalTrades);
        System.out.printf("Win Rate: %.0f%%%n", winRate);
        System.out.printf("Average Win: %.2fR%n", averageWin);
        System.out.printf("Average Loss: %.2fR%n", averageLoss);
        System.out.printf("Overall EV: %s%.2fR%n", evSign, Math.abs(expectedValue));
        System.out.printf("Total R: %s%.2fR%n", totalRSign, Math.abs(totalR));
        showLine();
    }

    public void showStrategyComparison(Map<String, StrategyStats> strategyComparison) {
        assert strategyComparison != null : "Strategy comparison data should not be null";

        showLine();
        System.out.println("Strategy Comparison:\n");
        for (Map.Entry<String, StrategyStats> strategyEntry : strategyComparison.entrySet()) {
            String strategyName = strategyEntry.getKey();
            StrategyStats strategyStats = strategyEntry.getValue();
            String evSign = strategyStats.getExpectedValue() >= 0 ? "+" : "-";

            System.out.println(strategyName + ":");
            System.out.println("  Trades: " + strategyStats.getTradeCount());
            System.out.printf("  Win Rate: %.0f%%%n", strategyStats.getWinRate());
            System.out.printf("  Average Win: %.2fR%n", strategyStats.getAverageWin());
            System.out.printf("  Average Loss: %.2fR%n", strategyStats.getAverageLoss());
            System.out.printf("  EV: %s%.3fR%n%n", evSign, Math.abs(strategyStats.getExpectedValue()));
        }
        showLine();
    }

    public void showUndoSuccess() {
        System.out.println("Most recent change has been undone.");
    }

    public void showUndoUnavailable() {
        System.out.println("There is no action to undo.");
    }

    public String readPassword(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    public void showError(String message) {
        assert message != null : "Error message should not be null";
        logger.log(Level.WARNING, "Error displayed: {0}", message);
        showLine();
        System.out.println("Error: " + message);
        showLine();
    }

    public void closeScanner() {
        scanner.close();
        logger.log(Level.INFO, "Scanner closed.");
    }

    /**
     * Displays a standalone message wrapped in dividers.
     * Used for errors or status checks to ensure consistent "mode" styling.
     * @param message The message to display.
     */
    public void showModeSimpleMessage(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Displays the warning and prompt block, then immediately closes it with a divider.
     * The user will provide input AFTER the divider appears.
     */
    public void showModePromptBlock(String current, String target) {
        showLine();
        System.out.println("Preparing to switch from " + current + " to " + target + "...");
        System.out.println(tradelog.model.ModeManager.getInstance().getWarningMessage());
        System.out.println("\nEnter 'yes' to confirm the switch, or any other key to cancel: ");
        showLine(); // Divider appears BEFORE user input
    }
}
