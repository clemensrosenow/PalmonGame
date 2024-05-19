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
    protected HashSet<Palmon> palmons;
    public enum Method {
        random(Localization.getMessage("enum.assembler.random")), id(Localization.getMessage("enum.assembler.id")), type(Localization.getMessage("enum.assembler.type"));
        public final String name;
        Method(String name) {
            this.name = name;
        }
        public String getLocalized() {
            return Localization.getMessage("enum.assembler." + this.name());
        }

        public static ArrayList<String> getLocalizedOptions() {
            return Arrays.stream(Method.values()).map(Method::getLocalized).collect(Collectors.toCollection(ArrayList::new));
        }
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

        // Output selectable types
        System.out.println(Localization.getMessage("team.assembler.select.by.type", TeamAssembler.Method.type.name()));
        ArrayList<String> availableTypes = new ArrayList<>(palmonsGroupedByType.keySet());
        Collections.sort(availableTypes); // Sort types in ascending alphabetical order

        while (assemblyUncompleted()) {
            String selectedType = UserInput.select(Localization.getMessage("team.assembler.select.type"), availableTypes);
            ArrayList<Palmon> availablePalmons = palmonsGroupedByType.get(selectedType);
            TableOutput.printPalmonTable(availablePalmons);
            addPalmonById(Localization.getMessage("team.assembler.select.by.type.prompt", selectedType), availablePalmons);
        }
    }

    private void assembleById() {
        System.out.println(Localization.getMessage("team.assembler.select.by.id", TeamAssembler.Method.id.name()));

        ArrayList<Palmon> totalPalmons = DB.getPalmons();
        TableOutput.printPalmonTable(totalPalmons);

        while (assemblyUncompleted()) {
            addPalmonById(Localization.getMessage("team.assembler.select.by.id.prompt"), totalPalmons);
        }
    }

    private Palmon addPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        final int maxPalmonId = 10194;
        int selectedId = UserInput.number(prompt, 1, maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if (optionalPalmon.isEmpty()) {
            return addPalmonById(Localization.getMessage("team.assembler.no.palmon.exists"), dataSource);
        }

        Palmon selectedPalmon = optionalPalmon.get();
        if (palmons.contains(selectedPalmon)) {
            return addPalmonById(Localization.getMessage("team.assembler.already.in.team"), dataSource);
        }

        add(selectedPalmon);

        System.out.println(Localization.getMessage("team.assembler.added", selectedPalmon.name));
        ExecutionPause.sleep(1);
        return selectedPalmon;
    }
}
