package tradelog.logic.parser;

import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the functionality of the ArgumentTokeniser class.
 * Ensures that user input strings are correctly split into mapped arguments.
 */
public class ArgumentTokeniserTest {

    /**
     * Tests if the tokeniser correctly extracts values when multiple valid prefixes are present.
     */
    @Test
    public void tokenize_validInput_returnsCorrectMap() {
        String[] prefixes = {"t/", "e/", "x/"};
        String input = "add t/AAPL e/150.5 x/160.0";

        HashMap<String, String> result = ArgumentTokeniser.tokenise(input, prefixes);

        assertTrue(result.containsKey("t/"));
        assertEquals("AAPL", result.get("t/"));
        assertEquals("150.5", result.get("e/"));
        assertEquals("160.0", result.get("x/"));
    }

    /**
     * Tests if the tokeniser correctly handles arguments placed in a scrambled, non-standard order.
     */
    @Test
    public void tokenize_scrambledOrder_returnsCorrectMap() {
        String[] prefixes = {"t/", "dir/", "strat/"};
        String input = "add strat/Breakout dir/long t/EURUSD";

        HashMap<String, String> result = ArgumentTokeniser.tokenise(input, prefixes);

        assertEquals("EURUSD", result.get("t/"));
        assertEquals("long", result.get("dir/"));
        assertEquals("Breakout", result.get("strat/"));
    }

    /**
     * Tests if the tokeniser correctly ignores prefixes that were not provided in the input string.
     */
    @Test
    public void tokenize_missingPrefix_prefixNotInMap() {
        String[] prefixes = {"t/", "d/", "e/"};
        String input = "add t/TSLA d/2026-02-18";

        HashMap<String, String> result = ArgumentTokeniser.tokenise(input, prefixes);

        assertEquals("TSLA", result.get("t/"));
        assertEquals("2026-02-18", result.get("d/"));
        assertFalse(result.containsKey("e/"));
    }
}