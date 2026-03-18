package tradelog.logic.command;

import java.util.Map;

import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.logic.parser.ParserUtil;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to edit an existing trade in the TradeLog.
 * Supports partial updates where only specified fields are modified.
 */
public class EditCommand extends Command {

    /** All possible prefixes that can be used for editing. */
    private static final String[] ACCEPTED_PREFIXES = {
        "t/", "d/", "dir/", "e/", "x/", "s/", "o/", "strat/"
    };

    private final int targetIndex;
    private final Map<String, String> parsedArgs;

    /**
     * Constructs an EditCommand by parsing the index and optional arguments.
     *
     * @param arguments The raw string containing the index and optional prefixes.
     * @throws TradeLogException If the index is missing, invalid, or no fields are provided.
     */
    public EditCommand(String arguments) throws TradeLogException {
        String trimmedArgs = arguments.trim();
        if (trimmedArgs.isEmpty()) {
            throw new TradeLogException("Please specify a trade index to edit.");
        }
        String[] parts = trimmedArgs.split(" ", 2);
        try {
            this.targetIndex = Integer.parseInt(parts[0]) - 1;
        } catch (NumberFormatException e) {
            throw new TradeLogException("The trade index must be a valid number.");
        }
        String prefixArgs = (parts.length > 1) ? parts[1] : "";
        this.parsedArgs = ArgumentTokeniser.tokenise(prefixArgs, ACCEPTED_PREFIXES);
        if (parsedArgs.isEmpty()) {
            throw new TradeLogException("At least one field must be specified to edit.");
        }
    }

    /**
     * Executes the edit command by updating the specified trade's fields.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        try {
            if (targetIndex < 0 || targetIndex >= tradeList.size()) {
                ui.showError("Trade index out of bounds.");
                return;
            }
            Trade tradeToEdit = tradeList.getTrade(targetIndex);

            assert tradeToEdit != null : "Retrieved trade should not be null";
            assert targetIndex < tradeList.size() : "Trade index must be within bounds";

            if (parsedArgs.containsKey("t/")) {
                tradeToEdit.setTicker(ParserUtil.parseTicker(parsedArgs.get("t/")));
                assert tradeToEdit.getTicker().equals(parsedArgs.get("t/").toUpperCase()) :
                        "Ticker should be updated";
            }
            if (parsedArgs.containsKey("d/")) {
                tradeToEdit.setDate(parsedArgs.get("d/"));
            }
            if (parsedArgs.containsKey("dir/")) {
                ParserUtil.validateStopLoss(parsedArgs.get("dir/").trim().toLowerCase(),
                        tradeToEdit.getEntryPrice(), tradeToEdit.getStopLossPrice());
                tradeToEdit.setDirection(ParserUtil.parseDirection(parsedArgs.get("dir/")));
            }
            if (parsedArgs.containsKey("e/")) {
                ParserUtil.validatePrices(ParserUtil.parsePrice(parsedArgs.get("e/"), "Entry"), 
                        tradeToEdit.getStopLossPrice());
                ParserUtil.validateStopLoss(tradeToEdit.getDirection().trim().toLowerCase(),
                        ParserUtil.parsePrice(parsedArgs.get("e/"), "Entry"), tradeToEdit.getStopLossPrice());
                tradeToEdit.setEntryPrice(ParserUtil.parsePrice(parsedArgs.get("e/"), "Entry"));
            }
            if (parsedArgs.containsKey("x/")) {
                ParserUtil.validatePrices(tradeToEdit.getEntryPrice(), ParserUtil.parsePrice(parsedArgs.get("x/"), 
                        "Exit"));
                tradeToEdit.setExitPrice(ParserUtil.parsePrice(parsedArgs.get("x/"), "Exit"));
            }
            if (parsedArgs.containsKey("s/")) {
                ParserUtil.validateStopLoss(tradeToEdit.getDirection().trim().toLowerCase(),
                        tradeToEdit.getEntryPrice(), ParserUtil.parsePrice(parsedArgs.get("s/"), "Stop Loss"));
                tradeToEdit.setStopLossPrice(ParserUtil.parsePrice(parsedArgs.get("s/"), "Stop Loss"));
            }
            if (parsedArgs.containsKey("o/")) {
                tradeToEdit.setOutcome(parsedArgs.get("o/"));
            }
            if (parsedArgs.containsKey("strat/")) {
                tradeToEdit.setStrategy(parsedArgs.get("strat/"));
            }

            ui.showTradeUpdated(targetIndex + 1);
            ui.printTrade(tradeToEdit);
        } catch (TradeLogException e) {
            ui.showError(e.getMessage());
        }
    }
}
