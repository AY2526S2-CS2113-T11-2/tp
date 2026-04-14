package tradelog.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach; // Added import
import org.junit.jupiter.api.Test;

import java.time.LocalDate; // Added import
import tradelog.exception.TradeLogException;
import tradelog.model.ModeManager; // Added import

public class ParserUtilTest {

    @BeforeEach
    public void setUp() {
        // Reset ModeManager to BACKTEST before each test for consistency
        ModeManager.getInstance().setLive(false);
    }

    @Test
    public void parseStrategy_knownShortcut_returnsExpandedName() {
        assertEquals("Breakout", ParserUtil.parseStrategy("BB"));
        assertEquals("Major Trend Reversal", ParserUtil.parseStrategy("mtr"));
    }

    @Test
    public void parseStrategy_knownFullNameCaseVariant_returnsCanonicalName() {
        assertEquals("Breakout", ParserUtil.parseStrategy("breakout"));
        assertEquals("Major Trend Reversal", ParserUtil.parseStrategy("  major   trend reversal "));
    }

    @Test
    public void parseStrategy_knownShortcutWithWhitespace_returnsExpandedName() {
        assertEquals("Breakout", ParserUtil.parseStrategy("  bb  "));
    }

    @Test
    public void parseStrategy_unknownStrategy_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> ParserUtil.parseStrategy("Custom Strategy"));
    }

    @Test
    public void parseStrategy_emptyStrategy_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> ParserUtil.parseStrategy(""));
        assertThrows(TradeLogException.class, () -> ParserUtil.parseStrategy("   "));
    }

    @Test
    public void canonicalizeStoredStrategy_unknownStrategy_returnsTrimmedInput() {
        assertEquals("Custom Strategy", ParserUtil.canonicalizeStoredStrategy("  Custom Strategy  "));
    }

    @Test
    public void canonicalizeStoredStrategy_knownValues_returnsCanonicalName() {
        assertEquals("Breakout", ParserUtil.canonicalizeStoredStrategy(" bb "));
        assertEquals("Major Trend Reversal",
                ParserUtil.canonicalizeStoredStrategy(" major   trend reversal "));
    }

    @Test
    public void parseTicker_validTicker_returnsUppercase() {
        assertEquals("AAPL", ParserUtil.parseTicker("aapl"));
        assertEquals("MSFT", ParserUtil.parseTicker("  msft  "));
    }

    @Test
    public void parseTicker_emptyTicker_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> ParserUtil.parseTicker(""));
        assertThrows(TradeLogException.class, () -> ParserUtil.parseTicker("   "));
    }

    @Test
    public void parsePrice_validPrice_returnsDouble() {
        assertEquals(150.5, ParserUtil.parsePrice("150.5", "Entry"));
    }

    @Test
    public void parsePrice_invalidPrice_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> ParserUtil.parsePrice("abc", "Entry"));
        assertThrows(TradeLogException.class, () -> ParserUtil.parsePrice("-10", "Entry"));
        assertThrows(TradeLogException.class, () -> ParserUtil.parsePrice("", "Entry"));
    }

    @Test
    public void parseDirection_validDirection_returnsFormatted() {
        assertEquals("Long", ParserUtil.parseDirection("long"));
        assertEquals("Short", ParserUtil.parseDirection("SHORT"));
    }

    @Test
    public void parseDirection_invalidDirection_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> ParserUtil.parseDirection("up"));
        assertThrows(TradeLogException.class, () -> ParserUtil.parseDirection(""));
    }

    // Added ModeManager Assertions

    /**
     * Verifies that dates other than today are rejected when the system is in LIVE mode.
     */
    @Test
    public void parseDate_liveModeHistoricalDate_throwsTradeLogException() {
        ModeManager.getInstance().setLive(true);
        String yesterday = LocalDate.now().minusDays(1).toString();

        assertThrows(TradeLogException.class, () -> ParserUtil.parseDate(yesterday));
    }

    /**
     * Verifies that today's date is accepted even when the system is in LIVE mode.
     */
    @Test
    public void parseDate_liveModeCurrentDate_success() throws TradeLogException {
        ModeManager.getInstance().setLive(true);
        String today = LocalDate.now().toString();

        assertEquals(today, ParserUtil.parseDate(today));
    }
}
