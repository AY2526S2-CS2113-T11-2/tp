package tradelog.ui;

import org.junit.jupiter.api.Test;
import tradelog.logic.command.StrategyStats;
import tradelog.model.Trade;
import tradelog.model.TradeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(buffer));
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }

    @Test
    public void printTradeList_emptyList_showsEmptyMessage() {
        Ui ui = new Ui();
        TradeList tradeList = new TradeList();
        String output = captureOutput(() -> ui.printTradeList(tradeList));
        assertTrue(output.contains("No trades logged yet."));
    }

    @Test
    public void printTradeList_oneTrade_showsTrade() {
        Ui ui = new Ui();
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-02-18",
                "Long", 180.0, 190.0, 170.0, "Breakout"));
        String output = captureOutput(() -> ui.printTradeList(tradeList));
        assertTrue(output.contains("AAPL"));
        assertTrue(output.contains("1."));
    }

    @Test
    public void printIndexedTrades_selectedIndices_showsOriginalNumbering() {
        Ui ui = new Ui();
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-02-18",
                "Long", 180.0, 190.0, 170.0, "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-02-19",
                "Short", 400.0, 380.0, 410.0, "Pullback"));

        String output = captureOutput(() -> ui.printIndexedTrades(tradeList, java.util.List.of(1)));

        assertTrue(output.contains("2."));
        assertTrue(output.contains("TSLA"));
        assertFalse(output.contains("1. AAPL"));
    }

    @Test
    public void showWelcome_containsWelcomeMessage() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showWelcome);
        assertTrue(output.contains("Welcome to TradeLog!"));
    }

    @Test
    public void showWelcome_containsCommandList() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showWelcome);
        assertTrue(output.contains(
                "Commands: add, list, edit, delete, filter, compare, summary, encrypt, undo, exit"));
    }

    @Test
    public void showWelcome_containsStrategyShortcuts() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showWelcome);
        assertTrue(output.contains("Strategy shortcuts:"));
        assertTrue(output.contains("BB = Breakout"));
        assertTrue(output.contains("MTR = Major Trend Reversal"));
    }

    @Test
    public void showGoodbye_containsGoodbyeMessage() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showGoodbye);
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    public void showError_containsErrorMessage() {
        Ui ui = new Ui();
        String output = captureOutput(() -> ui.showError("something went wrong"));
        assertTrue(output.contains("Error: something went wrong"));
    }

    @Test
    public void showMessage_containsMessage() {
        Ui ui = new Ui();
        String output = captureOutput(() -> ui.showMessage("hello"));
        assertTrue(output.contains("hello"));
    }

    @Test
    public void showTradeAdded_containsConfirmation() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showTradeAdded);
        assertTrue(output.contains("Trade successfully added."));
    }

    @Test
    public void showTradeDeleted_containsConfirmation() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showTradeDeleted);
        assertTrue(output.contains("Trade successfully deleted."));
    }

    @Test
    public void showTradeUpdated_containsConfirmation() {
        Ui ui = new Ui();
        String output = captureOutput(() -> ui.showTradeUpdated(1));
        assertTrue(output.contains("Trade 1 updated successfully."));
    }

    @Test
    public void showSummaryEmpty_containsEmptyMessage() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showSummaryEmpty);
        assertTrue(output.contains("No trades available to generate a summary."));
    }

    @Test
    public void printTrade_containsTradeSummary() {
        Ui ui = new Ui();
        Trade trade = new Trade("AAPL", "2026-02-18",
                "Long", 180.0, 190.0, 170.0, "Breakout");
        String output = captureOutput(() -> ui.printTrade(trade));
        assertTrue(output.contains("Trade Summary:"));
        assertTrue(output.contains("AAPL"));
    }

    @Test
    public void showStrategyComparison_containsStrategyMetrics() {
        Ui ui = new Ui();
        Map<String, StrategyStats> strategyComparison = new LinkedHashMap<>();
        StrategyStats breakoutStats = new StrategyStats();
        breakoutStats.addTrade(2.0);
        breakoutStats.addTrade(-1.0);
        strategyComparison.put("Breakout", breakoutStats);

        String output = captureOutput(() -> ui.showStrategyComparison(strategyComparison));

        assertTrue(output.contains("Strategy Comparison:"));
        assertTrue(output.contains("Breakout:"));
        assertTrue(output.contains("Trades: 2"));
        assertTrue(output.contains("Win Rate: 50%"));
        assertTrue(output.contains("Average Win: 2.00R"));
        assertTrue(output.contains("Average Loss: 1.00R"));
        assertTrue(output.contains("EV: +0.500R"));
    }

    @Test
    public void showSummary_negativeValues_formatsWithSingleMinusSign() {
        Ui ui = new Ui();

        String output = captureOutput(() -> ui.showSummary(2, 0.0, 0.0,
                1.25, -1.25, -2.5));

        assertTrue(output.contains("Overall EV: -1.25R"));
        assertTrue(output.contains("Total R: -2.50R"));
        assertFalse(output.contains("--1.25R"));
        assertFalse(output.contains("--2.50R"));
    }

    @Test
    public void showStrategyComparison_negativeEv_formatsWithSingleMinusSign() {
        Ui ui = new Ui();
        Map<String, StrategyStats> strategyComparison = new LinkedHashMap<>();
        StrategyStats breakoutStats = new StrategyStats();
        breakoutStats.addTrade(-1.0);
        breakoutStats.addTrade(-0.5);
        strategyComparison.put("Breakout", breakoutStats);

        String output = captureOutput(() -> ui.showStrategyComparison(strategyComparison));

        assertTrue(output.contains("EV: -0.750R"));
        assertFalse(output.contains("--0.750R"));
    }
}
