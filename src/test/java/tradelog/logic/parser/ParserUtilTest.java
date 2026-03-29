package tradelog.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ParserUtilTest {

    @Test
    public void parseStrategy_knownShortcut_returnsExpandedName() {
        assertEquals("Breakout", ParserUtil.parseStrategy("BB"));
        assertEquals("Major Trend Reversal", ParserUtil.parseStrategy("mtr"));
    }

    @Test
    public void parseStrategy_unknownShortcut_returnsTrimmedInput() {
        assertEquals("Custom Strategy", ParserUtil.parseStrategy("  Custom Strategy  "));
    }
}
