package tradelog.logic.command;

import java.util.HashMap;
import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to add a new trade to the TradeLog.
 * Handles parsing, strict validation of user arguments, and executing the addition.
 */
public class AddCommand extends Command{
    public static final String[] REQUIRED_PREFIXES = {"t/", "d/", "dir/", "e/", "x/", "s/", "o/", "strat/"};
    private final Trade addTrade;

    /**
     * Constructs an AddCommand by parsing and validating the raw arguments string.
     * Strictly checks that all required prefixes are present and not empty.
     *
     * @param arguments The raw string after the "add" command word.
     * @throws TradeLogException If any required prefix is missing or blank.
     */
    public AddCommand(String arguments) throws TradeLogException {
        HashMap<String, String> parsedArgs = ArgumentTokeniser.tokenise(arguments, REQUIRED_PREFIXES);

        for (String prefix : REQUIRED_PREFIXES){
            if (!parsedArgs.containsKey(prefix)){
                throw new TradeLogException("Missing required prefix: " + prefix);
            }
            if (parsedArgs.get(prefix).trim().isEmpty()){
                throw new TradeLogException("The value for " + prefix + " cannot be empty.");
            }
        }

        double entryPrice;
        double exitPrice;
        double stopLossPrice;
        try {
            entryPrice = Double.parseDouble(parsedArgs.get("e/"));
            exitPrice = Double.parseDouble(parsedArgs.get("x/"));
            stopLossPrice = Double.parseDouble(parsedArgs.get("s/"));
        } catch (NumberFormatException e) {
            throw new TradeLogException("Entry, Exit, and Stop Loss must be valid numbers!");
        }

        if (entryPrice == exitPrice) {
            throw new  TradeLogException("Entry price and stop loss price cannot have the same value.");
        }

        String ticker = parsedArgs.get("t/").trim().toUpperCase();

        String rawDir = parsedArgs.get("dir/").trim().toLowerCase();
        if (!rawDir.equals("long") && !rawDir.equals("short")) {
            throw new TradeLogException("Direction must be exactly 'long' or 'short'!");
        }
        String direction = rawDir.substring(0, 1).toUpperCase() + rawDir.substring(1);

        String date = parsedArgs.get("d/").trim();
        String outcome = parsedArgs.get("o/").trim();
        String strategy = parsedArgs.get("strat/").trim();

        this.addTrade = new Trade(ticker, date, direction, entryPrice, exitPrice, stopLossPrice, outcome, strategy);
        
        // Invariant: addTrade should be properly initialized
        assert addTrade != null : "Trade object should not be null";
        assert addTrade.getTicker().equals(ticker) : "Ticker should match parsed value";
        assert addTrade.getEntryPrice() == entryPrice : "Entry price should match parsed value";
    }

    /**
     * Executes the add command by creating a new Trade object, adding it to the TradeList,
     * and displaying the trade summary to the user.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        int initialSize = tradeList.size();
        
        tradeList.addTrade(addTrade);

        // Invariant: TradeList size should increase by 1
        assert tradeList.size() == initialSize + 1 : "TradeList size should increase by 1 after adding";
        
        // Invariant: The last trade should be the one just added
        Trade lastTrade = tradeList.getTrade(tradeList.size() - 1);
        assert lastTrade.getTicker().equals(addTrade.getTicker()) : "Last trade ticker should match added trade";
        assert lastTrade.getEntryPrice() == addTrade.getEntryPrice() : "Last trade entry price should match added trade";

        ui.showLine();
        System.out.println("Trade successfully added.");
        System.out.println(addTrade.toSummaryString());
        ui.showLine();
    }
}
