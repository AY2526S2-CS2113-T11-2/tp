package tradelog.logic.command;

import java.util.ArrayList;
import java.util.List;

import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Represents a command that reverts the most recent mutating command.
 * Only one level of undo is supported.
 */
public class UndoCommand extends Command {

    /** Snapshot of the TradeList before the most recent mutating command. */
    private static List<Trade> previousState;

    /**
     * Saves a deep copy of the current TradeList so that it can be restored later.
     *
     * @param tradeList The current list of trades.
     */
    public static void saveState(TradeList tradeList) {
        assert tradeList != null : "TradeList should not be null when saving undo state";

        previousState = new ArrayList<>();
        for (int i = 0; i < tradeList.size(); i++) {
            previousState.add(copyTrade(tradeList.getTrade(i)));
        }

        assert previousState.size() == tradeList.size()
                : "Saved undo state should match current TradeList size";
    }

    /**
     * Executes the undo command by restoring the TradeList
     * to the previously saved state.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null when executing undo";
        assert ui != null : "Ui should not be null when executing undo";

        if (previousState == null) {
            ui.showUndoUnavailable();
            return;
        }

        // Clear current list from the back
        while (!tradeList.isEmpty()) {
            tradeList.deleteTrade(tradeList.size() - 1);
        }

        // Restore saved state
        for (Trade trade : previousState) {
            tradeList.addTrade(copyTrade(trade));
        }

        // One-step undo only
        previousState = null;

        ui.showUndoSuccess();
    }

    /**
     * Creates a deep copy of a Trade.
     *
     * @param trade The trade to copy.
     * @return A copied Trade instance.
     */
    private static Trade copyTrade(Trade trade) {
        assert trade != null : "Trade to copy should not be null";

        return new Trade(
                trade.getTicker(),
                trade.getDate(),
                trade.getDirection(),
                trade.getEntryPrice(),
                trade.getExitPrice(),
                trade.getStopLossPrice(),
                trade.getOutcome(),
                trade.getStrategy()
        );
    }
}
