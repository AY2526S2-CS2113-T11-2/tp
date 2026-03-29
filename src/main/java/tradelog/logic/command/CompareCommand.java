package tradelog.logic.command;

import java.util.LinkedHashMap;
import java.util.Map;

import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Displays performance metrics grouped by strategy.
 */
public class CompareCommand extends Command {

    /**
     * Executes the compare command by aggregating trade metrics per strategy.
     *
     * @param tradeList The current list of trades.
     * @param ui        The UI handler for output.
     * @param storage   The storage handler for persistence.
     */
    @Override
    public void execute(TradeList tradeList, Ui ui, Storage storage) {
        assert tradeList != null : "TradeList should not be null when executing compare";
        assert ui != null : "Ui should not be null when executing compare";

        if (tradeList.isEmpty()) {
            ui.showSummaryEmpty();
            return;
        }

        Map<String, StrategyStats> strategyComparison = new LinkedHashMap<>();

        for (int i = 0; i < tradeList.size(); i++) {
            Trade trade = tradeList.getTrade(i);
            assert trade != null : "Trade at index " + i + " should not be null";

            String strategy = trade.getStrategy();
            StrategyStats strategyStats = strategyComparison.computeIfAbsent(
                    strategy, unused -> new StrategyStats());
            strategyStats.addTrade(trade.getRiskRewardRatio());
        }

        ui.showStrategyComparison(strategyComparison);
    }
}
