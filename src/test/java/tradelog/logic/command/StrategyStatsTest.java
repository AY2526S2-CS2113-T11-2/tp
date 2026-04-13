package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test suite for {@link StrategyStats}.
 */
public class StrategyStatsTest {

    @Test
    public void addTrade_mixedResults_calculatesMetricsCorrectly() {
        StrategyStats strategyStats = new StrategyStats();

        strategyStats.addTrade(2.0);
        strategyStats.addTrade(-1.0);
        strategyStats.addTrade(0.0);

        assertEquals(3, strategyStats.getTradeCount());
        assertEquals(33.333, strategyStats.getWinRate(), 0.001);
        assertEquals(2.0, strategyStats.getAverageWin(), 0.001);
        assertEquals(1.0, strategyStats.getAverageLoss(), 0.001);
        assertEquals(0.333, strategyStats.getExpectedValue(), 0.001);
    }

    @Test
    public void addTrade_onlyBreakevenTrades_returnsZeroMetrics() {
        StrategyStats strategyStats = new StrategyStats();

        strategyStats.addTrade(0.0);
        strategyStats.addTrade(0.0);

        assertEquals(2, strategyStats.getTradeCount());
        assertEquals(0.0, strategyStats.getWinRate(), 0.001);
        assertEquals(0.0, strategyStats.getAverageWin(), 0.001);
        assertEquals(0.0, strategyStats.getAverageLoss(), 0.001);
        assertEquals(0.0, strategyStats.getExpectedValue(), 0.001);
    }

    @Test
    public void addTrade_onlyLosses_calculatesLossMetricsCorrectly() {
        StrategyStats strategyStats = new StrategyStats();

        strategyStats.addTrade(-1.0);
        strategyStats.addTrade(-2.0);

        assertEquals(2, strategyStats.getTradeCount());
        assertEquals(0.0, strategyStats.getWinRate(), 0.001);
        assertEquals(0.0, strategyStats.getAverageWin(), 0.001);
        assertEquals(1.5, strategyStats.getAverageLoss(), 0.001);
        assertEquals(-1.5, strategyStats.getExpectedValue(), 0.001);
    }
}
