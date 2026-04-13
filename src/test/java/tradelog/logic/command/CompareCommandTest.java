package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Test suite for the {@link CompareCommand}.
 */
public class CompareCommandTest {

    private TradeList tradeList;
    private Storage dummyStorage;
    private MockUi mockUi;

    private static class MockUi extends Ui {
        boolean isShowSummaryEmptyCalled;
        Map<String, StrategyStats> capturedComparison;

        @Override
        public void showSummaryEmpty() {
            isShowSummaryEmptyCalled = true;
        }

        @Override
        public void showStrategyComparison(Map<String, StrategyStats> strategyComparison) {
            capturedComparison = strategyComparison;
        }
    }

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        dummyStorage = new Storage("dummy_compare_storage.txt");
        mockUi = new MockUi();
    }

    @Test
    public void execute_emptyList_callsShowSummaryEmpty() {
        CompareCommand command = new CompareCommand();

        command.execute(tradeList, mockUi, dummyStorage);

        assertTrue(mockUi.isShowSummaryEmptyCalled);
    }

    @Test
    public void execute_populatedList_groupsTradesByStrategy() {
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 120, 90, "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-03-02", "Long",
                100, 95, 90, "Breakout"));
        tradeList.addTrade(new Trade("MSFT", "2026-03-03", "Long",
                100, 115, 90, "Pullback"));

        CompareCommand command = new CompareCommand();
        command.execute(tradeList, mockUi, dummyStorage);

        assertEquals(2, mockUi.capturedComparison.size());

        StrategyStats breakoutStats = mockUi.capturedComparison.get("Breakout");
        assertEquals(2, breakoutStats.getTradeCount());
        assertEquals(50.0, breakoutStats.getWinRate(), 0.001);
        assertEquals(2.0, breakoutStats.getAverageWin(), 0.001);
        assertEquals(0.5, breakoutStats.getAverageLoss(), 0.001);
        assertEquals(0.75, breakoutStats.getExpectedValue(), 0.001);

        StrategyStats pullbackStats = mockUi.capturedComparison.get("Pullback");
        assertEquals(1, pullbackStats.getTradeCount());
        assertEquals(100.0, pullbackStats.getWinRate(), 0.001);
        assertEquals(1.5, pullbackStats.getAverageWin(), 0.001);
        assertEquals(0.0, pullbackStats.getAverageLoss(), 0.001);
        assertEquals(1.5, pullbackStats.getExpectedValue(), 0.001);
    }

    @Test
    public void execute_caseVariantKnownStrategy_groupsTradesUnderCanonicalName() {
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 120, 90, "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-03-02", "Long",
                100, 95, 90, "breakout"));
        tradeList.addTrade(new Trade("MSFT", "2026-03-03", "Long",
                100, 115, 90, "BB"));

        CompareCommand command = new CompareCommand();
        command.execute(tradeList, mockUi, dummyStorage);

        assertEquals(1, mockUi.capturedComparison.size());

        StrategyStats breakoutStats = mockUi.capturedComparison.get("Breakout");
        assertEquals(3, breakoutStats.getTradeCount());
        assertEquals(66.667, breakoutStats.getWinRate(), 0.001);
    }

    @Test
    public void execute_spacingVariantKnownStrategy_groupsTradesUnderCanonicalName() {
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 120, 90, "  Major   Trend Reversal  "));
        tradeList.addTrade(new Trade("TSLA", "2026-03-02", "Long",
                100, 95, 90, "mtr"));

        CompareCommand command = new CompareCommand();
        command.execute(tradeList, mockUi, dummyStorage);

        assertEquals(1, mockUi.capturedComparison.size());
        assertNotNull(mockUi.capturedComparison.get("Major Trend Reversal"));
    }

    @Test
    public void execute_unknownStoredStrategy_preservesTrimmedLegacyName() {
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long",
                100, 120, 90, "  Legacy Setup  "));

        CompareCommand command = new CompareCommand();
        command.execute(tradeList, mockUi, dummyStorage);

        assertEquals(1, mockUi.capturedComparison.size());
        assertNotNull(mockUi.capturedComparison.get("Legacy Setup"));
    }
}
