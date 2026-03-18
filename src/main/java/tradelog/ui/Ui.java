package tradelog.ui;

import tradelog.model.TradeList;

/**
 * Handles all user interaction output for TradeLog.
 */
public class Ui {

    /** Prints the welcome banner shown on startup. */
    public void showWelcome() {
        showLine();
        System.out.println("Welcome to TradeLog!");
        System.out.println("Type 'list' to view trades, 'exit' to quit.");
        showLine();
    }

    /** Prints the goodbye message shown when exiting the app. */
    public void showGoodbye() {
        showLine();
        System.out.println("Goodbye! May your profits be high and your losses small.");
        showLine();
    }

    /** Prints a horizontal divider line. */
    public void showLine() {
        System.out.println("-".repeat(80));
    }

    /**
     * Prints all trades in the given TradeList, numbered from 1.
     * Prints an empty-list message if there are no trades.
     *
     * @param tradeList The list of trades to display.
     */
    public void printTradeList(TradeList tradeList) {
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
     * Prints an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        showLine();
        System.out.println("Error: " + message);
        showLine();
    }
}
