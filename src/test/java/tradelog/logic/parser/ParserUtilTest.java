package tradelog.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;

public class ParserUtilTest {

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
}
