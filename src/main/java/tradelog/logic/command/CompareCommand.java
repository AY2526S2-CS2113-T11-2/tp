package tradelog.logic.command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Displays performance metrics grouped by strategy.
 */
public class CompareCommand extends Command {

    private static final Logger logger = Logger.getLogger(CompareCommand.class.getName());

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
            logger.log(Level.INFO, "Compare command executed on an empty trade list.");
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

        assert !strategyComparison.isEmpty() : "Strategy comparison should contain at least one entry";

        logger.log(Level.INFO, "Displaying comparison for {0} strategies.",
                strategyComparison.size());

        ui.showStrategyComparison(strategyComparison);
    }
}
