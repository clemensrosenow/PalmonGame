package utilities;

import java.util.*;

//Mini-Library for repeated user console input
public class UserInput {
    static Scanner scanner = new Scanner(System.in);

    private UserInput() { // Private constructor to prevent instantiation
    }

    public static String text(String prompt) {
        System.out.print("\n" + prompt + " ");
        String input = scanner.nextLine();
        while (input.isEmpty()) {
            System.out.print("Text can't be empty: ");
            input = scanner.nextLine();
        }
        return input;
    }

    public static boolean confirm(String prompt) {
        enum Options {
            yes, no
        }
        return Options.valueOf(select(prompt, Options.values())).equals(Options.yes);
    }

    public static int number(String prompt) {
        System.out.print("\n" + prompt);
        try {
            int input = scanner.nextInt();
            scanner.nextLine(); // fully consume input
            return input;
        } catch (InputMismatchException exception) {
            scanner.nextLine(); // discard invalid token
            return number("Enter a valid number: ");
        }
    }

    public static int number(String prompt, int lowerBound, int upperBound) {
        int input = number(prompt);

        //Recursive error handling to avoid complex looping
        if (input < lowerBound) {
            return number("Enter a number greater than or equal to " + lowerBound + ": ", lowerBound, upperBound);
        }
        if (input > upperBound) {
            return number("Enter a number smaller than or equal to " + upperBound + ": ", lowerBound, upperBound);
        }
        return input;
    }

    //Todo: Convert to ArrayList as Input, because with set the displayed order is reversed
    public static String select(String prompt, ArrayList<String> options) {
        //Print options
        System.out.println("\n" + prompt);
        options.forEach(option -> System.out.println("- " + option));
        System.out.print("Your choice: ");

        String selection = scanner.nextLine();
        while (!options.contains(selection)) {
            System.out.print("\nSelect one of the provided options: ");
            selection = scanner.nextLine();
        }
        return selection;
    }

    //Enables selection from enum values
    public static <T extends Enum<T>> String select(String prompt, T[] options) {
        //Covert enum type array to ArrayList of enum names for unified selection
        return select(prompt, new ArrayList<>(Arrays.asList(Arrays.stream(options).map(Enum::name).toArray(String[]::new))));
    }

}
