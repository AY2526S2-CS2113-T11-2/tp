package tradelog.logic.parser;

import java.util.HashMap;

// A utility class whose sole job is to scan a string like add t/AAPL d/2026-02-18 and
// split it into a map of prefixes and their values.
public class ArgumentTokeniser {
    /**
     * Scans the user input and extracts the values associated with each specified prefix.
     * @param userInput The raw string of arguments (e.g., "t/AAPL d/2026-03-17 dir/long")
     * @param prefixes An array of prefixes to look for (e.g., {"t/", "d/", "dir/"})
     * @return A HashMap where the key is the prefix, and the value is the extracted string.
     */
    public static HashMap<String, String> tokenise(String userInput,String[] prefixes) {
        HashMap<String, String> argumentMap = new HashMap<>();

        // Add a leading space to make it easier to find the first prefix
        String paddedInput = " " + userInput;

        for (String prefix : prefixes) {
            String prefixWithSpace = " " + prefix;
            int startIndex = paddedInput.indexOf(prefixWithSpace);

            // If the prefix exists in the string
            if (startIndex != -1) {
                // Move the start index to the end of the prefix (where the actual value begins)
                startIndex += prefixWithSpace.length();
                // By default, assume this argument goes all the way to the end of the string
                int endIndex = paddedInput.length();
                // Check if another prefix interrupts this one
                for (String otherPrefix : prefixes) {
                    if (otherPrefix.equals(prefix)) {
                        continue;
                    }

                    int otherIndex = paddedInput.indexOf(" " + otherPrefix);
                    // If another prefix is found AFTER our current prefix, cut the string there
                    if (otherIndex > startIndex && otherIndex < endIndex) {
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
