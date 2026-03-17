package tradelog.logic.command;

<<<<<<< HEAD:src/main/java/tradelog/logic/command/CommandListing.java
// Tells the UI to print all trades.
public class CommandListing {
=======
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Command to display all logged trades.
 * Corresponds to the user command: {@code list}
 */
public class ListCommand extends Command {

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
>>>>>>> 7d171066f59a9f887c4c191007ee068dc04ea1eb:src/main/java/tradelog/logic/command/ListCommand.java
}
