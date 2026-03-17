package tradelog.logic.command;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command to delete an existing trade from the TradeLog.
 * Handles parsing, strict validation of the trade index, and executing the deletion.
 */
public class DeleteCommand extends Command {
    private final int tradeIndex;

    /**
     * Constructs a DeleteCommand by parsing and validating the raw arguments string.
     * Strictly checks that the trade index is present and is a valid positive integer.
     *
     * @param arguments The raw string after the "delete" command word.
     * @throws TradeLogException If the trade index is missing, blank, or not a valid positive integer.
     */
    public DeleteCommand(String arguments) throws TradeLogException {
        arguments = arguments.trim();

        if (arguments.isEmpty()) {
            throw new TradeLogException("Missing trade index for delete command.");
        }

        try {
            tradeIndex = Integer.parseInt(arguments);

            if (tradeIndex <= 0) {
                throw new TradeLogException("Trade index must be a positive integer.");
            }
        } catch (NumberFormatException e) {
            throw new TradeLogException("Trade index must be a valid integer.");
        }
    }

    /**
     * Executes the delete command by removing the trade at the specified index
     * from the TradeList and displaying the deleted trade summary to the user.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        try {
            Trade deletedTrade = tradeList.deleteTrade(tradeIndex - 1);

            System.out.println(deletedTrade.toSummaryString());
            System.out.println("Trade successfully deleted.");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Trade index does not exist!");
        }
    }
}
