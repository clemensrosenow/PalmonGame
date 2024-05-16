import entities.Fight;
import entities.Player;
import utils.UserInput;
import utils.CSVProcessing;
import utils.Localization;

import java.util.concurrent.*;

public class Game {
    static Player user = new Player();
    static Player opponent = new Player();

    public static void main(String[] args) {
        CompletableFuture<Void> asyncFileLoading = CSVProcessing.loadCSVFiles(); //Start processing CSV files in seperate thread
        System.out.println("Welcome to Palmon!");

        System.out.println("OMG".repeat(3));
        //Setup
        Localization.configureLanguage();
        user.setName("name_player_question");
        System.out.println(Localization.getMessage("greeting") + " " + user.name + "!");
        opponent.setName("name_opponent_question");

        asyncFileLoading.join(); //Block execution until CSV processing is complete

        //Game Round Loop
        do {
            Fight fight = new Fight(user, opponent);
            fight.assembleTeams();
            fight.battle();
            fight.determineResult();
        } while (UserInput.confirm("Do you want to play another round?"));
    }
}