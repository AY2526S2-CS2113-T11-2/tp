package tradelog.logic.command;

import tradelog.exception.TradeLogException;
import tradelog.logic.parser.ArgumentTokeniser;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.util.Map;

/**
 * Represents a command to edit an existing trade in the TradeLog.
 * Supports partial updates where only specified fields are modified.
 */
public class EditCommand extends Command {

    /** All possible prefixes that can be used for editing. */
    private static final String[] ACCEPTED_PREFIXES = {
            "t/",
            "d/",
            "dir/",
            "e/",
            "x/",
            "s/",
            "o/",
            "strat/"
    };

    private final int targetIndex;
    private final Map<String, String> parsedArgs;

    /**
     * Constructs a EditCommand by parsing the index and optional arguments.
     *
     * @param arguments The raw string containing the index and optional prefixes.
     * @throws TradeLogException If the index is missing, invalid, or no fields are provided.
     */
    public EditCommand(String arguments) throws TradeLogException {
        String trimmedArgs = arguments.trim();
        if (trimmedArgs.isEmpty()) {
            throw new TradeLogException("Please specify a trade index to edit.");
        }

        // Split index from the rest of the prefixes
        String[] parts = trimmedArgs.split(" ", 2);
        try {
            this.targetIndex = Integer.parseInt(parts[0]) - 1; // Zero-based indexing
        } catch (NumberFormatException e) {
            throw new TradeLogException("The trade index must be a valid number.");
        }

        // Tokenise the remaining optional arguments
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
                System.out.println("Error: Trade index out of bounds.");
                return;
            }

            Trade tradeToEdit = tradeList.getTrade(targetIndex);

            // Update only specified fields
            if (parsedArgs.containsKey("t/")) {
                tradeToEdit.setTicker(parsedArgs.get("t/").toUpperCase());
            }
            if (parsedArgs.containsKey("d/")) {
                tradeToEdit.setDate(parsedArgs.get("d/"));
            }
            if (parsedArgs.containsKey("dir/")) {
                String rawDir = parsedArgs.get("dir/");
                String direction = rawDir.substring(0, 1).toUpperCase() + rawDir.substring(1).toLowerCase();
                tradeToEdit.setDirection(direction);
            }
            if (parsedArgs.containsKey("e/")) {
                tradeToEdit.setEntryPrice(Double.parseDouble(parsedArgs.get("e/")));
            }
            if (parsedArgs.containsKey("x/")) {
                tradeToEdit.setExitPrice(Double.parseDouble(parsedArgs.get("x/")));
            }
            if (parsedArgs.containsKey("s/")) {
                tradeToEdit.setStopLossPrice(Double.parseDouble(parsedArgs.get("s/")));
            }
            if (parsedArgs.containsKey("o/")) {
                tradeToEdit.setOutcome(parsedArgs.get("o/"));
            }
            if (parsedArgs.containsKey("strat/")) {
                tradeToEdit.setStrategy(parsedArgs.get("strat/"));
            }

            System.out.println("Trade " + (targetIndex + 1) + " updated successfully.");
            System.out.println(tradeToEdit.toSummaryString());

        } catch (NumberFormatException e) {
            System.out.println("Error: Numeric fields (Entry/Exit/Stop) must be valid numbers!");
        }
    }
}
