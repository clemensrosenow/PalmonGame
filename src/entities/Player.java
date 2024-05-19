package entities;

import utilities.DataNormalization;
import utilities.Localization;
import utilities.UserInput;

public class Player {
    public String name;
    public Team team;

    public void setName(String messageKey) {
        String nameInput = UserInput.text(Localization.getMessage(messageKey));
        name = DataNormalization.name(nameInput, ' ', ' ');
    }
}
