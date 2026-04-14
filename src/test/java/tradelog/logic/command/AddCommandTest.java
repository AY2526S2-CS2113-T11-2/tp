package tradelog.logic.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.model.ModeManager; // Added import
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

import java.time.LocalDate; // Added import for today's date validation

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the validation logic inside the AddCommand constructor.
 * Ensures that missing or blank prefixes correctly throw exceptions.
 */
public class AddCommandTest {

    /** The list of trades used as the testing environment. */
    private TradeList tradeList;

    /** A dummy UI instance to satisfy command dependencies without crashing. */
    private Ui dummyUi;

    /** A dummy storage instance to satisfy command dependencies. */
    private Storage dummyStorage;

    /**
     * Initializes a fresh environment before each test.
     * Sets up an empty trade list and dummy objects to pass the execute method's assertions.
     */
    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        dummyUi = new Ui();
        dummyStorage = new Storage("dummy_add_storage.txt");

        // Reset ModeManager to BACKTEST before each test for consistency
        ModeManager.getInstance().setLive(false);
    }

    /**
     * Tests the execution of a valid AddCommand.
     * Verifies that the trade is successfully added to the TradeList and the fields match.
     *
     * @throws TradeLogException If the command fails to parse valid input.
     */
    @Test
    public void execute_validCommand_tradeAddedSuccessfully() throws TradeLogException {
        String validArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 strat/Breakout";
        AddCommand command = new AddCommand(validArgs);

        command.execute(tradeList, dummyUi, dummyStorage);

        assertEquals(1, tradeList.size(), "TradeList should have 1 trade after execution.");

        Trade addedTrade = tradeList.getTrade(0);
        assertEquals("AAPL", addedTrade.getTicker(), "Ticker should match the input.");
        assertEquals("Long", addedTrade.getDirection(), "Direction should match the input.");
        assertEquals(180.0, addedTrade.getEntryPrice(), "Entry price should match the input.");
        assertEquals("Breakout", addedTrade.getStrategy(), "Strategy shortcut should expand.");
    }

    @Test
    public void execute_strategyShortcut_tradeStoresExpandedStrategy() throws TradeLogException {
        String validArgs =
                " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 strat/BB";
        AddCommand command = new AddCommand(validArgs);

        command.execute(tradeList, dummyUi, dummyStorage);

        Trade addedTrade = tradeList.getTrade(0);
        assertEquals("Breakout", addedTrade.getStrategy(),
                "Shortcut should be stored as full strategy name.");
    }

    @Test
    public void execute_knownStrategyCaseVariant_tradeStoresCanonicalStrategy() throws TradeLogException {
        String validArgs =
                " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 strat/breakout";
        AddCommand command = new AddCommand(validArgs);

        command.execute(tradeList, dummyUi, dummyStorage);

        Trade addedTrade = tradeList.getTrade(0);
        assertEquals("Breakout", addedTrade.getStrategy(),
                "Known strategy names should be stored in canonical form.");
    }

    @Test
    public void constructor_invalidStrategy_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 strat/INVALID";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Invalid strategy"));
    }

    /**
     * Tests if a perfectly formatted command string is accepted without throwing any exceptions.
     */
    @Test
    public void constructor_validInput_doesNotThrowException() {
        String validArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 strat/Breakout";
        assertDoesNotThrow(() -> new AddCommand(validArgs));
    }

    /**
     * Tests if omitting a required prefix (e.g., missing d/) correctly throws a TradeLogException.
     */
    @Test
    public void constructor_missingPrefix_throwsTradeLogException() {
        String invalidArgs = " t/AAPL dir/long e/180 x/190 s/170 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Missing required prefix: d/"));
    }

    /**
     * Tests if including a prefix but leaving its value blank throws a TradeLogException.
     */
    @Test
    public void constructor_blankPrefixValue_throwsTradeLogException() {
        String invalidArgs = " t/ d/2026-02-18 dir/long e/180 x/190 s/170 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    /**
     * Tests if entry price equal to stop loss price throws a TradeLogException.
     */
    @Test
    public void constructor_entryEqualsStopLoss_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/180 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Entry price and stop loss price"));
    }

    /**
     * Tests if an invalid direction throws a TradeLogException.
     */
    @Test
    public void constructor_invalidDirection_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/up e/180 x/190 s/170 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Direction must be exactly"));
    }

    /**
     * Tests if a non-numeric price throws a TradeLogException.
     */
    @Test
    public void constructor_invalidPrice_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/abc x/190 s/170 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("valid number"));
    }

    /**
     * Tests if a long trade with stop loss above entry price throws a TradeLogException.
     */
    @Test
    public void constructor_longTradeStopLossAboveEntry_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/long e/180 x/190 s/190 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Long position"));
    }

    /**
     * Tests if a short trade with stop loss below entry price throws a TradeLogException.
     */
    @Test
    public void constructor_shortTradeStopLossBelowEntry_throwsTradeLogException() {
        String invalidArgs = " t/AAPL d/2026-02-18 dir/short e/180 x/170 s/170 strat/Breakout";
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(invalidArgs));
        assertTrue(exception.getMessage().contains("Short position"));
    }

    // Added ModeManager Assertions

    /**
     * Tests that adding a past trade in LIVE mode throws a TradeLogException.
     * Note: The validation happens in the constructor via ParserUtil.
     */
    @Test
    public void execute_liveModePastDate_throwsTradeLogException() {
        // Set to LIVE mode BEFORE creating the command
        ModeManager.getInstance().setLive(true);

        String pastDateArgs = " t/TSLA d/2020-01-01 dir/long e/100 x/110 s/90 strat/BB";

        // The constructor itself will throw the exception due to ParserUtil validation
        TradeLogException exception = assertThrows(TradeLogException.class,
                () -> new AddCommand(pastDateArgs));

        // Match the actual message returned by your ParserUtil
        assertTrue(exception.getMessage().contains("Live mode only allows trades for today"),
                "Exception message should explain Live mode date restrictions.");
    }

    /**
     * Tests that adding today's trade in LIVE mode is allowed.
     */
    @Test
    public void execute_liveModeTodayDate_addsSuccessfully() throws TradeLogException {
        ModeManager.getInstance().setLive(true);

        // Use the current date to satisfy Live mode requirements
        String today = LocalDate.now().toString();
        String todayArgs = " t/NVDA d/" + today + " dir/short e/500 x/480 s/510 strat/Breakout";

        // Ensure constructor succeeds
        AddCommand command = new AddCommand(todayArgs);

        // Ensure execution succeeds
        assertDoesNotThrow(() -> command.execute(tradeList, dummyUi, dummyStorage));
        assertEquals(1, tradeList.size(), "Trade should be added successfully for today's date in Live mode.");
    }
}
