package utilities;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DataNormalization {
    public static String word(String word) {
        if (word == null || word.isEmpty()) return word;
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    public static String name(String word, char splitSymbol, char joinSymbol) {
        return Arrays.stream(word.split(String.valueOf(splitSymbol)))
                .map(DataNormalization::word)
                .collect(Collectors.joining(String.valueOf(joinSymbol)));
    }

    public static int number(String value) {
        return Integer.parseInt(value);
    }

    public static float percentage(String value) {
        return Float.parseFloat(value.replace("%", "")) / 100;
    }
}
