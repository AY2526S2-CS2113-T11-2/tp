package tradelog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite for {@link ModeManager}.
 * Verifies singleton integrity and environment mode transition logic.
 */
public class ModeManagerTest {

    private ModeManager modeManager;

    @BeforeEach
    public void setUp() {
        modeManager = ModeManager.getInstance();
        // Reset to default BACKTEST state before each test to ensure isolation
        modeManager.setLive(false);
    }

    /**
     * Verifies the Singleton pattern.
     */
    @Test
    public void getInstance_multipleCalls_returnsSameInstance() {
        ModeManager anotherInstance = ModeManager.getInstance();
        assertSame(modeManager, anotherInstance, "ModeManager must be a singleton.");
    }

    /**
     * Verifies that the initial system state is BACKTEST.
     */
    @Test
    public void isLive_defaultState_returnsFalse() {
        assertFalse(modeManager.isLive(), "The default mode should be BACKTEST (false).");
        assertEquals(ModeManager.EnvironmentMode.BACKTEST, modeManager.getCurrentMode());
    }

    /**
     * Verifies switching modes using the EnvironmentMode enum.
     */
    @Test
    public void setMode_changeToLive_updatesCorrectly() {
        modeManager.setMode(ModeManager.EnvironmentMode.LIVE);
        assertTrue(modeManager.isLive());
        assertEquals(ModeManager.EnvironmentMode.LIVE, modeManager.getCurrentMode());

        modeManager.setMode(ModeManager.EnvironmentMode.BACKTEST);
        assertFalse(modeManager.isLive());
    }

    /**
     * Verifies that the setLive(boolean) helper method correctly updates state.
     */
    @Test
    public void setLive_booleanToggle_updatesEnumMode() {
        modeManager.setLive(true);
        assertTrue(modeManager.isLive());
        assertEquals(ModeManager.EnvironmentMode.LIVE, modeManager.getCurrentMode());

        modeManager.setLive(false);
        assertFalse(modeManager.isLive());
        assertEquals(ModeManager.EnvironmentMode.BACKTEST, modeManager.getCurrentMode());
    }

    /**
     * Verifies that the warning message contains critical keywords.
     * Note: Converted to upper case for case-insensitive matching to avoid trivial failures.
     */
    @Test
    public void getWarningMessage_containsRequiredConstraints() {
        String warning = modeManager.getWarningMessage();

        // Ensure the warning is not null or empty first
        assertTrue(warning != null && !warning.isEmpty(), "Warning message should not be empty.");

        // Case-insensitive check to ensure keywords are present
        String upperWarning = warning.toUpperCase();

        assertTrue(upperWarning.contains("LIVE"),
                "Warning should mention LIVE mode (actual: " + warning + ")");

        // If your implementation uses 'Loss' instead of 'Loss Limit', adjust accordingly
        assertTrue(upperWarning.contains("LOSS"),
                "Warning should mention Loss constraints.");

        // Check for historical data restrictions common in backtesting contexts
        assertTrue(upperWarning.contains("HISTORICAL") || upperWarning.contains("RESTRICT"),
                "Warning should mention data restrictions.");
    }
}
