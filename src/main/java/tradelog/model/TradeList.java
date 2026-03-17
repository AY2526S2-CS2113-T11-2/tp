package tradelog.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of trades in memory.
 * Handles adding, retrieving, deleting, and sizing of trades.
 */
public class TradeList {
    private final List<Trade> trades;

    /** Constructs an empty TradeList. */
    public TradeList() {
        this.trades = new ArrayList<>();
    }

    /**
     * Adds a trade to the list.
     *
     * @param trade The trade to add.
     */
    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    /**
     * Returns the trade at the specified 0-based index.
     *
     * @param index 0-based index of the trade.
     * @return The trade at that index.
     */
    public Trade getTrade(int index) {
        return trades.get(index);
    }

    /**
     * Returns the number of trades currently stored.
     *
     * @return The size of the trade list.
     */
    public int size() {
        return trades.size();
    }

    /**
     * Returns true if there are no trades in the list.
     *
     * @return true if the list is empty.
     */
    public boolean isEmpty() {
        return trades.isEmpty();
    }
}
