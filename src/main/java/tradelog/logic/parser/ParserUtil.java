package tradelog.logic.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import tradelog.exception.TradeLogException;

/**
 * Utility class containing methods for parsing and validating specific data types.
 * Helps keep the main Parser class clean and focused on routing commands.
 */
public class ParserUtil {

    private static final Map<String, String> STRATEGY_SHORTCUTS = createStrategyShortcuts();
    private static final Map<String, String> CANONICAL_STRATEGY_NAMES = createCanonicalStrategyNames();
    private static final Logger logger = Logger.getLogger(ParserUtil.class.getName());

    private ParserUtil() {
        // Utility class; prevent instantiation.
    }

    private static Map<String, String> createStrategyShortcuts() {
        Map<String, String> shortcuts = new LinkedHashMap<>();
        shortcuts.put("BB", "Breakout");
        shortcuts.put("TBF", "Trend Bar Failure");
        shortcuts.put("PB", "Pullback");
        shortcuts.put("MTR", "Major Trend Reversal");
        shortcuts.put("HOD", "High of Day");
        shortcuts.put("LOD", "Low of Day");
        shortcuts.put("MR", "Mean Reversion");
        shortcuts.put("TR", "Trading Range");
        shortcuts.put("DB", "Double Bottom");
        shortcuts.put("DT", "Double Top");
        return Collections.unmodifiableMap(shortcuts);
    }

    private static Map<String, String> createCanonicalStrategyNames() {
        Map<String, String> canonicalStrategyNames = new HashMap<>();
        for (String canonicalStrategyName : STRATEGY_SHORTCUTS.values()) {
            canonicalStrategyNames.put(toStrategyLookupKey(canonicalStrategyName), canonicalStrategyName);
        }
        return Collections.unmodifiableMap(canonicalStrategyNames);
    }

    private static String normalizeStrategySpacing(String strategy) {
        return strategy.trim().replaceAll("\\s+", " ");
    }

    private static String toStrategyLookupKey(String strategy) {
        return normalizeStrategySpacing(strategy).toUpperCase();
    }

