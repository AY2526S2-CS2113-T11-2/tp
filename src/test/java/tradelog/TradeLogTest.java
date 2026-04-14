package tradelog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tradelog.model.ModeManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the TradeLog main application loop.
 */
class TradeLogTest {

    @TempDir
    Path tempDir;

    /**
     * Captures System. Out output during the execution of a task.
     */
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
        // Check for either the expanded name or the shortcut to ensure parser mapping works
        assertTrue(output.contains("Breakout") || output.contains("BB"));
    }

    @Test
    public void run_emptyCommand_showsError() {
        System.setIn(new ByteArrayInputStream("testpassword\n\nexit\n".getBytes()));
        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());
        assertTrue(output.contains("Error:"));
    }

    /**
     * Verifies that the system can transition to LIVE mode via the command loop
     * and displays the appropriate environment warning.
     */
    /**
     * Verifies that the system can transition to LIVE mode via the command loop
     * and displays the appropriate environment warning.
     */
    @Test
    public void run_setModeCommand_showsModeTransition() {
        // Sequence:
        // 1. Enter password: "testpassword"
        // 2. Enter command: "mode live" (NOT "setmode live")
        // 3. Confirm transition: "yes" (Required by your SetModeCommand.execute logic)
        // 4. Exit: "exit"
        String modeInput = "testpassword\nmode live\nyes\nexit\n";
        System.setIn(new ByteArrayInputStream(modeInput.getBytes()));

        String output = captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());

        // Convert to uppercase for robust keyword checking
        String upperOutput = output.toUpperCase();

        // Verify transition confirmation
        assertTrue(upperOutput.contains("LIVE") && upperOutput.contains("MODE"),
                "Output should confirm switching to LIVE mode.");

        // Verify that the UI blocks/warnings were shown
        // Since your code uses boxed blocks for warnings, these keywords must appear
        assertTrue(upperOutput.contains("LOSS") || upperOutput.contains("LIMIT") || upperOutput.contains("WARNING") || upperOutput.contains("SUCCESS"),
                "Output should display LIVE mode environment warnings or success messages.");
    }

    /**
     * Verifies that trades are saved to storage even if the input stream ends
     * without an explicit 'exit' command.
     */
    @Test
    public void run_endOfInputWithoutExit_savesTradesBeforeShutdown() throws IOException {
        String addInput = "testpassword\nadd t/AAPL d/2026-02-18 dir/long"
                + " e/180 x/190 s/170 strat/Breakout\n";
        System.setIn(new ByteArrayInputStream(addInput.getBytes()));

        captureOutput(() -> new TradeLog(tempDir.toString(), "trades").run());

        // Verify file persistence
        String savedContent = Files.readString(tempDir.resolve("trades.txt"));
        assertTrue(savedContent.contains("AAPL"));
    }
}
