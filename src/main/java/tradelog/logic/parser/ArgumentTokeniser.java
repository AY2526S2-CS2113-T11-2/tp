package tradelog.logic.parser;

import java.util.HashMap;

/**
 * Utility class for tokenising user input strings into mapped arguments based on specific prefixes.
 */
public class ArgumentTokeniser {
    /**
     * Scans the user input and extracts the values associated with each specified prefix.
     * @param userInput The raw string of arguments (e.g., "t/AAPL d/2026-03-17 dir/long")
     * @param prefixes An array of prefixes to look for (e.g., {"t/", "d/", "dir/"})
     * @return A HashMap where the key is the prefix, and the value is the extracted string.
     */
    public static HashMap<String, String> tokenise(String userInput, String[] prefixes) {
        HashMap<String, String> argumentMap = new HashMap<>();

        // Add a leading space to make it easier to find the first prefix
        String paddedInput = " " + userInput;

        for (String prefix : prefixes) {
            String prefixWithSpace = " " + prefix;
            int startIndex = paddedInput.indexOf(prefixWithSpace);

            if (startIndex != -1) {
                startIndex += prefixWithSpace.length();
                int endIndex = paddedInput.length();

                for (String otherPrefix : prefixes) {
                    if (otherPrefix.equals(prefix)) {
                        continue;
                    }

                    int otherIndex = paddedInput.indexOf(" " + otherPrefix, startIndex);
                    // If another prefix is found AFTER our current prefix, cut the string there
                    if (otherIndex != -1 && otherIndex < endIndex) {
                        endIndex = otherIndex;
                    }
                }
                // Extract the value, trim the whitespace, and save it to the map
                String value = paddedInput.substring(startIndex, endIndex).trim();
                argumentMap.put(prefix, value);
            }
        }
        return argumentMap;
    }
}
