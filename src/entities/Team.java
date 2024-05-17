package entities;

import utils.TableOutput;
import utils.UserInput;
import utils.CSVProcessing;
import resources.Constants;

import java.util.*;

public class Team {
    public enum AssembleMethod {
        random, id, type;
    }

    final public static AssembleMethod defaultAssembleMethod = Team.AssembleMethod.random;

    public void assemble(AssembleMethod method) {
        palmons = new HashSet<>(); //Reset previous team on restart
        switch (method) {
            case random:
                assembleRandomly();
                break;
            case id:
                assembleById();
                break;
            case type:
                assembleByType();
        }
        printTable(new ArrayList<>(palmons));
        palmonIterator = palmons.iterator();
    }

    private HashSet<Palmon> palmons;
    public Iterator<Palmon> palmonIterator;

    int totalPalmonCount;
    int minLevel;
    int maxLevel;

    public Team(int palmonCount, int minLevel, int maxLevel) {
        this.palmons = new HashSet<>();
        this.totalPalmonCount = palmonCount;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    boolean palmonExistsInTeam(Palmon palmon) {
        return palmons.contains(palmon);
    }

    boolean assembleIsUncompleted()
    {
        return palmons.size() < totalPalmonCount;
    }

    void add(Palmon palmon) {
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.setMoves();
        palmons.add(palmon);
    }

    public Palmon getNextPalmon() {
        return palmonIterator.hasNext() ? palmonIterator.next() : null;
    }

    private void assembleRandomly() {
        Random random = new Random();
        int range = CSVProcessing.palmons.size();
        while(assembleIsUncompleted()) {
            Palmon randomPalmon = CSVProcessing.palmons.get(random.nextInt(range));
            if (!palmonExistsInTeam(randomPalmon)) {
                add(randomPalmon);
            }
        }
    }

    private void assembleByType() {
        //Group palmons by type in a HashMap
        HashMap<String, ArrayList<Palmon>> palmonTypes = new HashMap<>();
        for (Palmon palmon : CSVProcessing.palmons) {
            for (String type : palmon.types) {
                palmonTypes.putIfAbsent(type,new ArrayList<>());
                palmonTypes.get(type).add(palmon);
            }
        }

        //Output selectable types
        System.out.println("Now select your favorite Palmons by their " + AssembleMethod.type.name() + "!");
        Set<String> availableTypes = palmonTypes.keySet();

        while(assembleIsUncompleted()){
            String selectedType = UserInput.select("Which Palmon type do you want? ", availableTypes);
            ArrayList<Palmon> availablePalmons = palmonTypes.get(selectedType);
            printTable(availablePalmons);
            addPalmonById("Which Palmon do you want in your team? Enter its ID: ", availablePalmons);
        }
    }

    private void assembleById() {
        System.out.println("Now select your favorite Palmons by their " + AssembleMethod.id.name() + "!");

        printTable(CSVProcessing.palmons);

        while (assembleIsUncompleted()) {
            addPalmonById("Which Palmon do you want in your team? Enter its ID: ", CSVProcessing.palmons);
        }
    }

    private Palmon addPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        int selectedId = UserInput.number(prompt,1,Constants.maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if(optionalPalmon.isEmpty()) {
            return addPalmonById("No Palmon exists for this ID. Enter a different one: ", dataSource);
        }

        Palmon selectedPalmon = optionalPalmon.get();
        if(palmonExistsInTeam(selectedPalmon)) {
            return addPalmonById("The selected Palmon is already in your team. Please select a different one: ", dataSource);
        }

        add(selectedPalmon);
        System.out.println(selectedPalmon.name + " has been added to your team.");
        return selectedPalmon;
    }

    void printTable(ArrayList<Palmon> dataSource) {
        ArrayList<TableOutput.Column> columns = new ArrayList<>();
        columns.add(new TableOutput.Column("id", 5, TableOutput.Column.Formatting.digit, dataSource.stream().map(palmon -> palmon.id).toArray()));
        columns.add(new TableOutput.Column("name", 26, TableOutput.Column.Formatting.string, dataSource.stream().map(palmon -> palmon.name).toArray()));
        columns.add(new TableOutput.Column("types", 20, TableOutput.Column.Formatting.string, dataSource.stream().map(palmon -> palmon.types[0] + (palmon.types[1].isEmpty()? "" : ", " + palmon.types[1])).toArray()));
        columns.add(new TableOutput.Column("hp", 3, TableOutput.Column.Formatting.digit, dataSource.stream().map(palmon -> palmon.hp).toArray()));
        columns.add(new TableOutput.Column("attack", 6, TableOutput.Column.Formatting.digit, dataSource.stream().map(palmon -> palmon.attack).toArray()));
        columns.add(new TableOutput.Column("defense", 7, TableOutput.Column.Formatting.digit, dataSource.stream().map(palmon -> palmon.defense).toArray()));
        columns.add(new TableOutput.Column("speed", 5, TableOutput.Column.Formatting.digit, dataSource.stream().map(palmon -> palmon.speed).toArray()));

        new TableOutput(new ArrayList<>(columns)).print();
    }
}