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
    public void parseStrategy_unknownStrategy_throwsTradeLogException() {
        assertThrows(TradeLogException.class, () -> ParserUtil.parseStrategy("Custom Strategy"));
    }

    @Test
    public void canonicalizeStoredStrategy_unknownStrategy_returnsTrimmedInput() {
        assertEquals("Custom Strategy", ParserUtil.canonicalizeStoredStrategy("  Custom Strategy  "));
    }
}
