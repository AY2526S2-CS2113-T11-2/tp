package tradelog.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import tradelog.exception.TradeLogException;
import tradelog.logic.command.CompareCommand;

public class ParserTest {

    @Test
    public void parseCommand_compare_returnsCompareCommand() throws TradeLogException {
        assertInstanceOf(CompareCommand.class, Parser.parseCommand("compare"));
    }
}
