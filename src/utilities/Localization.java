package utilities;

import java.text.MessageFormat;
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
    static final String basePath = "resources/console_messages";
    static ResourceBundle bundle;

    public static void configureLanguage() {
        Locale.setDefault(new Locale(defaultLanguage.countryCode));
        String prompt = "What's your preferred language?";
        userLanguage = Language.valueOf(UserInput.select(prompt, Language.values()));
        bundle = ResourceBundle.getBundle(basePath, new Locale(userLanguage.countryCode));
    }

    public static String getMessage(String propertyKey, Object... args) { //Variable amount of arguments for dynamic value insertion
        String pattern = bundle.getString(propertyKey);
        return MessageFormat.format(pattern, args); //automatically replaces placeholders (e.g. {0}) with arguments
    }

    private Localization(){}; // Private constructor to prevent instantiation
}
