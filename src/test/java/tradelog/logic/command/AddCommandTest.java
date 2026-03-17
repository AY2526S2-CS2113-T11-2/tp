package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.exception.TradeLogException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the validation logic inside the AddCommand constructor.
 * Ensures that missing or blank prefixes correctly throw exceptions.
 */
public class AddCommandTest {

    /**
     * Tests if a perfectly formatted command string is accepted without throwing any exceptions.
     */
    @Test
    public void constructor_validInput_doesNotThrowException() {
        String validArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/Breakout";
        assertDoesNotThrow(() -> new AddCommand(validArgs));
    }

    /**
     * Tests if omitting a required prefix (e.g., missing d/) correctly throws a TradeLogException.
     */
    @Test
    public void constructor_missingPrefix_throwsTradeLogException() {
        // Missing the "d/" date prefix
        String invalidArgs = " t/AAPL dir/long e/180 x/190 s/170 o/win strat/Breakout";

        TradeLogException exception = assertThrows(TradeLogException.class, () -> {
            new AddCommand(invalidArgs);
        });

        assertTrue(exception.getMessage().contains("Missing required prefix: d/"));
    }

    /**
     * Tests if including a prefix but leaving its value blank throws a TradeLogException.
     */
    @Test
    public void constructor_blankPrefixValue_throwsTradeLogException() {
        // The "t/" prefix is present, but has no ticker symbol after it
        String invalidArgs = " t/ d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/Breakout";

        TradeLogException exception = assertThrows(TradeLogException.class, () -> {
            new AddCommand(invalidArgs);
        });

        assertTrue(exception.getMessage().contains("cannot be empty"));
    }
}
