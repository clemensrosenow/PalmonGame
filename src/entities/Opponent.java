package entities;

import resources.DB;
import utilities.*;

import java.util.Random;

public class Opponent implements Player {
    public String name;
    public Team team;

    /**
     * Sets the name based on normalized user input.
     */
    @Override
    public void setName() {
        String nameInput = UserInput.text(Localization.getMessage("game.prompt.opponent.name"));
        name = DataNormalization.name(nameInput, ' ', ' ');
        System.out.println(Localization.getMessage("game.opponent.taunt", name));
        ExecutionPause.sleep(1);
    }

    /**
     * Assembles the team for the opponent, optionally allowing the user to specify the team size.
     * Level range and assembly method can't be influenced by the user.
     *
     * @param userPalmonCount the amount of palmons in the user team as a base value for the random opponent team size
     */
    @Override
    public void assembleTeam(int userPalmonCount) {
        int opponentPalmonCount;
        // Set team size, limited to half count of all Palmons
        // Allow user to set specific team size if desired
        if (UserInput.confirm(Localization.getMessage("fight.prompt.opponent.team.size", name))) {
            opponentPalmonCount = UserInput.number(Localization.getMessage("fight.prompt.opponent.team.size.input"), 1, DB.halfPalmonCount());
        } else {
            // If not, randomly generate team size based on user team size
            Random random = new Random();
            int randomPalmonCount = random.nextInt(userPalmonCount * 2 - 1) + 1; // set count to random int >= 1, which averages the user team size
            opponentPalmonCount = Math.min(randomPalmonCount, DB.halfPalmonCount());
        }

        // Set up team
        // Always random assembly with full level range
        team = new Team(opponentPalmonCount, Palmon.lowestLevelPossible, Palmon.highestLevelPossible, TeamAssembler.Method.random);
        ExecutionPause.sleep(2);
    }
}