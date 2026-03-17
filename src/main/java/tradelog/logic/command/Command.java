package tradelog.logic.command;

import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Abstract base class for all commands.
 * Every command must implement {@code execute}.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    public abstract void execute(TradeList tradeList, Ui ui, Storage storage);

    /**
     * Returns true if this command signals the application to exit.
     * Override in ExitCommand to return true.
     *
     * @return false by default.
     */
    public boolean isExit() {
        return false;
    }
}
