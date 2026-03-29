package tradelog.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.model.Trade;
import tradelog.model.TradeList;
import tradelog.storage.Storage;
import tradelog.ui.Ui;

/**
 * Test suite for EditCommand validation and atomic updates.
 */
public class EditCommandTest {
    private TradeList tradeList;
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        // Fixed: Use 8 arguments for Trade constructor to avoid "Expected 8 found 6"
        // Order: Ticker, Date, Direction, Entry, Exit, Stop, Outcome, Strategy
        Trade initialTrade = new Trade("AAPL", "2023-10-10", "long",
                150.0, 160.0, 140.0, "Open", "Trend");
        tradeList.addTrade(initialTrade);

        ui = new Ui();
        storage = new Storage("test_edit_storage.txt");
    }

    @Test
    public void execute_validEdit_tradeUpdatedSuccessfully() throws TradeLogException {
        // User wants to update the exit price to 175.0 and the outcome to WIN
        EditCommand command = new EditCommand("1 x/175.0 o/WIN");

        command.execute(tradeList, ui, storage);

        Trade updatedTrade = tradeList.getTrade(0);

        // Verify that the specified fields were updated
        assertEquals(175.0, updatedTrade.getExitPrice(), "Exit price should be updated to 175.0");
        assertEquals("WIN", updatedTrade.getOutcome(), "Outcome should be updated to WIN");

        // Verify that the OTHER fields remained exactly the same
        assertEquals("AAPL", updatedTrade.getTicker(), "Ticker should remain unchanged");
        assertEquals("2023-10-10", updatedTrade.getDate(), "Date should remain unchanged");
        assertEquals(150.0, updatedTrade.getEntryPrice(), "Entry price should remain unchanged");
    }

    @Test
    public void execute_invalidDirectionString_throwsTradeLogException() {
        EditCommand command = new EditCommand("1 dir/invalid_direction");

        // Verify that the method throws TradeLogException and STOPS before calling any UI methods
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Data remains unchanged
        assertEquals("long", tradeList.getTrade(0).getDirection());
    }

    @Test
    public void execute_invalidLongRisk_throwsTradeLogException() {
        // Stop loss (160) above entry (150) for long is invalid
        EditCommand command = new EditCommand("1 s/160.0");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Stop loss price remains 140.0
        assertEquals(140.0, tradeList.getTrade(0).getStopLossPrice());
    }

    @Test
    public void execute_atomicUpdateFailure_tickerNotChanged() {
        // Attempting to change ticker to TSLA but failing at the stop loss validation step
        EditCommand command = new EditCommand("1 t/TSLA s/160.0");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Verify Atomicity: Ticker must still be AAPL
        assertEquals("AAPL", tradeList.getTrade(0).getTicker());
    }

    @Test
    public void execute_indexOutOfBounds_throwsTradeLogException() {
        EditCommand command = new EditCommand("10 t/MSFT");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
    }

    @Test
    public void execute_strategyShortcut_strategyExpandedSuccessfully() throws TradeLogException {
        EditCommand command = new EditCommand("1 strat/MTR");

        command.execute(tradeList, ui, storage);

        assertEquals("Major Trend Reversal", tradeList.getTrade(0).getStrategy());
    }
}
