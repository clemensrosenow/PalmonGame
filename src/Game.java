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

        //Setup
        Localization.configureLanguage();
        setNames();

        //Game Loop
        do {
            Fight fight = new Fight(user, opponent);
            fight.assembleTeams();
            fight.battle();
            fight.printResult();
        } while (UserInput.confirm("Do you want to play another round?"));
    }
    private static void setNames() {
        user.setName("name_user_question");
        System.out.println("Welcome to the Palmon Arena, " + user.name + "!");
        ExecutionPause.sleep(1);
        //System.out.println(Localization.getMessage("greeting") + " " + user.name + "!");
        opponent.setName("name_opponent_question");
        System.out.println(opponent.name + " will destroy you!");
        ExecutionPause.sleep(1);
    }
    private Game(){}; // Private constructor to prevent instantiation
}