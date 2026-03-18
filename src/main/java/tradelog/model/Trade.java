package tradelog.model;

/**
 * Represents a single trade entered by the user.
 * Stores trade details such as ticker, date, direction, prices, and strategy.
 */
public class Trade {
    private String ticker;
    private String date;
    private String direction;
    private double entryPrice;
    private double exitPrice;
    private double stopLossPrice;
    private String outcome;
    private String strategy;

    /**
     * Constructs a new Trade object with the specified parameters.
     *
     * @param ticker     The stock or asset ticker symbol (e.g., AAPL).
     * @param date       The date of the trade in YYYY-MM-DD format.
     * @param direction  The direction of the trade (Long or Short).
     * @param entryPrice The price at which the trade was entered.
     * @param exitPrice  The price at which the trade was exited.
     * @param stopLossPrice   The stop loss price set for the trade.
     * @param outcome    The outcome of the trade (Win or Loss).
     * @param strategy   The strategy used for the trade.
     */
    public Trade(String ticker, String date, String direction, double entryPrice,
                 double exitPrice, double stopLossPrice, String outcome, String strategy) {
        this.ticker = ticker;
        this.date = date;
        this.direction = direction;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.stopLossPrice = stopLossPrice;
        this.outcome = outcome;
        this.strategy = strategy;
    }

    /**
     * Calculates the Risk:Reward ratio of the trade.
     * Risk is the absolute difference between Entry and Stop Loss.
     * Reward is the difference between Exit and Entry (inverted for Short trades).
     *
     * @return The calculated Risk:Reward ratio, or 0 if risk is zero.
     */
    public double getRiskRewardRatio() {
        double risk = Math.abs(entryPrice - stopLossPrice);
        if (risk == 0) {
            return 0; // Prevent division by zero if stop loss equals entry
        }

        double reward;
        if (direction.equalsIgnoreCase("short")) {
            reward = entryPrice - exitPrice;
        } else {
            reward = exitPrice - entryPrice;
        }

        return reward / risk;
    }

    /**
     * Formats a double price to a String, removing trailing decimal zeros if it is a whole number.
     *
     * @param price The price to format.
     * @return The formatted price string (e.g., "180" instead of "180.0").
     */
    private String formatPrice(double price) {
        if (price == (long) price) {
            return String.format("%d", (long) price);
        } else {
            return String.format("%s", price);
        }
    }

    /**
     * Generates a formatted summary string of the trade details and Risk:Reward ratio.
     * Matches the expected output format strictly.
     *
     * @return The formatted trade summary string.
     */
    public String toSummaryString() {
        double rr = getRiskRewardRatio();
        String sign = rr > 0 ? "+" : "";

        return "Trade Summary:\n" +
                "Ticker: " + ticker + "\n" +
                "Date: " + date + "\n" +
                "Direction: " + direction + "\n" +
                "Entry: " + formatPrice(entryPrice) + "\n" +
                "Exit: " + formatPrice(exitPrice) + "\n" +
                "Stop: " + formatPrice(stopLossPrice) + "\n" +
                "Strategy: " + strategy + "\n\n" +
                String.format("Risk:Reward: %s%.2fR", sign, rr);
    }

    /**
     * Formats the trade details into a string suitable for storage in a text file.
     * Fields are separated by " | " to allow for easy parsing when loading from storage.
     * 
     * @return The formatted storage string.
     */
    public String toStorageString() {
        return ticker + " | " +
                date + " | " +
                direction + " | " +
                entryPrice + " | " +
                exitPrice + " | " +
                stopLossPrice + " | " +
                outcome + " | " +
                strategy;
    }
  
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }

    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}
