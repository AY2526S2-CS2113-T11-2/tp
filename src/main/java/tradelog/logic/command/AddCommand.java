package tradelog.logic.command;

import java.util.HashMap;

import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.logic.parser.ParserUtil;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to add a new trade to the TradeLog.
 * Handles parsing, strict validation of user arguments, and executing the addition.
 */
public class AddCommand extends Command {

    /** The required prefixes for the add command. */
    public static final String[] REQUIRED_PREFIXES = {
        "t/", "d/", "dir/", "e/", "x/", "s/", "o/", "strat/"};

    private final Trade addTrade;

    /**
     * Constructs an AddCommand by parsing and validating the raw arguments string.
     *
     * @param arguments The raw string after the "add" command word.
     * @throws TradeLogException If any required prefix is missing or blank.
     */
    public AddCommand(String arguments) throws TradeLogException {
        assert arguments != null : "Raw arguments string should not be null";
        HashMap<String, String> parsedArgs = ArgumentTokeniser.tokenise(arguments, REQUIRED_PREFIXES);
        for (String prefix : REQUIRED_PREFIXES) {
            if (!parsedArgs.containsKey(prefix)) {
                throw new TradeLogException("Missing required prefix: " + prefix);
            }
            if (parsedArgs.get(prefix).trim().isEmpty()) {
                throw new TradeLogException("The value for " + prefix + " cannot be empty.");
            }
        }

        double entryPrice = ParserUtil.parsePrice(parsedArgs.get("e/"), "Entry");
        double exitPrice = ParserUtil.parsePrice(parsedArgs.get("x/"), "Exit");
        double stopLossPrice = ParserUtil.parsePrice(parsedArgs.get("s/"), "Stop Loss");

        ParserUtil.validatePrices(entryPrice, stopLossPrice);
        ParserUtil.validateStopLoss(parsedArgs.get("dir/").trim().toLowerCase(), entryPrice, stopLossPrice);

        String ticker = ParserUtil.parseTicker(parsedArgs.get("t/"));
        String direction = ParserUtil.parseDirection(parsedArgs.get("dir/"));
        String date = parsedArgs.get("d/").trim();
        String outcome = parsedArgs.get("o/").trim();
        String strategy = ParserUtil.parseStrategy(parsedArgs.get("strat/"));

        this.addTrade = new Trade(ticker, date, direction,
                entryPrice, exitPrice, stopLossPrice, outcome, strategy);

        assert addTrade.getTicker().equals(ticker) : "Ticker should match parsed value";
        assert addTrade.getEntryPrice() == entryPrice : "Entry price should match parsed value";
        assert addTrade.getStrategy().equals(strategy) : "Last field check to ensure full assignment";
    }

    /**
     * Executes the add command by adding the trade to the TradeList
     * and displaying the trade summary to the user.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null when executing add";
        assert ui != null : "Ui should not be null when executing add";
        assert addTrade != null : "addTrade object should have been successfully created in constructor";
        int initialSize = tradeList.size();

        tradeList.addTrade(addTrade);

        assert tradeList.size() == initialSize + 1 : "TradeList size should increase by 1 after adding";

        Trade lastTrade = tradeList.getTrade(tradeList.size() - 1);
        assert lastTrade.getTicker().equals(addTrade.getTicker()) : "Last trade ticker should match added trade";
        assert lastTrade.getEntryPrice() == addTrade.getEntryPrice() : "Last trade entryPrice should match added trade";
        assert lastTrade.getStrategy().equals(addTrade.getStrategy()) : "Last trade strategy should match added trade";

        ui.printTrade(addTrade);
        ui.showTradeAdded();
    }
}
