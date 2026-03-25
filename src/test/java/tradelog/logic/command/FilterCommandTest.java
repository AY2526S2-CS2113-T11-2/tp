package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterCommandTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        action.run();
        System.setOut(original);
        return buffer.toString();
    }

    @Test
    void constructor_noCriteria_throwsTradeLogException() {
        String args = "";
        TradeLogException ex = assertThrows(TradeLogException.class, () -> new FilterCommand(args));
        assertTrue(ex.getMessage().contains("Use at least one filter"));
    }

    @Test
    void constructor_withValidCriteria_doesNotThrow() {
        String args = "t/AAPL";
        assertDoesNotThrow(() -> new FilterCommand(args));
    }

    @Test
    void execute_filterByTicker_printsExpectedTrade() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long", 100, 110, 95, "Win", "Breakout"));
        tradeList.addTrade(new Trade("MSFT", "2026-03-02", "Short", 200, 190, 210, "Win", "Momentum"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("t/AAPL");

        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("AAPL | 2026-03-01 | Long"));
        assertFalse(output.contains("MSFT | 2026-03-02 | Short"));
    }

    @Test
    void execute_filterNoMatch_showsNoMatchMessage() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long", 100, 110, 95, "Win", "Breakout"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("t/GOOG");
        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("No trades match the filter criteria."));
    }

    @Test
    void execute_filterByPartialTicker_printsExpectedTrade() {
        TradeList tradeList = new TradeList();
        tradeList.addTrade(new Trade("AAPL", "2026-03-01", "Long", 100, 110, 95, "Win", "Breakout"));
        tradeList.addTrade(new Trade("MSFT", "2026-03-02", "Short", 200, 190, 210, "Win", "Momentum"));

        Ui ui = new Ui();
        Storage storage = new Storage("./data/trades.txt");

        FilterCommand command = new FilterCommand("-p t/AP");

        String output = captureOutput(() -> command.execute(tradeList, ui, storage));

        assertTrue(output.contains("AAPL | 2026-03-01 | Long"));
        assertFalse(output.contains("MSFT | 2026-03-02 | Short"));
    }
}
