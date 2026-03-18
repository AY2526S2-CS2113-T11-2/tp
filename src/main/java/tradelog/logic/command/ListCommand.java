package tradelog.logic.command;

import tradelog.model.TradeList;
import tradelog.ui.Ui;
import tradelog.storage.Storage;

/**
 * Represents a command entity for the listing of trades.
 * This class triggers the display logic within the UI to show all logged trades
 * to the user in a formatted, numbered list.
 */
public class ListCommand extends Command {

    /**
     * Executes the listing command by delegating the display of the current
     * trade log to the UI handler.
     *
     * @param trades  The list of trades to be displayed.
     * @param ui      The user interface handler responsible for formatting the output.
     * @param storage The storage handler (unused in this specific command).
     */
    @Override
    public void execute(TradeList trades, Ui ui, Storage storage) {
        ui.printTradeList(trades);
    }
}