package utilities;

import java.util.*;
import java.util.stream.Collectors;

// Mini-Library for repeated user console input
public class UserInput {
    static Scanner scanner = new Scanner(System.in);

    private UserInput() { // Private constructor to prevent instantiation
    }

    public static String text(String prompt) {
        System.out.print("\n" + prompt + " ");
        String input = scanner.nextLine();
        while (input.isEmpty()) {
            System.out.print(Localization.getMessage("userinput.prompt.text.empty"));
            input = DataNormalization.name(scanner.nextLine(), ' ', ' ');
        }
        return input;
    }

    public static boolean confirm(String prompt) {
        enum Options {
            yes, no;

            public String getLocalized() {
                return Localization.getMessage("enum.confirmation." + this.name());
            }
        }
        // Convert enum values to localized strings for selection
        ArrayList<String> options = Arrays.stream(Options.values()).map(Options::getLocalized).collect(Collectors.toCollection(ArrayList::new));
        return select(prompt, options).equals(Options.yes.getLocalized());
    }

    public static int number(String prompt) {
        System.out.print("\n" + prompt + " ");
        try {
            int input = scanner.nextInt();
            scanner.nextLine(); // fully consume input
            return input;
        } catch (InputMismatchException exception) {
            scanner.nextLine(); // discard invalid token
            return number(Localization.getMessage("userinput.prompt.number"));
        }
    }

    public static int number(String prompt, int lowerBound, int upperBound) {
        int input = number(prompt);

        // Recursive error handling to avoid complex looping
        if (input < lowerBound) {
            return number(Localization.getMessage("userinput.prompt.number.range.lower", lowerBound), lowerBound, upperBound);
        }
        if (input > upperBound) {
            return number(Localization.getMessage("userinput.prompt.number.range.upper", upperBound), lowerBound, upperBound);
        }
        return input;
    }

    public static String select(String prompt, ArrayList<String> options) {
        // Print options
        System.out.println(prompt);
        options.forEach(option -> System.out.println("- " + option));
        if(Localization.bundle == null) {
            System.out.print("Your choice: "); // Fallback when language is not configured
        } else {
            System.out.print(Localization.getMessage("userinput.prompt.choice"));
        }

        String selection = DataNormalization.word(scanner.nextLine());
        while (!options.contains(selection)) {
            if(Localization.bundle == null) {
                System.out.print("Select one of the provided options: ");
            } else {
                System.out.println(Localization.getMessage("userinput.prompt.choice.invalid"));
            }
            selection = scanner.nextLine();
        }
        return selection;
    }

    // Enables selection from enum values
    public static <T extends Enum<T>> String select(String prompt, T[] options) {
        // Convert enum type array to ArrayList of enum names for unified selection
        return select(prompt, new ArrayList<>(Arrays.asList(Arrays.stream(options).map(Enum::name).toArray(String[]::new))));
    }
}
