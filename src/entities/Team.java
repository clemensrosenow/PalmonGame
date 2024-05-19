package entities;

import interfaces.TeamAssembly;
import utilities.TableOutput;
import utilities.UserInput;
import utilities.CSVProcessing;

import java.util.*;

//Todo: Associate team with fight instead of player to allow for multiple fights, else reset team after each fight
public class Team implements TeamAssembly {
    private Iterator<Palmon> fightingQueue;
    public Palmon fightingPalmon;
    int palmonCount;
    int minLevel;
    int maxLevel;
    public final HashSet<Palmon> palmons;
    public Team(int palmonCount, int minLevel, int maxLevel) {
        this.palmons = new HashSet<>();
        this.palmonCount = palmonCount;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    private void setupFightingQueue() {
        //Reflects a queue of all team palmons after assembly
        fightingQueue = palmons.iterator();
        fightingPalmon = fightingQueue.next();
    }

    boolean palmonExistsInTeam(Palmon palmon) {
        return palmons.contains(palmon);
    }

    boolean assemblyUncompleted() {
        return palmons.size() < palmonCount;
    }

    void add(Palmon palmon) {
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.setMoves();
        palmons.add(palmon);
    }

    public void swapFightingPalmon() {
        fightingPalmon = fightingQueue.hasNext() ? fightingQueue.next() : null;
    }

    @Override
    public void assembleRandomly() {
        Random random = new Random();
        int range = CSVProcessing.palmons.size();
        while (assemblyUncompleted()) {
            Palmon randomPalmon = CSVProcessing.palmons.get(random.nextInt(range));
            if (!palmonExistsInTeam(randomPalmon)) {
                add(randomPalmon);
            }
        }
        setupFightingQueue();
    }

    @Override
    public void assembleByType() {
        //Group palmons by type in a HashMap
        HashMap<String, ArrayList<Palmon>> palmonTypes = new HashMap<>();
        for (Palmon palmon : CSVProcessing.palmons) {
            for (String type : palmon.types) {
                palmonTypes.putIfAbsent(type, new ArrayList<>());
                palmonTypes.get(type).add(palmon);
            }
        }
        palmonTypes.remove(""); //Absence of second type not considered a type

        //Output selectable types
        System.out.println("Now select your favorite Palmons by their " + AssembleMethod.type.name() + "!");
        ArrayList<String> availableTypes = new ArrayList<>(palmonTypes.keySet());
        Collections.sort(availableTypes); //Sort types in ascending alphabetical order

        while (assemblyUncompleted()) {
            String selectedType = UserInput.select("Which Palmon type do you want? ", availableTypes);
            ArrayList<Palmon> availablePalmons = palmonsGroupedByType.get(selectedType);
            TableOutput.printPalmonTable(availablePalmons);
            addPalmonById("Which " + selectedType + " Palmon do you want in your team? Enter its ID: ", availablePalmons);
        }
        setupFightingQueue();
    }

    @Override
    public void assembleById() {
        System.out.println("Now select your favorite Palmons by their " + AssembleMethod.id.name() + "!");

        ArrayList<Palmon> totalPalmons = DB.getPalmons();
        TableOutput.printPalmonTable(totalPalmons);

        while (assemblyUncompleted()) {
            addPalmonById("Which Palmon do you want in your team? Enter its ID: ", totalPalmons);
        }
        setupFightingQueue();
    }

    private Palmon addPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        final int maxPalmonId = 10194;
        int selectedId = UserInput.number(prompt, 1, maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if (optionalPalmon.isEmpty()) {
            return addPalmonById("No Palmon exists for this ID. Enter a different one: ", dataSource);
        }

        Palmon selectedPalmon = optionalPalmon.get();
        if (palmonExistsInTeam(selectedPalmon)) {
            return addPalmonById("The selected Palmon is already in your team. Please select a different one: ", dataSource);
        }

        add(selectedPalmon);
        System.out.println(selectedPalmon.name + " has been added to your team.");
        return selectedPalmon;
    }


    public enum AssembleMethod {
        random, id, type
    }
}