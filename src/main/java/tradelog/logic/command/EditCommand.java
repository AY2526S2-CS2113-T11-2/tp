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
        parsedArgs = ArgumentTokeniser.tokenise(prefixArgs, ACCEPTED_PREFIXES);
        if (parsedArgs.isEmpty()) {
            throw new TradeLogException("At least one field must be specified to edit.");
        }
    }

    /**
     * Executes the edit command.
     * Uses temporary variables to validate the entire updated state before
     * modifying the original Trade object to maintain data atomicity.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for user feedback.
     * @param storage   The storage handler for data persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null when executing edit";
        assert ui != null : "Ui should not be null when executing edit";
        assert storage != null : "storage should not be null when executing edit";
        assert targetIndex >= 0 : "targetIndex should be 0 or greater (0-based)";

        if (targetIndex >= tradeList.size()) {
            throw new TradeLogException("Trade index out of bounds.");
        }

        Trade tradeToEdit = tradeList.getTrade(targetIndex);
        assert tradeToEdit != null : "Trade object to edit should not be null";

        // 1. Parse and stage updated values in local variables (Pre-computation)
        String newTicker = parsedArgs.containsKey("t/")
                ? ParserUtil.parseTicker(parsedArgs.get("t/")) : tradeToEdit.getTicker();
        String newDate = parsedArgs.containsKey("d/")
                ? parsedArgs.get("d/") : tradeToEdit.getDate();
        String newDir = parsedArgs.containsKey("dir/")
                ? ParserUtil.parseDirection(parsedArgs.get("dir/")) : tradeToEdit.getDirection();
        double newEntry = parsedArgs.containsKey("e/")
                ? ParserUtil.parsePrice(parsedArgs.get("e/"), "Entry") : tradeToEdit.getEntryPrice();
        double newExit = parsedArgs.containsKey("x/")
                ? ParserUtil.parsePrice(parsedArgs.get("x/"), "Exit") : tradeToEdit.getExitPrice();
        double newStop = parsedArgs.containsKey("s/")
                ? ParserUtil.parsePrice(parsedArgs.get("s/"), "Stop Loss") : tradeToEdit.getStopLossPrice();
        String newOutcome = parsedArgs.getOrDefault("o/", tradeToEdit.getOutcome());
        String newStrat = parsedArgs.containsKey("strat/")
                ? ParserUtil.parseStrategy(parsedArgs.get("strat/"))
                : tradeToEdit.getStrategy();

        // 2. Business Logic Validation (Reusing teammate's methods)
        // Step A: Ensure entry and stop loss are not the same
        ParserUtil.validatePrices(newEntry, newStop);

        // Step B: Ensure stop loss is on the correct side
        // Note: teammate's validateStopLoss expects lowercase "long"/"short"
        ParserUtil.validateStopLoss(newDir.toLowerCase(), newEntry, newStop);

        // Save state only after all parsing/validation succeeds
        UndoCommand.saveState(tradeList);

        // 3. Atomicity: Commit changes only if ALL previous steps (Parsing & Validation) passed
        tradeToEdit.setTicker(newTicker);
        tradeToEdit.setDate(newDate);
        tradeToEdit.setDirection(newDir);
        tradeToEdit.setEntryPrice(newEntry);
        tradeToEdit.setExitPrice(newExit);
        tradeToEdit.setStopLossPrice(newStop);
        tradeToEdit.setOutcome(newOutcome);
        tradeToEdit.setStrategy(newStrat);

        ui.showTradeUpdated(targetIndex + 1);
        ui.printTrade(tradeToEdit);
    }
}
