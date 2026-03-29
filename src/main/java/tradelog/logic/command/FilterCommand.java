package tradelog.logic.command;

import java.util.HashMap;
import java.util.Arrays;

import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.logic.parser.ParserUtil;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Command to filter trades by ticker, strategy, and/or date.
 */
public class FilterCommand extends Command {

    public static final String[] PREFIXES = {"t/", "strat/", "d/"};

    private final String ticker;
    private final String strategy;
    private final String date;
    private final boolean isPartial;

    /**
     * Constructs a FilterCommand by parsing the arguments string.
     *
     * @param arguments The user-provided arguments after "filter".
     * @throws TradeLogException If no filter values are provided.
     */
    public FilterCommand(String arguments) throws TradeLogException {
        HashMap<String, String> parsedArgs = ArgumentTokeniser.tokenise(arguments, PREFIXES);

        ticker = ParserUtil.parseTicker(parsedArgs.getOrDefault("t/", ""));
        strategy = ParserUtil.parseStrategy(parsedArgs.getOrDefault("strat/", ""));
        date = parsedArgs.getOrDefault("d/", "").trim();
        isPartial = Arrays.asList(arguments.split(" ")).contains("-p");

        if (ticker.isEmpty() && strategy.isEmpty() && date.isEmpty()) {
            throw new TradeLogException("Use at least one filter: t/<ticker>, strat/<strategy>, d/<date>");
        }
    }

    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null";
        assert ui != null : "Ui should not be null";

        java.util.List<Integer> matchingIndices = new java.util.ArrayList<>();
        TradeList filteredTrades = new TradeList();

        for (int i = 0; i < tradeList.size(); i++) {
            boolean matchesTicker;
            boolean matchesStrategy;
            boolean matchesDate;
            Trade trade = tradeList.getTrade(i);
            if (isPartial) {
                matchesTicker = ticker.isEmpty() || trade.getTicker().contains(ticker);
                matchesStrategy = strategy.isEmpty() || 
                        trade.getStrategy().toLowerCase().contains(strategy.toLowerCase());
                matchesDate = date.isEmpty() || trade.getDate().contains(date);
            } else {
                matchesTicker = ticker.isEmpty() || trade.getTicker().equals(ticker);
                matchesStrategy = strategy.isEmpty() || trade.getStrategy().equalsIgnoreCase(strategy);
                matchesDate = date.isEmpty() || trade.getDate().equals(date);
            }

            if (matchesTicker && matchesStrategy && matchesDate) {
                matchingIndices.add(i);
                filteredTrades.addTrade(trade);
            }
        }

        if (matchingIndices.isEmpty()) {
            ui.showMessage("No trades match the filter criteria.");
        } else {
            ui.showLine();
            for (int index : matchingIndices) {
                System.out.println((index + 1) + ". " + tradeList.getTrade(index));
            }
            ui.showLine();
            SummaryCommand summaryCommand = new SummaryCommand();
            summaryCommand.execute(filteredTrades, ui, storage);
        }
    }
}
