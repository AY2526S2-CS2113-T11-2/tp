package tradelog.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import tradelog.model.Trade;
import tradelog.model.TradeList;

/**
 * Handles all user interaction output for TradeLog.
 */
public class Ui {

    private static final String DIVIDER = "-".repeat(80);
    private static final Logger logger = Logger.getLogger(Ui.class.getName());

    /** Prints the welcome banner shown on startup. */
    public void showWelcome() {
        showLine();
        System.out.println("Welcome to TradeLog!");
        System.out.println("Commands: add, list, edit, delete, summary, exit");
        showLine();
        logger.log(Level.INFO, "Welcome message displayed.");
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
     * Prints an empty-list message if there are no trades.
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

    /** Prints a message confirming a trade was added. */
    public void showTradeAdded() {
        System.out.println("Trade successfully added.");
        logger.log(Level.INFO, "Trade added confirmation displayed.");
    }

    /** Prints a message confirming a trade was deleted. */
    public void showTradeDeleted() {
        System.out.println("Trade successfully deleted.");
        logger.log(Level.INFO, "Trade deleted confirmation displayed.");
    }

    /**
     * Prints a message confirming a trade was updated.
     *
     * @param index The 1-based index of the updated trade.
     */
    public void showTradeUpdated(int index) {
        assert index > 0 : "Index should be a positive integer";
        System.out.println("Trade " + index + " updated successfully.");
        logger.log(Level.INFO, "Trade {0} updated confirmation displayed.", index);
    }

    /** Prints a message when no trades are available for the summary. */
    public void showSummaryEmpty() {
        showLine();
        System.out.println("No trades available to generate a summary.");
        showLine();
        logger.log(Level.INFO, "Empty summary message displayed.");
    }

    /**
     * Prints the overall performance summary.
     *
     * @param totalTrades   Total number of trades.
     * @param winRate       Win rate as a percentage.
     * @param averageWin    Average winning R value.
     * @param averageLoss   Average losing R value.
     * @param expectedValue Overall expected value per trade.
     * @param totalR        Total R gained or lost.
     */
    public void showSummary(int totalTrades, double winRate, double averageWin,
                            double averageLoss, double expectedValue, double totalR) {
        assert totalTrades > 0 : "Total trades should be greater than zero";
        assert winRate >= 0 && winRate <= 100 : "Win rate should be between 0 and 100";
        assert averageWin >= 0 : "Average win should be non-negative";
        assert averageLoss >= 0 : "Average loss should be non-negative";

        logger.log(Level.INFO, "Displaying summary for {0} trades.", totalTrades);

        String evSign = expectedValue >= 0 ? "+" : "-";
        String totalRSign = totalR >= 0 ? "+" : "-";
        showLine();
        System.out.println("Overall Performance:\n");
        System.out.println("Total Trades: " + totalTrades);
        System.out.printf("Win Rate: %.0f%%%n", winRate);
        System.out.printf("Average Win: %.2fR%n", averageWin);
        System.out.printf("Average Loss: %.2fR%n", averageLoss);
        System.out.printf("Overall EV: %s%.2fR%n", evSign, expectedValue);
        System.out.printf("Total R: %s%.2fR%n", totalRSign, totalR);
        showLine();
    }

    /**
     * Prints an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        assert message != null : "Error message should not be null";

        logger.log(Level.WARNING, "Error displayed to user: {0}", message);

        showLine();
        System.out.println("Error: " + message);
        showLine();
    }
}
