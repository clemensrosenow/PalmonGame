package utils;

import utils.Input;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Localization {
    static final Map<String, String> languageOptions = Map.of("en", "English (default)", "de", "Deutsch");
    static final String defaultLanguage = "en";
    static final String baseName = "resources/console_messages";
    static ResourceBundle consoleText;

    public static void configureLanguage() {
        Locale.setDefault(new Locale(defaultLanguage));
        String userLanguage = Input.select("What's your preferred language?", languageOptions);
        consoleText = ResourceBundle.getBundle(baseName, new Locale(userLanguage));
    }

    public static String getMessage(String propertyKey) {
        return consoleText.getString(propertyKey);
    }

    private Localization(){}; // Private constructor to prevent instantiation
}
