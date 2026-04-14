package tradelog;

import org.junit.jupiter.api.BeforeEach; // Added import
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tradelog.model.ModeManager; // Added import

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeLogTest {

    @TempDir
    Path tempDir;

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

    @BeforeEach
    public void setUp() {
        // Reset ModeManager to BACKTEST before each test for environmental consistency
        ModeManager.getInstance().setLive(false);
    }

    @Test
    public void run_onStart_showsWelcomeMessage() {
        System.setIn(new ByteArrayInputStream("testpassword\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Welcome to TradeLog!"));
    }

    @Test
    public void run_listCommand_showsEmptyMessage() {
        System.setIn(new ByteArrayInputStream("testpassword\nlist\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("No trades logged yet."));
    }

    @Test
    public void run_unknownCommand_showsError() {
        System.setIn(new ByteArrayInputStream("testpassword\nblah\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Error:"));
    }

    @Test
    public void run_exitCommand_showsGoodbye() {
        System.setIn(new ByteArrayInputStream("testpassword\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Goodbye!"));
    }

    @Test
    public void run_addCommand_showsTradeAdded() {
        String addInput = "testpassword\nadd t/AAPL d/2026-02-18 dir/long"
                + " e/180 x/190 s/170 strat/Breakout\nexit\n";
        System.setIn(new ByteArrayInputStream(addInput.getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Trade successfully added."));
    }

    @Test
    public void run_compareCommand_showsStrategyComparison() {
        String compareInput = "testpassword\n"
                + "add t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 strat/BB\n"
                + "compare\n"
                + "exit\n";
        System.setIn(new ByteArrayInputStream(compareInput.getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Strategy Comparison:"));
        assertTrue(output.contains("Breakout:"));
    }

    @Test
    public void run_emptyCommand_showsError() {
        System.setIn(new ByteArrayInputStream("testpassword\n\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Error:"));
    }

    // Added ModeManager Integration Assertions

    /**
     * Verifies that the system can transition to LIVE mode via the command loop
     * and displays the appropriate environment warning.
     */
    @Test
    public void run_setModeCommand_showsModeTransition() {
        // password -> mode switch -> exit
        String modeInput = "testpassword\nsetmode live\nexit\n";
        System.setIn(new ByteArrayInputStream(modeInput.getBytes()));
        
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());

        assertTrue(output.contains("Switched to LIVE mode"));
        assertTrue(output.contains("Daily Loss Limit"), "Should show LIVE mode warnings.");
    }

    /**
     * Verifies that trades are saved even if the user does not explicitly 
     * type 'exit' (end of input stream).
     */
    @Test
    public void run_endOfInputWithoutExit_savesTradesBeforeShutdown() throws IOException {
        String addInput = "testpassword\nadd t/AAPL d/2026-02-18 dir/long"
                + " e/180 x/190 s/170 strat/Breakout\n";
        System.setIn(new ByteArrayInputStream(addInput.getBytes()));

        captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
      
        String savedContent = Files.readString(tempDir.resolve("trades.txt"));
        assertTrue(savedContent.contains("AAPL"));
    }
    }
}
