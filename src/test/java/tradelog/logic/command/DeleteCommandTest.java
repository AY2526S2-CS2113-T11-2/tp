package tradelog.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the validation logic inside the DeleteCommand constructor.
 * Ensures that invalid or missing trade indices correctly throw exceptions.
 */
public class DeleteCommandTest {
    /** The list of trades used as the testing environment. */
    private TradeList tradeList;

    /** A dummy storage instance to satisfy command dependencies. */
    private Storage dummyStorage;

    /** A mocked UI instance used to capture and verify error messages. */
    private MockUi mockUi;

    /**
     * A specialized Mock UI class that intercepts the error display call
     * to verify that out-of-bounds deletions are handled gracefully.
     */
    private class MockUi extends Ui {
        boolean isShowErrorCalled = false;
        String capturedErrorMessage = "";

        @Override
        public void showError(String message) {
            isShowErrorCalled = true;
            capturedErrorMessage = message;
        }

        // Overriding these simply to prevent console spam during testing
        @Override
        public void printTrade(Trade trade) {}
        @Override
        public void showTradeDeleted() {}
    }

    /**
     * Initializes a fresh environment before each test.
     * Sets up a mocked UI, dummy storage, and a populated TradeList.
     */
    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        mockUi = new MockUi();
        dummyStorage = new Storage("dummy_delete_storage.txt");

        // Add two dummy trades so we have data to delete
        tradeList.addTrade(new Trade("AAPL", "2023-10-10", "long", 150.0, 160.0, 140.0, "Trend"));
        tradeList.addTrade(new Trade("TSLA", "2023-10-11", "short", 200.0, 180.0, 210.0, "Breakout"));
    }

    /**
     * Tests the execution of a valid DeleteCommand.
     * Verifies that the correct trade is removed and the list shifts appropriately.
     *
     * @throws TradeLogException If the constructor fails (Should not happen).
     */
    @Test
    public void execute_validIndex_tradeDeletedSuccessfully() throws TradeLogException {
        // User wants to delete the FIRST trade (index 1 in UI, index 0 in internal logic)
        DeleteCommand command = new DeleteCommand("1");

        command.execute(tradeList, mockUi, dummyStorage);

        // Verify the size decreased
        assertEquals(1, tradeList.size(), "TradeList size should be 1 after deleting one of the two trades.");

        // Verify that TSLA (the second trade) is now the first and only trade in the list
        Trade remainingTrade = tradeList.getTrade(0);
        assertEquals("TSLA", remainingTrade.getTicker(), "The remaining trade should be TSLA.");
    }

    /**
     * Tests execution when the user provides a number that is higher than the list size.
     * Verifies that the internal catch block triggers and sends an error to the UI.
     *
     * @throws TradeLogException If the constructor fails (Should not happen).
     */
    @Test
    public void execute_outOfBoundsIndex_showsErrorMessage() throws TradeLogException {
        // The list only has 2 items. User attempts to delete item #5.
        DeleteCommand command = new DeleteCommand("5");

        command.execute(tradeList, mockUi, dummyStorage);

        // Verify the list size was NOT changed
        assertEquals(2, tradeList.size(), "TradeList size should remain 2 after a failed deletion.");

        // Verify the catch block successfully called the UI error method
        assertTrue(mockUi.isShowErrorCalled, "The UI showError method should have been called.");
        assertEquals("Trade index does not exist!", mockUi.capturedErrorMessage,
                "The error message should match the catch block's specific output.");
    }

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
