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

    public static void main(String[] args) {
        DB.fetchData(new CSVLoader());

        // Setup
        Localization.configureLanguage();
        setNames();

        // Game Loop
        do {
            Fight fight = new Fight(user, opponent);
            fight.assembleTeams();
            fight.battle();
            fight.printResult();
        } while (UserInput.confirm(Localization.getMessage("game.prompt.play.again")));
    }

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