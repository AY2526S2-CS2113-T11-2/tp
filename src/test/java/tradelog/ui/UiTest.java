package tradelog.ui;

import org.junit.jupiter.api.Test;
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
    public void showWelcome_containsWelcomeMessage() {
        Ui ui = new Ui();
        String output = captureOutput(ui::showWelcome);
        assertTrue(output.contains("Welcome to TradeLog!"));
    }

    @Test
    public void showError_containsErrorMessage() {
        Ui ui = new Ui();
        String output = captureOutput(() -> ui.showError("something went wrong"));
        assertTrue(output.contains("Error: something went wrong"));
    }
}
