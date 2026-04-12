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
    // Constants to satisfy IDE static analysis and ensure consistent testing
    private static final String INIT_TICKER = "AAPL";
    private static final String INIT_DATE = "2023-10-10";
    private static final String INIT_DIR = "long";
    private static final double INIT_ENTRY = 150.0;
    private static final double INIT_EXIT = 160.0;
    private static final double INIT_STOP = 140.0;
    private static final String INIT_OUTCOME = "Open";
    private static final String INIT_STRAT = "Trend";

    private TradeList tradeList;
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tradeList = new TradeList();
        Trade initialTrade = new Trade(INIT_TICKER, INIT_DATE, INIT_DIR,
                INIT_ENTRY, INIT_EXIT, INIT_STOP, INIT_OUTCOME, INIT_STRAT);
        tradeList.addTrade(initialTrade);

        ui = new Ui();
        storage = new Storage("test_edit_storage.txt");
    }

    /**
     * Performs a deep state comparison to verify Atomicity.
     * Arguments are wrapped to comply with 120-character line length limit.
     */
    private void assertTradeUnchanged(int index, String ticker, String date, String dir,
                                      double entry, double exit, double stop,
                                      String outcome, String strat) {
        Trade current = tradeList.getTrade(index);
        assertEquals(ticker, current.getTicker(), "Atomicity Failure: Ticker modified");
        assertEquals(date, current.getDate(), "Atomicity Failure: Date modified");
        assertEquals(dir.toLowerCase(), current.getDirection().toLowerCase(), "Atomicity Failure: Direction modified");
        assertEquals(entry, current.getEntryPrice(), "Atomicity Failure: Entry price modified");
        assertEquals(exit, current.getExitPrice(), "Atomicity Failure: Exit price modified");
        assertEquals(stop, current.getStopLossPrice(), "Atomicity Failure: Stop loss modified");
        assertEquals(outcome, current.getOutcome(), "Atomicity Failure: Outcome modified");
        assertEquals(strat, current.getStrategy(), "Atomicity Failure: Strategy modified");
    }

    @Test
    public void execute_validEdit_tradeUpdatedSuccessfully() throws TradeLogException {
        EditCommand command = new EditCommand("1 x/175.0 o/WIN");
        command.execute(tradeList, ui, storage);

        Trade updatedTrade = tradeList.getTrade(0);
        assertEquals(175.0, updatedTrade.getExitPrice());
        assertEquals("WIN", updatedTrade.getOutcome());
    }

    @Test
    public void execute_editSecondTrade_success() throws TradeLogException {
        String newTicker = "MSFT";
        Trade secondTrade = new Trade("TSLA", "2024-01-01", "short", 250.0, 230.0, 260.0, "WIN", "Swing");
        tradeList.addTrade(secondTrade);

        EditCommand command = new EditCommand("2 t/" + newTicker);
        command.execute(tradeList, ui, storage);

        // Line wrapped to satisfy Checkstyle 120-char limit
        assertTradeUnchanged(0, INIT_TICKER, INIT_DATE, INIT_DIR, INIT_ENTRY,
                INIT_EXIT, INIT_STOP, INIT_OUTCOME, INIT_STRAT);

        assertTradeUnchanged(1, newTicker, "2024-01-01", "short", 250.0,
                230.0, 260.0, "WIN", "Swing");
    }

    @Test
    public void execute_invalidDirectionString_throwsTradeLogException() {
        EditCommand command = new EditCommand("1 dir/invalid_direction");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
        assertEquals(INIT_DIR.toLowerCase(), tradeList.getTrade(0).getDirection().toLowerCase());
    }

    @Test
    public void execute_invalidLongRisk_throwsTradeLogException() {
        EditCommand command = new EditCommand("1 s/160.0");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
        assertEquals(INIT_STOP, tradeList.getTrade(0).getStopLossPrice());
    }

    @Test
    public void execute_atomicUpdateFailure_tickerNotChanged() {
        EditCommand command = new EditCommand("1 t/TSLA s/160.0");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Line wrapped to satisfy Checkstyle 120-char limit
        assertTradeUnchanged(0, INIT_TICKER, INIT_DATE, INIT_DIR, INIT_ENTRY,
                INIT_EXIT, INIT_STOP, INIT_OUTCOME, INIT_STRAT);
    }

    @Test
    public void execute_complexInvalidEdit_fullStateMaintained() {
        EditCommand command = new EditCommand("1 t/MSFT d/2025-01-01 e/not_a_number");

        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));

        // Line wrapped to satisfy Checkstyle 120-char limit
        assertTradeUnchanged(0, INIT_TICKER, INIT_DATE, INIT_DIR, INIT_ENTRY,
                INIT_EXIT, INIT_STOP, INIT_OUTCOME, INIT_STRAT);
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

    @Test
    public void execute_invalidStrategy_throwsTradeLogException() {
        EditCommand command = new EditCommand("1 strat/INVALID");
        assertThrows(TradeLogException.class, () -> command.execute(tradeList, ui, storage));
        assertEquals(INIT_STRAT, tradeList.getTrade(0).getStrategy());
    }
}
