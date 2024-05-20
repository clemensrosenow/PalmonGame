package entities;

import utilities.DataNormalization;
import utilities.UserInput;

/**
 * Represents a player in the game, either user or opponent.
 */
public class Player {
    public String name;
    public Team team;

    /**
     * Prompts the user to enter a name and sets it for the player.
     *
     * @param prompt the prompt message for the user
     */
    public void setName(String prompt) {
        String nameInput = UserInput.text(prompt);
        name = DataNormalization.name(nameInput, ' ', ' ');
    }
}
