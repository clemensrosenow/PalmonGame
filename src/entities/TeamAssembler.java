package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.Localization;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.*;
import java.util.stream.Collectors;

class TeamAssembler {
    protected final HashSet<Palmon> palmons;
    private final int palmonCount;
    private final int minLevel;
    private final int maxLevel;

    /**
     * Assembles a team with the specified parameters.
     *
     * @param palmonCount    the number of Palmons to assemble
     * @param minLevel       the minimum possible level of the Palmons
     * @param maxLevel       the maximum possible level of the Palmons
     * @param assemblyMethod the method used to assemble the team
     */
    TeamAssembler(int palmonCount, int minLevel, int maxLevel, Method assemblyMethod) {
        this.palmonCount = palmonCount;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.palmons = new HashSet<>();

        boolean userAssembles = assemblyMethod == Method.id || assemblyMethod == Method.type;

        if (userAssembles) {
            // Assembly call to action expect for random assembly
            System.out.println(Localization.getMessage("team.assembler.select", assemblyMethod.getLocalized()));
            ExecutionPause.sleep(2);
        }
        if (assemblyMethod == Method.id) {
            // Print all available Palmons in a table
            TableOutput.printPalmonTable(DB.getUnusedPalmons());
        }

        // Assemble until the team has the required number of Palmons
        while (assemblyUncompleted()) {
            Palmon selectedPalmon = switch (assemblyMethod) {
                case random -> DB.getRandomUnusedPalmon();
                case id -> selectPalmonById(DB.getUnusedPalmons());
                case type -> selectPalmonByType(DB.getUnusedPalmonsGroupedByType());
            };
            add(selectedPalmon, userAssembles);
        }
    }

    /**
     * Checks if the assembly process is incomplete.
     *
     * @return true if the team has fewer Palmons than required, false otherwise
     */
    private boolean assemblyUncompleted() {
        return palmons.size() < palmonCount;
    }

    /**
     * Adds a Palmon to the team, setting its level and assigning its moves.
     * Optionally display a confirmation message to the user.
     *
     * @param palmon       the Palmon to add
     * @param confirmation true if a confirmation message should be displayed, false otherwise
     */
    private void add(Palmon palmon, boolean confirmation) {
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.assignHighestDamageMoves();
        palmons.add(palmon);
        palmon.inTeam = true;
        if (confirmation) {
            System.out.println(Localization.getMessage("team.assembler.added", palmon.name));
            ExecutionPause.sleep(1);
        }
    }

    /**
     * Assembles the team based on the Palmon type as a preselection.
     *
     * @param palmonsGroupedByType a group of types with Palmons as the value
     * @see DB#getUnusedPalmonsGroupedByType() for the Palmon type grouping
     * @see TeamAssembler#selectPalmonById(ArrayList, String) for the secondary selection
     */
    private Palmon selectPalmonByType(HashMap<String, ArrayList<Palmon>> palmonsGroupedByType) {
        // Get all available types in a sorted list
        ArrayList<String> availableTypes = new ArrayList<>(palmonsGroupedByType.keySet()); // Get all available types in a list
        Collections.sort(availableTypes); // Sort the types in ascending alphabetical order

        // Select desired type and display all its Palmons in a table
        String selectedType = UserInput.select(Localization.getMessage("team.assembler.select.type"), availableTypes);
        ArrayList<Palmon> availablePalmons = palmonsGroupedByType.get(selectedType);
        TableOutput.printPalmonTable(availablePalmons);

        // Choose one of the available Palmons by ID
        return selectPalmonById(availablePalmons, Localization.getMessage("team.assembler.select.by.type.prompt", selectedType));
    }

    /**
     * Prompts the user to select a Palmon by its ID from a data source.
     *
     * @param prompt     the prompt message for the user
     * @param dataSource the data source containing the Palmons to choose from
     * @return the selected Palmon
     */
    private Palmon selectPalmonById(ArrayList<Palmon> dataSource, String prompt) {
        final int maxPalmonId = 10194; // Highest Palmon ID, derived from CSV file

        // Select a potential Palmon by ID
        int selectedId = UserInput.number(prompt, 1, maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        // Extract the present Palmon or else handle the absence with recursive error handling call
        return optionalPalmon.orElseGet(() -> selectPalmonById(dataSource, Localization.getMessage("team.assembler.no.palmon.exists")));
    }

    /**
     * Prompts the user to select a Palmon by its ID from a data source.
     * Overloaded method with a default prompt message.
     *
     * @param dataSource the data source containing the Palmons to choose from
     * @return the selected Palmon
     */
    private Palmon selectPalmonById(ArrayList<Palmon> dataSource) {
        return selectPalmonById(dataSource, Localization.getMessage("team.assembler.select.by.id"));
    }

    /**
     * Enumeration of the different methods for assembling a team.
     */
    enum Method {
        random, id, type;

        /**
         * Returns a list of localized selection options
         *
         * @return a list of localized options
         */
        static ArrayList<String> getLocalizedOptions() {
            return Arrays.stream(Method.values()).map(Method::getLocalized).collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * Returns the localized name of the assembly method.
         * Necessary for localization of the selection options.
         *
         * @return the localized name
         */
        String getLocalized() {
            return Localization.getMessage("enum.assembler." + this.name());
        }
    }
}
