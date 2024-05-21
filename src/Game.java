import entities.Fight;
import entities.Opponent;
import entities.User;
import utilities.CSVProcessing.CSVLoader;
import utilities.UserInput;
import utilities.Localization;
import resources.DB;

public class Game {
    static User user = new User();
    static Opponent opponent = new Opponent();

    /**
     * Main method to start the game.
     */
    public static void main(String[] args) {
        // Setup
        DB.fetchData(new CSVLoader()); // Load data from CSV files in separate thread
        Localization.configureLanguage(); // Set language of the ResourceBundle for console messages
        user.setName();
        opponent.setName();

        do {
            // Fight loop
            Fight fight = new Fight(user, opponent);
            fight.assembleTeams();
            fight.battle();
            fight.printResult();
        } while (UserInput.confirm(Localization.getMessage("game.prompt.play.again"))); // Ask for another round
    }

    private Game() {} // Private constructor to prevent instantiation
}