import java.util.*;
import java.util.concurrent.*;

public class Game {
    static Character player = new Character();
    static Character opponent = new Character();
    static Round round;
    static ResourceBundle consoleText;

    public static void main(String[] args) {
        CompletableFuture<Void> asyncFileLoading = Data.loadCSVFiles();
        Setup.configureLanguage();
        Setup.assignCharacterNames();
        asyncFileLoading.join(); //Block until CSV processing is complete
        do {
            round = new Round(player, opponent);
            round.assembleTeams();
            round.fight();
        } while (Input.confirm("Do you want to play another round?"));

    }

    private static class Setup {
        public static void configureLanguage() {
            Locale.setDefault(new Locale("en"));
            System.out.println("Welcome to Palmon!");
            Locale userLanguage = new Locale(Input.select("What's your preferred language?", Map.of("en", "English (default)", "de", "Deutsch")));
            Game.consoleText = ResourceBundle.getBundle("ConsoleText", userLanguage);
        }

        public static void assignCharacterNames() {
            Game.player.name = Data.normalize(Input.text(Game.consoleText.getString("name_player_question")));
            System.out.println(Game.consoleText.getString("greeting") + " " + Game.player.name + "!");
            Game.opponent.name = Data.normalize(Input.text(Game.consoleText.getString("name_opponent_question")));
        }
    }
}