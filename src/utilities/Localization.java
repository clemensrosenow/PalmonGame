package utilities;

import java.text.MessageFormat;
import java.util.*;

/**
 * Manages localization of console messages based on user's preferred language.
 */
public class Localization {
    // Enumeration of supported languages
    enum Language {
        English("en"), Deutsch("de");

        final String countryCode;
        Language(String countryCode) {
            this.countryCode = countryCode;
        }
    }
    static Language userLanguage;
    static final String basePath = "resources/console_messages";
    static ResourceBundle bundle;

    /**
     * Configures the language based on user input.
     */
    public static void configureLanguage() {
        String prompt = "What's your preferred language?";
        userLanguage = Language.valueOf(UserInput.select(prompt, Language.values()));
        bundle = ResourceBundle.getBundle(basePath, new Locale(userLanguage.countryCode));
    }

    /**
     * Retrieves a localized message from the resource bundle.
     *
     * @param propertyKey the resource bundle key of the message
     * @param args        variable amount of values to be dynamically inserted into the message
     * @return the localized message
     */
    public static String getMessage(String propertyKey, Object... args) {
        String pattern = bundle.getString(propertyKey);
        return MessageFormat.format(pattern, args); //method automatically replaces placeholders like {0} with arguments
    }

    private Localization(){} // Private constructor to prevent instantiation
}
