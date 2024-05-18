package entities;

import utilities.CSVProcessing;
import utilities.Localization;
import utilities.UserInput;

public class Player {
    public String name;
    public Team team;

    public void setName(String messageKey) {
        String nameInput = UserInput.text(Localization.getMessage(messageKey));
        name = CSVProcessing.normalize(nameInput);
    }
}
