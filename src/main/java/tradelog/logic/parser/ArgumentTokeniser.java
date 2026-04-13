package tradelog.logic.parser;

import java.util.HashMap;

import tradelog.exception.TradeLogException;

/**
 * Utility class for tokenising user input strings into mapped arguments based on prefixes.
 */
public class ArgumentTokeniser {

    /**
     * Scans the user input and extracts the values associated with each specified prefix.
     *
     * @param userInput The raw string of arguments (e.g., "t/AAPL d/2026-03-17 dir/long").
     * @param prefixes  An array of prefixes to look for (e.g., {"t/", "d/", "dir/"}).
     * @return A HashMap where each key is a prefix and the value is the extracted string.
     * @throws TradeLogException If a prefix appears more than once.
     */
    public static HashMap<String, String> tokenise(String userInput, String[] prefixes)
            throws TradeLogException {
        checkForUnknownPrefixes(userInput, prefixes);
        HashMap<String, String> argumentMap = new HashMap<>();
        String paddedInput = " " + userInput.trim().replaceAll("\\s+", " ");
        for (String prefix : prefixes) {
            String prefixWithSpace = " " + prefix;
            int startIndex = paddedInput.indexOf(prefixWithSpace);
            if (startIndex != -1) {
                int duplicateIndex = paddedInput.indexOf(prefixWithSpace, startIndex + 1);
                if (duplicateIndex != -1) {
                    throw new TradeLogException("Duplicate prefix detected");
                }
                startIndex += prefixWithSpace.length();
                int endIndex = paddedInput.length();
                for (String otherPrefix : prefixes) {
                    if (otherPrefix.equals(prefix)) {
                        continue;
                    }
                    int otherIndex = paddedInput.indexOf(" " + otherPrefix, startIndex);
                    if (otherIndex != -1 && otherIndex < endIndex) {
                        endIndex = otherIndex;
                    }
                }
                argumentMap.put(prefix, paddedInput.substring(startIndex, endIndex).trim());
            }
        }
        return argumentMap;
    }

    /**
     * Checks the user input for any prefixes that are not in the accepted list.
     * A prefix is detected as any token containing "/" (e.g., "o/", "foo/").
     *
     * @param userInput The raw string of arguments.
     * @param acceptedPrefixes The array of accepted prefixes.
     * @throws TradeLogException If an unrecognised prefix is found.
     */
    public static void checkForUnknownPrefixes(String userInput, String[] acceptedPrefixes)
            throws TradeLogException {
        String[] tokens = userInput.trim().split("\\s+");
        for (String token : tokens) {
            int slashIndex = token.indexOf('/');
            if (slashIndex <= 0) {
                continue;
            }
            String prefix = token.substring(0, slashIndex + 1);
            boolean isKnown = false;
            for (String acceptedPrefix : acceptedPrefixes) {
                if (acceptedPrefix.equals(prefix)) {
                    isKnown = true;
                    break;
                }
            }
            if (!isKnown) {
                throw new TradeLogException("Unrecognised prefix: " + prefix
                        + " is not a valid tag.");
            }
        }
    }
}
