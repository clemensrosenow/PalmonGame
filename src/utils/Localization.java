package utils;

import java.util.*;

public class Localization {
    enum Language {
        English("en"), Deutsch("de");

        final String countryCode;
        Language(String countryCode) {
            this.countryCode = countryCode;
        }
    }
    static final Language defaultLanguage = Language.English;
    static Language userLanguage;
    static final String baseName = "resources/console_messages";
    static ResourceBundle consoleText;

    public static void configureLanguage() {

        Locale.setDefault(new Locale(defaultLanguage.countryCode));
        String prompt = "What's your preferred language?";
        userLanguage = Language.valueOf(UserInput.select(prompt, Language.values(), defaultLanguage.name()));
        consoleText = ResourceBundle.getBundle(baseName, new Locale(userLanguage.countryCode));
    }

    public static String getMessage(String propertyKey) {
        return consoleText.getString(propertyKey);
    }

    private Localization(){}; // Private constructor to prevent instantiation
}
