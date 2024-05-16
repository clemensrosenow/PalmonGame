package entities;

import utils.CSVProcessing;
import utils.Localization;
import utils.UserInput;

public class Player {
    public String name;
    public Team team;

    public void setName(String messageKey) {
        String nameInput = UserInput.text(Localization.getMessage(messageKey));
        name = CSVProcessing.normalize(nameInput);
    }
}
