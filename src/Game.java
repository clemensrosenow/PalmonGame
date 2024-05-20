import entities.Fight;
import entities.Player;
import utilities.CSVProcessing.CSVLoader;
import resources.DB;
import utilities.ExecutionPause;
import utilities.UserInput;
import utilities.Localization;

public class Game {
    static Player user = new Player();
    static Player opponent = new Player();

    /**
     * Main method to start the game.
     */
    public static void main(String[] args) {
        // Setup
        DB.fetchData(new CSVLoader()); // Load data from CSV files in separate thread
        Localization.configureLanguage(); // Set language of the ResourceBundle for console messages
        setNames();

        do {
            // Fight loop
            Fight fight = new Fight(user, opponent);
            fight.assembleTeams();
            fight.battle();
            fight.printResult();
        } while (UserInput.confirm(Localization.getMessage("game.prompt.play.again"))); // Ask for another round
    }

    /**
     * Sets the names of the user and opponent.
     */
    private static void setNames() {
        user.setName(Localization.getMessage("game.prompt.name"));
        System.out.println(Localization.getMessage("game.welcome.message", user.name));
        ExecutionPause.sleep(1);
        opponent.setName(Localization.getMessage("game.prompt.opponent.name"));
        System.out.println(Localization.getMessage("game.opponent.taunt", opponent.name));
        ExecutionPause.sleep(1);
    }

    private Game() {} // Private constructor to prevent instantiation
}