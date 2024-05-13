package utils;

import java.util.*;

//Mini-Library for repeated user console input
public class Input {
    static Scanner scanner = new Scanner(System.in);

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
        return select(prompt, Set.of("yes", "no")).equals("yes");

    }

    public static int number(String prompt) {
        System.out.print("\n" + prompt);
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // fully consume input
                return input;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // discard invalid token
                number("Enter a value valid number: ");
            }
        }
    }


    public static int number(String prompt, int lowerBound, int upperBound) {
        int input = number(prompt);
        if (input < lowerBound) {
            return number("Enter a value greater than or equal to " + lowerBound + ": ", lowerBound, upperBound);
        }
        if (input > upperBound) {
            return number("Enter a value smaller than or equal to " + upperBound + ": ", lowerBound, upperBound);
        }
        return input;
    }


    //Possible Abstract to avoid repetition (e.g. using helper function)
    public static String select(String prompt, Map<String,String> options) {
        System.out.println("\n" + prompt);
        options.forEach((key, value) -> System.out.println("- " + key + ": " + value));
        System.out.print("Your choice: ");
        String selection = scanner.nextLine();
        while (!options.containsKey(selection)) {
            System.out.print("Select one of the provided options: ");
            selection = scanner.nextLine();
        }
        return selection;
    }
    //Todo: Abstract to accept enum as options parameter and return one of its values
    public static String select(String prompt, Set<String> options) {
        System.out.println("\n" + prompt);
        options.forEach(key -> System.out.println("- " + key));
        System.out.print("Your choice: ");
        String selection = scanner.nextLine();
        while (!options.contains(selection)) {
            System.out.print("Select one of the provided options: ");
            selection = scanner.nextLine();
        }
        return selection;
    }



}