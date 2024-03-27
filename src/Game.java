import java.util.*;
import java.util.concurrent.*;

public class Game {
    static Player player = Player.getInstance();
    static Opponent opponent = Opponent.getInstance();
    static ResourceBundle consoleText;

    public static void main(String[] args) {
        CompletableFuture<Void> asyncFileLoading = Data.loadCSVFiles();
        setup();
        asyncFileLoading.join(); //Block until CSV processing is complete*/

        /*System.out.printf("%-10d %-20s %-30s %n", 0, "|Name", "Role");
        System.out.println("─");
        System.out.printf("%-10d %-20s %-30s %n", 1, "|Alice", "Software Engineer");
        System.out.printf("%-10s %-20s %-30s %n", "├────────", "┼──────────────────", "┼────────────────────────────┤");

        System.out.printf("%-10d %-20s %-30s %n", 2, "|Bob", "Data Scientist");
        System.out.printf("%-10s %-20s %-30s %n", "├────────", "┼──────────────────", "┼────────────────────────────┤");
        System.out.printf("%-10d %-20s %-30s %n", 3, "|Charlie", "Product Manager");*/
        assembleTeams();
    }

    public static void setup() {
        System.out.println("Welcome to Palmon!");
        configureLanguage();
        assignCharacterNames();
        assembleTeams();
    }

    public static void configureLanguage() {
        Locale.setDefault(new Locale("en"));
        Map<String, String> languageOptions = Map.of("en", "English (default)", "de", "Deutsch");
        Locale userLanguage = new Locale(Input.select("What's your preferred language?", languageOptions));
        consoleText = ResourceBundle.getBundle("ConsoleText", userLanguage);
    }

    public static void assignCharacterNames() {
        player.name = Data.normalize(Input.text(consoleText.getString("name_player_question")));
        System.out.println(consoleText.getString("greeting") + " " + player.name + "!");
        opponent.name = Data.normalize(Input.text(consoleText.getString("name_opponent_question")));
    }

    public static void assembleTeams() {
        // Palmon Count Selection
        final int totalPalmons = 1092; //Checked in Palmon CSV
        int playerPalmonCount = Input.number("How many Palmons do you want to have in your team? ", 1, totalPalmons);
        int opponentPalmonCount = Input.number("How many Palmons does your opponent have in his team? ", 1, totalPalmons);

        // Assemble Method Selection
        Map<String, String> assembleMethods = Map.of("random", "randomly (default)", "id", "by ID", "type", "by Type");
        String assembleMethod = Input.select("How do you want to select your Palmons?", assembleMethods);

        // Level Range Selection
        final int totalLevels = 100; //Based on max value of all rows in palmon_move.csv
        int minLevel = Input.number("What's the lowest possible level of your Palmons? ", 0, totalLevels);
        int maxLevel = Input.number("What's the highest possible level of your Palmons? ", minLevel, totalLevels);

        // Setup
        player.team = new Team(playerPalmonCount, minLevel, maxLevel);
        switch (assembleMethod) {
            case "id":
                player.team.assembleById();
                break;
            case "type":
                player.team.assembleByType();
                break;
            case "random":
            default:
                player.team.assembleRandomly();
        }

        // Opponent team always consists of random palmons in full level range
        opponent.team = new Team(opponentPalmonCount, 0, totalLevels);
        opponent.team.assembleRandomly();
    }
}