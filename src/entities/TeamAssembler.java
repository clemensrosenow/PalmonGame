package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.TableOutput;
import utilities.UserInput;
import utilities.Localization;

import java.util.*;
import java.util.stream.Collectors;

class TeamAssembler {
    private final int palmonCount;
    private final int minLevel;
    private final int maxLevel;
    protected final HashSet<Palmon> palmons;

    /**
     * Enumeration of the different methods for assembling a team.
     */
    enum Method {
        random, id, type;

        /**
         * Returns the localized name of the assembly method.
         * Necessary for localization of the selection options.
         *
         * @return the localized name
         */
        String getLocalized() {
            return Localization.getMessage("enum.assembler." + this.name());
        }

        /**
         * Returns a list of localized selection options
         *
         * @return a list of localized options
         */
        static ArrayList<String> getLocalizedOptions() {
            return Arrays.stream(Method.values()).map(Method::getLocalized).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    /**
     * Assembles a team with the specified parameters.
     *
     * @param palmonCount the number of Palmons to assemble
     * @param minLevel the minimum possible level of the Palmons
     * @param maxLevel the maximum possible level of the Palmons
     * @param assemblyMethod the method used to assemble the team
     */
    TeamAssembler(int palmonCount, int minLevel, int maxLevel, Method assemblyMethod) {
        this.palmonCount = palmonCount;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.palmons = new HashSet<>();

        // Assemble based on the selected method, random by default
        switch (assemblyMethod) {
            case id:
                assembleById();
                break;
            case type:
                assembleByType();
                break;
            case random:
            default:
                assembleRandomly();
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
     *
     * @param palmon the Palmon to add
     */
    private void add(Palmon palmon) {
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.assignHighestDamageMoves();
        palmons.add(palmon);
    }

    /**
     * Assembles the team randomly from the available Palmons in the database.
     */
    private void assembleRandomly() {
        while (assemblyUncompleted()) {
            Palmon randomPalmon = DB.getRandomPalmon();
            if (!palmons.contains(randomPalmon)) { // Ensure no duplicates
                add(randomPalmon);
            }
        }
    }

    /**
     * Assembles the team based on the Palmon type as a preselection.
     * @see DB#getPalmonsByType() for the Palmon type grouping
     * @see TeamAssembler#addPalmonById(String, ArrayList) for the secondary selection
     */
    private void assembleByType() {
        HashMap<String, ArrayList<Palmon>> palmonsGroupedByType = DB.getPalmonsByType();

        // Output selectable types
        System.out.println(Localization.getMessage("team.assembler.select", TeamAssembler.Method.type.name()));
        ArrayList<String> availableTypes = new ArrayList<>(palmonsGroupedByType.keySet()); // Get all available types in a list
        Collections.sort(availableTypes); // Sort the types in ascending alphabetical order

        while (assemblyUncompleted()) {
            // Select desired type and display all its Palmons in a table
            String selectedType = UserInput.select(Localization.getMessage("team.assembler.select.type"), availableTypes);
            ArrayList<Palmon> availablePalmons = palmonsGroupedByType.get(selectedType);
            TableOutput.printPalmonTable(availablePalmons);

            // Choose one of the available Palmons by ID
            addPalmonById(Localization.getMessage("team.assembler.select.by.type.prompt", selectedType), availablePalmons);
        }
    }

    /**
     * Assembles the team by selecting Palmons by their IDs.
     * @see TeamAssembler#addPalmonById(String, ArrayList) for the selection process
     */
    private void assembleById() {
        System.out.println(Localization.getMessage("team.assembler.select", TeamAssembler.Method.id.name()));

        // Print all Palmons in a table
        ArrayList<Palmon> totalPalmons = DB.getPalmons();
        TableOutput.printPalmonTable(totalPalmons);

        while (assemblyUncompleted()) {
            // Choose Palmon by ID from of all available Palmons
            addPalmonById(Localization.getMessage("team.assembler.select.by.id.prompt"), totalPalmons);
        }
    }

    /**
     * Adds a Palmon to the team by prompting the user to select an ID.
     *
     * @param prompt the prompt message for the user
     * @param dataSource the data source containing the Palmons to choose from
     * @return the added Palmon
     */
    private Palmon addPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        final int maxPalmonId = 10194; // Highest Palmon ID, derived from CSV file

        // Select a potential Palmon by ID
        int selectedId = UserInput.number(prompt, 1, maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        // Error handling for absent Palmon ID
        if (optionalPalmon.isEmpty()) {
            // Recursive call with error message
            return addPalmonById(Localization.getMessage("team.assembler.no.palmon.exists"), dataSource);
        }

        Palmon selectedPalmon = optionalPalmon.get(); // Extract the present Palmon from the Optional

        // Edge case: already added Palmon
        if (palmons.contains(selectedPalmon)) {
            // Recursive call with duplicate notice
            return addPalmonById(Localization.getMessage("team.assembler.already.in.team"), dataSource);
        }

        // Add the selected Palmon to the team and print a confirmation message
        add(selectedPalmon);
        System.out.println(Localization.getMessage("team.assembler.added", selectedPalmon.name));
        ExecutionPause.sleep(1);

        return selectedPalmon; // No use other than enabling recursion
    }
}
