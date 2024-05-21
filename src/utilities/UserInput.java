package utilities;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Utility class for handling repeated user console input.
 */
public class UserInput {
    static Scanner scanner = new Scanner(System.in); // Scanner instance for reading user input

    private UserInput() { // Private constructor to prevent instantiation
    }

    /**
     * Prompts the user to enter text.
     *
     * @param prompt the message to display to the user
     * @return the normalized user's input as a non-empty string
     */
    public static String text(String prompt) {
        System.out.print("\n" + prompt + " ");

        // Scan the input and normalize it
        String input = DataNormalization.name(scanner.nextLine(), ' ', ' ');

        // Ensure the input is not empty
        while (input.isEmpty()) {
            System.out.print(Localization.getMessage("userinput.prompt.text.empty"));
            input = DataNormalization.name(scanner.nextLine(), ' ', ' ');
        }
        return input;
    }

    /**
     * Prompts the user to confirm with a yes/no question.
     *
     * @param prompt the message to display to the user
     * @return true if the user confirms with "yes", false otherwise
     */
    public static boolean confirm(String prompt) {
        enum Options {
            yes, no;

            // For option localization
            public String getLocalized() {
                return Localization.getMessage("enum.confirmation." + this.name());
            }
        }
        // Convert enum values to list of localized strings for selection and comparison
        ArrayList<String> options = Arrays.stream(Options.values()).map(Options::getLocalized).collect(Collectors.toCollection(ArrayList::new));
        return select(prompt, options).equals(Options.yes.getLocalized());
    }

    /**
     * Prompts the user to enter a number.
     *
     * @param prompt the message to display to the user
     * @return the user's input as an integer
     */
    public static int number(String prompt) {
        System.out.print("\n" + prompt + " ");
        try {
            int input = scanner.nextInt();
            scanner.nextLine(); // Fully consume input including next line character to avoid unexpected behavior
            return input;
        } catch (InputMismatchException exception) {
            // Error handling when entered value is not a number
            scanner.nextLine(); // Discard invalid token
            return number(Localization.getMessage("userinput.prompt.number"));
        }
    }

    /**
     * Prompts the user to enter a number within a specified range.
     *
     * @see #number(String) for base utility
     * @param prompt     the message to display to the user
     * @param lowerBound the minimum acceptable value
     * @param upperBound the maximum acceptable value
     * @return the user's input as an integer within the specified range
     */
    public static int number(String prompt, int lowerBound, int upperBound) {
        int input = number(prompt);

        // Recursive error handling when values lies outside range to avoid complex looping
        if (input < lowerBound) {
            return number(Localization.getMessage("userinput.prompt.number.range.lower", lowerBound), lowerBound, upperBound);
        }
        if (input > upperBound) {
            return number(Localization.getMessage("userinput.prompt.number.range.upper", upperBound), lowerBound, upperBound);
        }
        return input;
    }

    /**
     * Prompts the user to select an option from a list of strings.
     *
     * @param prompt  the message to display to the user
     * @param options the list of options to choose from
     * @return the user's selected option
     */
    public static String select(String prompt, ArrayList<String> options) {
        // Print prompt, options and input request
        System.out.println("\n" + prompt);
        options.forEach(option -> System.out.println("- " + option));
        if(Localization.bundle == null) {
            System.out.print("Your choice: "); // Fallback when language is not configured at the beginning
        } else {
            System.out.print(Localization.getMessage("userinput.prompt.choice") + " ");
        }

        // Get the user selection and normalize it
        String selection = DataNormalization.word(scanner.nextLine());
        while (!options.contains(selection)) {
            // Error handling for unavailable option selection
            if(Localization.bundle == null) {
                System.out.print("Select one of the provided options: "); // Fallback when language is not configured at the beginning
            } else {
                System.out.print(Localization.getMessage("userinput.prompt.choice.invalid") + " ");
            }
            selection = DataNormalization.word(scanner.nextLine());
        }
        return selection;
    }

    /**
     * Prompts the user to select an option from an array of enum values.
     *
     * @see #select(String, ArrayList) for base utility
     * @param prompt  the message to display to the user
     * @param options the array of enum options to choose from
     * @param <T>     the enum type
     * @return the user's selected option
     */
    public static <T extends Enum<T>> String select(String prompt, T[] options) {
        // Convert enum T[] to ArrayList of enum names for base utility call
        return select(prompt, new ArrayList<>(Arrays.asList(Arrays.stream(options).map(Enum::name).toArray(String[]::new))));
    }
}
