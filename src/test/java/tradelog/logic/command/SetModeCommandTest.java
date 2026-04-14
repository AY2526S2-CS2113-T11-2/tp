package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.model.ModeManager;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Test class for SetModeCommand.
 * Validates mode transitions and ensures the constructor handles invalid inputs gracefully
 * according to the implementation in SetModeCommand.
 */
public class SetModeCommandTest {

    private TradeList tradeList;
    private Ui ui;
    private Storage storage;
    private final InputStream systemIn = System.in;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        ui = new Ui();
        storage = new Storage("dummy_set_mode_storage.txt");
        // Reset ModeManager to a known state before each test
        ModeManager.getInstance().setMode(ModeManager.EnvironmentMode.BACKTEST);
    }

    /**
     * Helper method to simulate user input in the console.
     */
    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    public void execute_validLiveMode_updatesToLive() {
        provideInput("yes\n"); // Simulate user confirmation
        SetModeCommand command = new SetModeCommand("live");
        command.execute(tradeList, ui, storage);

        assertTrue(ModeManager.getInstance().isLive(), "Mode should be LIVE after successful transition");
        assertEquals(ModeManager.EnvironmentMode.LIVE, ModeManager.getInstance().getCurrentMode());
        System.setIn(systemIn); // Reset System.in
    }

    @Test
    public void execute_validBacktestMode_updatesToBacktest() {
        // Prepare: Start in LIVE mode
        ModeManager.getInstance().setMode(ModeManager.EnvironmentMode.LIVE);

        provideInput("yes\n");
        SetModeCommand command = new SetModeCommand("backtest");
        command.execute(tradeList, ui, storage);

        assertFalse(ModeManager.getInstance().isLive(), "Mode should be BACKTEST (not live) after transition");
        assertEquals(ModeManager.EnvironmentMode.BACKTEST, ModeManager.getInstance().getCurrentMode());
        System.setIn(systemIn);
    }

    /**
     * Verifies that an invalid mode string does not crash the constructor.
     * The source code catches IllegalArgumentException and sets targetMode to null.
     */
    @Test
    public void constructor_invalidMode_doesNotThrowException() {
        assertDoesNotThrow(() -> new SetModeCommand("invalid_mode"),
                "Constructor should handle invalid strings internally without throwing exceptions.");
    }

    /**
     * Verifies that empty or whitespace strings are handled gracefully by the constructor.
     */
    @Test
    public void constructor_emptyArgs_doesNotThrowException() {
        assertDoesNotThrow(() -> new SetModeCommand(""), "Should handle empty string");
        assertDoesNotThrow(() -> new SetModeCommand("   "), "Should handle whitespace string");
    }

    @Test
    public void isExit_returnsFalse() {
        SetModeCommand command = new SetModeCommand("live");
        assertFalse(command.isExit(), "SetModeCommand should not trigger application exit");
    }
}
