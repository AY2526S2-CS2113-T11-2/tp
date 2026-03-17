package tradelog.logic.command;

import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Command to display all logged trades.
 * Corresponds to the user command: {@code list}
 */
public class CommandListing extends Command {

    /**
     * Executes the list command by printing all trades via the UI.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler used to print the trade list.
     * @param storage   Not used by this command.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        ui.printTradeList(tradeList);
    }
}