package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListCommandTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        action.run();
        System.setOut(original);
        return buffer.toString();
    }

    @Test
    public void execute_emptyTradeList_showsEmptyMessage() {
        TradeList tradeList = new TradeList();
        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");
        ListCommand command = new ListCommand();
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));
        assertTrue(output.contains("No trades logged yet."));
    }

    @Test
    public void execute_nonEmptyTradeList_showsTrades() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-02-18",
                "Long", 180.0, 190.0, 170.0, "Win", "Breakout"));
        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");
        ListCommand command = new ListCommand();
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));
        assertTrue(output.contains("AAPL"));
        assertTrue(output.contains("1."));
    }

    @Test
    public void execute_multipleTradeList_showsAllTrades() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-02-18",
                "Long", 180.0, 190.0, 170.0, "Win", "Breakout"));
        tradeList.addTrade(new Trade("TSLA", "2026-02-17",
                "Short", 400.0, 380.0, 410.0, "Win", "Pullback"));
        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");
        ListCommand command = new ListCommand();
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));
        assertTrue(output.contains("1."));
        assertTrue(output.contains("2."));
        assertTrue(output.contains("AAPL"));
        assertTrue(output.contains("TSLA"));
    }

    @Test
    public void isExit_listCommand_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
