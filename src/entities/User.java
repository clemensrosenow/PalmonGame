package entities;

import utilities.*;

import java.util.ArrayList;
import java.util.Arrays;

public class User implements Player {
    public String name;
    public Team team;

    /**
     * Sets the name based on normalized user input.
     */
    @Override
    public void setName() {
        String nameInput = UserInput.text(Localization.getMessage("game.prompt.name"));
        name = DataNormalization.name(nameInput, ' ', ' ');
        System.out.println(Localization.getMessage("game.welcome.message", name));
        ExecutionPause.sleep(1);
    }

    /**
     * Assembles the team for the user by prompting for parameters.
     * These include the team size, optionally a level range, and the assembly method.
     *
     * @see TeamAssembler for the available assembly methods
     * @param palmonCount the amount of Palmons in the team
     */
    @Override
    public void assembleTeam(int palmonCount) {
        // Level Range Base Values
        int minLevel = Palmon.lowestLevelPossible;
        int maxLevel = Palmon.highestLevelPossible;

        // Optional Level Range Selection
        if (UserInput.confirm(Localization.getMessage("fight.prompt.level.range"))) {
            // Inclusion of minimumRange, so enough moves can be assigned
            minLevel = UserInput.number(Localization.getMessage("fight.prompt.lowest.level"), Palmon.lowestLevelPossible, Move.medianUnlockLevel);
            int minimumRange = Palmon.highestLevelPossible - Move.medianUnlockLevel; // Ensures a minimum level range based on the median unlock level
            maxLevel = UserInput.number(Localization.getMessage("fight.prompt.highest.level"), minLevel + minimumRange, Palmon.highestLevelPossible);
        }

        // Assembly Method Selection
        String selectedMethod = UserInput.select(Localization.getMessage("fight.prompt.select.attribute"), TeamAssembler.Method.getLocalizedOptions());
        TeamAssembler.Method assemblyMethod = Arrays.stream(TeamAssembler.Method.values()).filter(method -> method.getLocalized().equals(selectedMethod)).findFirst().orElse(TeamAssembler.Method.random); //Map selected method to enum, value is always found

        // Set up team
        team = new Team(palmonCount, minLevel, maxLevel, assemblyMethod);

        // Print overview of team Palmons
        System.out.println("\n" + Localization.getMessage("fight.team.consists.of"));
        ExecutionPause.sleep(2);
        TableOutput.printPalmonTable(new ArrayList<>(team.palmons));

        ExecutionPause.sleep(3);
    }
}
