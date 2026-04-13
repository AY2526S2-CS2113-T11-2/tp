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
    private String strategy;

    /**
     * Constructs a new Trade object with the specified parameters.
     *
     * @param ticker        The stock or asset ticker symbol (e.g., AAPL).
     * @param date          The date of the trade in YYYY-MM-DD format.
     * @param direction     The direction of the trade (Long or Short).
     * @param entryPrice    The price at which the trade was entered.
     * @param exitPrice     The price at which the trade was exited.
     * @param stopLossPrice The stop loss price set for the trade.
     * @param strategy      The strategy used for the trade.
     */
    public Trade(String ticker, String date, String direction, double entryPrice,
                 double exitPrice, double stopLossPrice, String strategy) {
        assert direction.equalsIgnoreCase("Long") || direction.equalsIgnoreCase("Short") :
                "Direction must be 'Long' or 'Short'";
        assert !Double.isNaN(entryPrice) && !Double.isInfinite(entryPrice) :
                "Entry price cannot be NaN or infinite";
        assert !Double.isNaN(exitPrice) && !Double.isInfinite(exitPrice) :
                "Exit price cannot be NaN or infinite";
        assert !Double.isNaN(stopLossPrice) && !Double.isInfinite(stopLossPrice) :
                "Stop loss price cannot be NaN or infinite";

        this.ticker = ticker;
        this.date = date;
        this.direction = direction;
        this.entryPrice = entryPrice;
        this.exitPrice = exitPrice;
        this.stopLossPrice = stopLossPrice;
        this.strategy = strategy;
    }

    /**
     * Calculates the Risk:Reward ratio of the trade.
     * Risk is the absolute difference between entry and stop loss.
     * Reward is the difference between exit and entry (inverted for Short trades).
     *
     * @return The calculated Risk:Reward ratio, or 0 if risk is zero.
     */
    public double getRiskRewardRatio() {
        double risk = Math.abs(entryPrice - stopLossPrice);
        assert risk >= 0 : "Risk cannot be negative";

        if (risk == 0) {
            return 0;
        }
        double reward;
        if (direction.equalsIgnoreCase("short")) {
            reward = entryPrice - exitPrice;
            assert !Double.isNaN(reward) && !Double.isInfinite(reward) :
                    "Reward cannot be NaN or infinite";
        } else {
            reward = exitPrice - entryPrice;
        }
        double riskRewardRatio = reward / risk;
        assert !Double.isNaN(riskRewardRatio) && !Double.isInfinite(riskRewardRatio) :
                "Risk:Reward ratio cannot be NaN or infinite";
        return riskRewardRatio;
    }

    /**
     * Formats a double price to a String, removing trailing zeros if it is a whole number.
     *
     * @param price The price to format.
     * @return The formatted price string (e.g., "180" instead of "180.0").
     */
    private String formatPrice(double price) {
        assert !Double.isNaN(price) && !Double.isInfinite(price) : "Price cannot be NaN or infinite";

        if (price == (long) price) {
            return String.format("%d", (long) price);
        } else {
            return String.format("%s", price);
        }
    }

    /**
     * Returns a formatted summary string of the trade details and Risk:Reward ratio.
     *
     * @return The formatted trade summary string.
     */
    public String toSummaryString() {
        double rr = getRiskRewardRatio();
        String sign = rr > 0 ? "+" : "";
        return "Trade Summary:\n"
                + "Ticker: " + ticker + "\n"
                + "Date: " + date + "\n"
                + "Direction: " + direction + "\n"
                + "Entry: " + formatPrice(entryPrice) + "\n"
                + "Exit: " + formatPrice(exitPrice) + "\n"
                + "Stop: " + formatPrice(stopLossPrice) + "\n"
                + "Strategy: " + strategy + "\n\n"
                + String.format("Risk:Reward: %s%.2fR", sign, rr);
    }

    /**
     * Returns a formatted string suitable for storage in a text file.
     * Fields are separated by " | " to allow easy parsing when loading from storage.
     *
     * @return The formatted storage string.
     */
    public String toStorageString() {
        return ticker + " | "
                + date + " | "
                + direction + " | "
                + entryPrice + " | "
                + exitPrice + " | "
                + stopLossPrice + " | "
                + strategy;
    }

    /**
     * Returns a single-line string representation of the trade for display in the list.
     * Format: AAPL | 2026-02-18 | Long | E:180 | TP:190 | SL:170 | Win | Breakout
     *
     * @return Formatted trade details.
     */
    @Override
    public String toString() {
        return ticker + " | "
                + date + " | "
                + direction + " | "
                + "E:" + formatPrice(entryPrice) + " | "
                + "TP:" + formatPrice(exitPrice) + " | "
                + "SL:" + formatPrice(stopLossPrice) + " | "
                + getOutcome() + " | "
                + strategy;
    }

    /**
     * Returns the ticker symbol.
     *
     * @return The ticker symbol.
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * Returns the trade date.
     *
     * @return The date in YYYY-MM-DD format.
     */
    @SuppressWarnings("unused")
    public String getDate() {
        return date;
    }

    /**
     * Returns the trade direction.
     *
     * @return The direction (Long or Short).
     */
    @SuppressWarnings("unused")
    public String getDirection() {
        return direction;
    }

    /**
     * Returns the entry price.
     *
     * @return The entry price.
     */
    @SuppressWarnings("unused")
    public double getEntryPrice() {
        return entryPrice;
    }

    /**
     * Returns the exit price.
     *
     * @return The exit price.
     */
    @SuppressWarnings("unused")
    public double getExitPrice() {
        return exitPrice;
    }

    /**
     * Returns the stop loss price.
     *
     * @return The stop loss price.
     */
    @SuppressWarnings("unused")
    public double getStopLossPrice() {
        return stopLossPrice;
    }

    /**
     * Returns the outcome of the trade, derived from the Risk:Reward ratio.
     * A positive RR is a Win, negative is a Loss, and zero is a Breakeven.
     *
     * @return "Win", "Loss", or "Breakeven".
     */
    public String getOutcome() {
        double rr = getRiskRewardRatio();
        if (rr > 0) {
            return "Win";
        } else if (rr < 0) {
            return "Loss";
        } else {
            return "Breakeven";
        }
    }

    /**
     * Returns the trade strategy.
     *
     * @return The strategy description.
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Sets the ticker symbol.
     *
     * @param ticker The new ticker symbol.
     */
    public void setTicker(String ticker) {
        this.ticker = ticker.toUpperCase();
    }

    /**
     * Sets the trade date.
     *
     * @param date The new date in YYYY-MM-DD format.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the trade direction, normalising it to title case (e.g. "Long", "Short").
     *
     * @param rawDir The raw direction string (e.g. "long", "SHORT").
     */
    public void setDirection(String rawDir) {
        direction = rawDir.substring(0, 1).toUpperCase() + rawDir.substring(1).toLowerCase();
    }

    /**
     * Sets the entry price.
     *
     * @param entryPrice The new entry price.
     */
    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    /**
     * Sets the exit price.
     *
     * @param exitPrice The new exit price.
     */
    public void setExitPrice(double exitPrice) {
        this.exitPrice = exitPrice;
    }

    /**
     * Sets the stop loss price.
     *
     * @param stopLossPrice The new stop loss price.
     */
    public void setStopLossPrice(double stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    /**
     * Sets the trade strategy.
     *
     * @param strategy The new strategy description.
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public boolean equals(Object trade) {
        if (this == trade) {
            return true;
        }
        if (!(trade instanceof Trade other)) {
            return false;
        }
        return ticker.equals(other.ticker)
                && date.equals(other.date)
                && direction.equals(other.direction)
                && Double.compare(entryPrice, other.entryPrice) == 0
                && Double.compare(exitPrice, other.exitPrice) == 0
                && Double.compare(stopLossPrice, other.stopLossPrice) == 0
                && strategy.equals(other.strategy);
    }
}
