package tradelog.logic.command;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to delete an existing trade from the TradeLog.
 */
public class DeleteCommand extends Command {

    private final int tradeIndex;

    /**
     * Constructs a DeleteCommand by parsing and validating the raw arguments string.
     *
     * @param arguments The raw string after the "delete" command word.
     * @throws TradeLogException If the index is missing, blank, or not a valid positive integer.
     */
    public DeleteCommand(String arguments) throws TradeLogException {
        String trimmedArgs = arguments.trim();
        if (trimmedArgs.isEmpty()) {
            throw new TradeLogException("Missing trade index for delete command.");
        }
        try {
            tradeIndex = Integer.parseInt(trimmedArgs);
            if (tradeIndex <= 0) {
                throw new TradeLogException("Trade index must be a positive integer.");
            }
        } catch (NumberFormatException e) {
            throw new TradeLogException("Trade index must be a valid integer.");
        }

    }

    /**
     * Executes the delete command by removing the trade at the specified index.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null when executing delete";
        assert ui != null : "Ui should not be null when executing delete";
        assert tradeIndex > 0 : "tradeIndex should be a positive integer";

        int initialSize = tradeList.size();
        try {
            UndoCommand.saveState(tradeList);
            Trade deletedTrade = tradeList.deleteTrade(tradeIndex - 1);

            assert deletedTrade != null : "Deleted trade should not be null";
            assert tradeList.size() == initialSize - 1 : "TradeList size should decrease by 1 after deletion";

            ui.printTrade(deletedTrade);
            ui.showTradeDeleted();
        } catch (IndexOutOfBoundsException e) {
            ui.showError("Trade index does not exist!");
        }
    }
}
