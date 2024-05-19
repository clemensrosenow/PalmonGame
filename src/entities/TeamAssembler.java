package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.*;

class TeamAssembler {
    private final int palmonCount;
    private final int minLevel;
    private final int maxLevel;
    protected HashSet<Palmon> palmons;
    public enum Method {
        random, id, type
    }

    TeamAssembler(int palmonCount, int minLevel, int maxLevel, Method assemblyMethod) {
        this.palmonCount = palmonCount;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.palmons = new HashSet<>();

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
    private boolean assemblyUncompleted() {
        return palmons.size() < palmonCount;
    }

    private void add(Palmon palmon) {
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.assignHighestDamageMoves();
        palmons.add(palmon);
    }

    private void assembleRandomly() {
        while (assemblyUncompleted()) {
            Palmon randomPalmon = DB.getRandomPalmon();
            if (!palmons.contains(randomPalmon)) {
                add(randomPalmon);
            }
        }
    }

    private void assembleByType() {
        HashMap<String, ArrayList<Palmon>> palmonsGroupedByType = DB.getPalmonsByType();

        //Output selectable types
        System.out.println("Now select your favorite Palmons by their " + TeamAssembler.Method.type.name() + "!");
        ArrayList<String> availableTypes = new ArrayList<>(palmonsGroupedByType.keySet());
        Collections.sort(availableTypes); //Sort types in ascending alphabetical order

        while (assemblyUncompleted()) {
            String selectedType = UserInput.select("Which Palmon type do you want? ", availableTypes);
            ArrayList<Palmon> availablePalmons = palmonsGroupedByType.get(selectedType);
            TableOutput.printPalmonTable(availablePalmons);
            addPalmonById("Which " + selectedType + " Palmon do you want in your team? Enter its ID: ", availablePalmons);
        }
    }

    private void assembleById() {
        System.out.println("Now select your favorite Palmons by their " + TeamAssembler.Method.id.name() + "!");

        ArrayList<Palmon> totalPalmons = DB.getPalmons();
        TableOutput.printPalmonTable(totalPalmons);

        while (assemblyUncompleted()) {
            addPalmonById("Which Palmon do you want in your team? Enter its ID: ", totalPalmons);
        }
    }

    private Palmon addPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        final int maxPalmonId = 10194;
        int selectedId = UserInput.number(prompt, 1, maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if (optionalPalmon.isEmpty()) {
            return addPalmonById("No Palmon exists for this ID. Enter a different one: ", dataSource);
        }

        Palmon selectedPalmon = optionalPalmon.get();
        if (palmons.contains(selectedPalmon)) {
            return addPalmonById("The selected Palmon is already in your team. Please select a different one: ", dataSource);
        }

        add(selectedPalmon);

        System.out.println(selectedPalmon.name + " has been added to your team.");
        ExecutionPause.sleep(1);
        return selectedPalmon;
    }
}
