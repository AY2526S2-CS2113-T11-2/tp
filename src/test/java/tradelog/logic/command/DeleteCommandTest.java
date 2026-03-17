package tradelog.logic.command;

import org.junit.jupiter.api.Test;
import tradelog.exception.TradeLogException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the validation logic inside the DeleteCommand constructor.
 * Ensures that invalid or missing trade indices correctly throw exceptions.
 */
public class DeleteCommandTest {

    /**
     * Tests if a valid trade index is accepted without throwing any exceptions.
     */
    @Test
    public void constructor_validInput_doesNotThrowException() {
        String validArgs = "2";
        assertDoesNotThrow(() -> new DeleteCommand(validArgs));
    }

    /**
     * Tests if an empty input correctly throws a TradeLogException.
     */
    @Test
    public void constructor_emptyInput_throwsTradeLogException() {
        String invalidArgs = "   ";

        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new DeleteCommand(invalidArgs));

        assertTrue(exception.getMessage().contains("Missing trade index"));
    }

    /**
     * Tests if a non-numeric input correctly throws a TradeLogException.
     */
    @Test
    public void constructor_nonNumericInput_throwsTradeLogException() {
        String invalidArgs = "abc";

        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new DeleteCommand(invalidArgs));

        assertTrue(exception.getMessage().contains("valid integer"));
    }

    /**
     * Tests if a negative trade index correctly throws a TradeLogException.
     */
    @Test
    public void constructor_negativeIndex_throwsTradeLogException() {
        String invalidArgs = "-1";

        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new DeleteCommand(invalidArgs));

        assertTrue(exception.getMessage().contains("positive integer"));
    }

    /**
     * Tests if zero as a trade index correctly throws a TradeLogException.
     */
    @Test
    public void constructor_zeroIndex_throwsTradeLogException() {
        String invalidArgs = "0";

        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new DeleteCommand(invalidArgs));

        assertTrue(exception.getMessage().contains("positive integer"));
    }
}
