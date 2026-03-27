package tradelog.ui;

import org.junit.jupiter.api.Test;
import tradelog.model.Trade;
import tradelog.model.TradeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UiTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        action.run();
        System.setOut(original);
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
                "Long", 180.0, 190.0, 170.0, "Win", "Breakout"));
        String output = captureOutput(() -> ui.printTradeList(tradeList));
        assertTrue(output.contains("AAPL"));
        assertTrue(output.contains("1."));
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
        assertTrue(output.contains("Commands: add, list, edit, delete, summary, exit"));
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
                "Long", 180.0, 190.0, 170.0, "Win", "Breakout");
        String output = captureOutput(() -> ui.printTrade(trade));
        assertTrue(output.contains("Trade Summary:"));
        assertTrue(output.contains("AAPL"));
    }
}
