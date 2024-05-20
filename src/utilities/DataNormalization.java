package utilities;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Provides methods for normalizing data from files and user input.
 */
public class DataNormalization {
    /**
     * Capitalizes the first letter of a word.
     *
     * @param word the word to normalize
     * @return the word with the first letter capitalized
     */
    public static String word(String word) {
        if (word == null || word.isEmpty()) return word; // Handle null or empty strings
        return Character.toUpperCase(word.charAt(0)) + word.substring(1); // Capitalize the first letter and append the rest
    }

    /**
     * Normalizes a name by capitalizing each word and changing connection symbols.
     *
     * @see #word(String) for capitalization
     *
     * @param word        the name to normalize
     * @param splitSymbol the symbol to split words
     * @param joinSymbol  the symbol to join words
     * @return the normalized name
     */
    public static String name(String word, char splitSymbol, char joinSymbol) {
        return Arrays.stream(word.split(String.valueOf(splitSymbol)))
                .map(DataNormalization::word)
                .collect(Collectors.joining(String.valueOf(joinSymbol)));
    }

    /**
     * Parses a string value to an integer without error handling.
     *
     * @param value the string representation of the number
     * @return the parsed integer value
     */
    public static int number(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Parses a string percentage value to a float without error handling.
     *
     * @param value the string representation of the percentage
     * @return the parsed float value (between 0 and 1)
     */
    public static float percentage(String value) {
        return Float.parseFloat(value.replace("%", "")) / 100;
    }
}
