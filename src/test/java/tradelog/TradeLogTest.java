package tradelog;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeLogTest {

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buffer));
        action.run();
        System.setOut(original);
        return buffer.toString();
    }

    @Test
    public void run_onStart_showsWelcomeMessage() {
        System.setIn(new ByteArrayInputStream("exit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("Welcome to TradeLog!"));
    }

    @Test
    public void run_listCommand_showsEmptyMessage() {
        System.setIn(new ByteArrayInputStream("list\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("No trades logged yet."));
    }

    @Test
    public void run_unknownCommand_showsError() {
        System.setIn(new ByteArrayInputStream("blah\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("Error:"));
    }

    @Test
    public void run_exitCommand_showsGoodbye() {
        System.setIn(new ByteArrayInputStream("exit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    public void run_addCommand_showsTradeAdded() {
        String addInput =
                "add t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/Breakout\nexit\n";
        System.setIn(new ByteArrayInputStream(addInput.getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("Trade successfully added."));
    }

    @Test
    public void run_compareCommand_showsStrategyComparison() {
        String compareInput = """
                add t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/BB
                compare
                exit
                """;
        System.setIn(new ByteArrayInputStream(compareInput.getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("Strategy Comparison:"));
        assertTrue(output.contains("Breakout:"));
    }

    @Test
    public void run_emptyCommand_showsError() {
        System.setIn(new ByteArrayInputStream("\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog("./data/test_trades.txt").run());
        assertTrue(output.contains("Error:"));
    }
}