    /**
     * Parses a string representation of a price into a double.
     * Validates that the input is a positive numerical value.
     *
     * @param priceString The string representing the price.
     * @param fieldName   The name of the field (e.g., "Entry", "Exit") for error messages.
     * @return The parsed double value.
     * @throws TradeLogException If the string is null/empty, not a valid number, or not positive.
     */
    public static double parsePrice(String priceString, String fieldName) throws TradeLogException {
        if (priceString == null || priceString.trim().isEmpty()) {
            throw new TradeLogException("The " + fieldName + " price cannot be empty.");
        }
        double price;
        try {
            price = Double.parseDouble(priceString.trim());
        } catch (NumberFormatException e) {
            throw new TradeLogException("The " + fieldName + " price must be a valid number!");
        }
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new TradeLogException("The " + fieldName + " price must be a valid number!");
        }
        if (price <= 0) {
            throw new TradeLogException("The " + fieldName + " price must be a positive number.");
        }
        return price;
    }

    /**
     * Parses and validates a date string in YYYY-MM-DD format.
     *
     * @param dateString The raw date string from the user.
     * @return The trimmed date string if valid.
     * @throws TradeLogException If the date is not in YYYY-MM-DD format or is not a real date.
     */
    public static String parseDate(String dateString) throws TradeLogException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new TradeLogException("Date cannot be empty.");
        }
        try {
            LocalDate.parse(dateString.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new TradeLogException("Invalid date \"" + dateString.trim()
                    + "\". Please use YYYY-MM-DD format (e.g. 2026-02-18).");
        }
        return dateString.trim();
    }

    /**
     * Parses and formats a ticker symbol to be consistently uppercase.
     *
     * @param ticker The raw ticker string.
     * @return The formatted uppercase ticker.
     * @throws TradeLogException If the ticker is empty.
     */
    public static String parseTicker(String ticker) throws TradeLogException {
        String trimmedTicker = ticker.trim();
        if (trimmedTicker.isEmpty()) {
            throw new TradeLogException("Ticker cannot be empty.");
        }
        return trimmedTicker.toUpperCase();
    }

    /**
     * Parses and validates the trade direction.
     * Validates that the input is not empty and matches 'long' or 'short'.
     *
     * @param direction The raw direction string.
     * @return The formatted direction ("Long" or "Short").
     * @throws TradeLogException If the direction is empty or is not 'long' or 'short'.
     */
    public static String parseDirection(String direction) throws TradeLogException {
        if (direction == null || direction.trim().isEmpty()) {
            throw new TradeLogException("Direction cannot be empty (must be 'long' or 'short').");
        }
        String dir = direction.trim().toLowerCase();
        if (dir.equals("long") || dir.equals("short")) {
            return dir.substring(0, 1).toUpperCase() + dir.substring(1);
        }
        throw new TradeLogException("Direction must be exactly 'long' or 'short'!");
    }

    private static String canonicalizeStrategyIfKnown(String strategy) {
        String normalizedStrategy = normalizeStrategySpacing(strategy);
        String strategyLookupKey = toStrategyLookupKey(strategy);
        String canonicalStrategy = STRATEGY_SHORTCUTS.get(strategyLookupKey);

        if (canonicalStrategy != null) {
            logger.log(Level.INFO, "Expanded strategy shortcut {0} to {1}",
                    new Object[] {normalizedStrategy, canonicalStrategy});
            return canonicalStrategy;
        }

        canonicalStrategy = CANONICAL_STRATEGY_NAMES.get(strategyLookupKey);
        if (canonicalStrategy != null && !canonicalStrategy.equals(normalizedStrategy)) {
            logger.log(Level.INFO, "Canonicalized strategy name {0} to {1}",
                    new Object[] {normalizedStrategy, canonicalStrategy});
        }

        return canonicalStrategy != null ? canonicalStrategy : normalizedStrategy;
    }

    /**
     * Expands a known strategy shortcut or canonicalizes a known full strategy name.
     *
     * @param strategy The raw strategy input from the user.
     * @return The canonical strategy name.
     * @throws TradeLogException If the strategy is blank or not one of the supported values.
     */
    public static String parseStrategy(String strategy) throws TradeLogException {
        assert strategy != null : "Strategy should not be null";

        String normalizedStrategy = normalizeStrategySpacing(strategy);
        if (normalizedStrategy.isEmpty()) {
            throw new TradeLogException("Strategy cannot be empty.");
        }

        String canonicalStrategy = canonicalizeStrategyIfKnown(normalizedStrategy);
        if (!CANONICAL_STRATEGY_NAMES.containsValue(canonicalStrategy)) {
            throw new TradeLogException("Invalid strategy. Use one of the supported strategy shortcuts "
                    + "or canonical strategy names shown at startup.");
        }

        return canonicalStrategy;
    }

    /**
     * Canonicalizes a strategy name if it is recognised, otherwise returns the trimmed input.
     * This is intended for existing stored data where unknown legacy values should not break features.
     *
     * @param strategy The stored strategy value.
     * @return The canonical known strategy name, or the trimmed original string if unknown.
     */
    public static String canonicalizeStoredStrategy(String strategy) {
        assert strategy != null : "Strategy should not be null";
        return canonicalizeStrategyIfKnown(strategy);
    }

    /**
     * Returns the supported strategy shortcuts in display order.
     *
     * @return An unmodifiable map of strategy shortcuts to full strategy names.
     */
    public static Map<String, String> getStrategyShortcuts() {
        return STRATEGY_SHORTCUTS;
    }

    /**
     * Validates that the entry price and stop loss price are not equal.
     *
     * @param entryPrice    The entry price of the trade.
     * @param stopLossPrice The stop loss price of the trade.
     * @throws TradeLogException If entry price equals stop loss price.
     */
    public static void validatePrices(double entryPrice, double stopLossPrice)
            throws TradeLogException {
        if (entryPrice == stopLossPrice) {
            throw new TradeLogException(
                    "Entry price and stop loss price cannot have the same value.");
        }
    }

    /**
     * Validates that the stop loss price is on the correct side of the entry price.
     *
     * @param direction     The trade direction ("long" or "short").
     * @param entryPrice    The entry price of the trade.
     * @param stopLossPrice The stop loss price of the trade.
     * @throws TradeLogException If the stop loss is on the wrong side of the entry price.
     */
    public static void validateStopLoss(String direction, double entryPrice, double stopLossPrice)
            throws TradeLogException {
        if (direction.equalsIgnoreCase("long") && stopLossPrice > entryPrice) {
            throw new TradeLogException(
                    "Invalid Trade: For a Long position, Stop Loss must be below Entry Price.");
        }
        if (direction.equalsIgnoreCase("short") && stopLossPrice < entryPrice) {
            throw new TradeLogException(
                    "Invalid Trade: For a Short position, Stop Loss must be above Entry Price.");
        }
    }
}
