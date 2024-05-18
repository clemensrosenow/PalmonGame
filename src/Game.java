import entities.Fight;
import entities.Player;
import utilities.UserInput;
import utilities.CSVProcessing;
import utilities.Localization;

import java.util.concurrent.*;

public class Game {
    static Player user = new Player();
    static Player opponent = new Player();

    public static void main(String[] args) {
        CompletableFuture<Void> asyncFileLoading = CSVProcessing.loadCSVFiles(); //Start processing CSV files in seperate thread
        System.out.println("Welcome to Palmon!");

        //Setup
        Localization.configureLanguage();
        setNames();
        asyncFileLoading.join(); //Block execution until CSV processing is complete

        //Game Round Loop
        do {
            Fight fight = new Fight(user, opponent);
            fight.assembleTeams();
            fight.battle();
            fight.printResult();
        } while (UserInput.confirm("Do you want to play another round?"));
    }
    private static void setNames() {
        user.setName("name_user_question");
        System.out.println(Localization.getMessage("greeting") + " " + user.name + "!");
        opponent.setName("name_opponent_question");
        System.out.println(opponent.name + " will destroy you!");
    }
    private Game(){}; // Private constructor to prevent instantiation
}