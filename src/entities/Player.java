package entities;

import utilities.DataNormalization;
import utilities.Localization;
import utilities.UserInput;

public class Player {
    public String name;
    public Team team;

    public void setName(String prompt) {
        String nameInput = UserInput.text(prompt);
        name = DataNormalization.name(nameInput, ' ', ' ');
    }
}
