package entities;

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
        switch (method) {
            case random:
                assembleRandomly(CSVProcessing.palmons.size());
                break;
            case id:
                assembleById();
                break;
            case type:
                assembleByType();
        }
        palmonIterator = palmons.iterator();
    }

    private HashSet<Palmon> palmons;
    public Iterator<Palmon> palmonIterator;

    int  palmonCount;
    int minLevel;
    int maxLevel;

    public Team(int palmonCount, int minLevel, int maxLevel) {
        this.palmons = new HashSet<>();
        this.palmonCount = palmonCount;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    boolean add(Palmon palmon) {
        if (palmons.contains(palmon)) return false;
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.setMoves();
        palmons.add(palmon);
        return true;
    }

    public Palmon getNextPalmon() {
        return palmonIterator.hasNext() ? palmonIterator.next() : null;
    }

    public void assembleRandomly(int range) {
        Random random = new Random();
        for (int i = 0; i < palmonCount; i++) {
            Palmon randomPalmon;
            do {
                int randomIndex = random.nextInt(range);
                randomPalmon = CSVProcessing.palmons.get(randomIndex);
            } while (!add(randomPalmon));
            System.out.println(randomPalmon.name + " has been added to your team.");
        }
    }

    private void assembleByType() {
        //Setup types and according list of palmons
        HashMap<String, ArrayList<Palmon>> palmonTypes = new HashMap<>();
        for (Palmon palmon : CSVProcessing.palmons) {
            for (String type : palmon.types) {
                palmonTypes.putIfAbsent(type,new ArrayList<>());
                palmonTypes.get(type).add(palmon);
            }
        }

        System.out.println("Now select your favorite Palmons by their " + AssembleMethod.type.name() + "!");

        // Output the data
        for(Map.Entry<String, ArrayList<Palmon>> entry : palmonTypes.entrySet()) {
            System.out.println("\nType: " + entry.getKey());
            for (Palmon palmon : entry.getValue()) {
                System.out.println("entities.Palmon: " + palmon.name);
            }
        }

        for (int i = 0; i < palmonCount; i++) {
            String selectedType = UserInput.select("Which Palmon type do you want? ", palmonTypes.keySet());
            System.out.println("Palmons of type " + selectedType);
            //printTable(palmonTypes.get(selectedType));
            Palmon validPalmon = getPalmonById("Which entities.Palmon do you want in your team? Enter its ID: ", palmonTypes.get(selectedType));
            if (add(validPalmon)) System.out.println(validPalmon.name + " has been added to your team.");
        }
    }


    private void assembleById() {
        System.out.println("Now select your favorite Palmons by their " + AssembleMethod.id.name() + "!");
        //Todo: Create table utility
        //printTable(CSVProcessing.palmons);
        for (int i = 0; i < palmonCount; i++) {
            Palmon validPalmon = getPalmonById("Which Palmon do you want in your team? Enter its ID: ", CSVProcessing.palmons);
            if(add(validPalmon)) System.out.println(validPalmon.name + " has been added to your team.");
        }
    }

    private Palmon getPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        int selectedId = UserInput.number(prompt,1,Constants.maxPalmonId);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if(optionalPalmon.isEmpty()) {
            return getPalmonById("No entities.Palmon exists for this ID. Enter a different one: ", dataSource);
        }

        if(!add(optionalPalmon.get())) {
            return getPalmonById("The selected entities.Palmon is already in your team. Please select a different one: ", dataSource);
        }

        return optionalPalmon.get();
    }

        //Use selection utilities.Input to display all palmons with id
        //Get multiple ids, seperated with Enter
        //Check that id is valid and palmon not already in Team
}