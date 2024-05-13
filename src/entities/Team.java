package entities;

import utils.Input;
import utils.CSVProcessing;

import java.util.*;

public class Team {
    public static assembleMethod assembleMethod;
    public enum assembleMethod {
        random("randomly (default)"), id("by ID"), type("by Type");
        final String name;
        assembleMethod(String name) {
            this.name = name;
        }
    }
    public void assemble() {
        assembleRandomly();
    }
    public void assemble(assembleMethod method) {
        switch (method) {
            case id:
                assembleById();
                break;
            case type:
                assembleByType();
                break;
            case random: default:
                assembleRandomly();
        }
    }

    public HashSet<Palmon> palmons;
    int palmonCount;
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



    private void assembleRandomly() {
        Random random = new Random();
        for (int i = 0; i < palmonCount; i++) {
            Palmon randomPalmon;
            do {
                int randomIndex = random.nextInt(CSVProcessing.palmons.size());
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

        System.out.println("Now select your favorite Palmons by their type!");

        // Output the data
        for(Map.Entry<String, ArrayList<Palmon>> entry : palmonTypes.entrySet()) {
            System.out.println("\nType: " + entry.getKey());
            for (Palmon palmon : entry.getValue()) {
                System.out.println("entities.Palmon: " + palmon.name);
            }
        }

        for (int i = 0; i < palmonCount; i++) {
            String selectedType = Input.select("Which entities.Palmon type do you want? ", palmonTypes.keySet());
            System.out.println("Palmons of type " + selectedType);
            printTable(palmonTypes.get(selectedType));
            Palmon validPalmon = getPalmonById("Which entities.Palmon do you want in your team? Enter its ID: ", palmonTypes.get(selectedType));
            if (add(validPalmon)) System.out.println(validPalmon.name + " has been added to your team.");
        }
    }


    private void assembleById() {
        System.out.println("Now select your favorite Palmons by their ID!");
        printTable(CSVProcessing.palmons);
        for (int i = 0; i < palmonCount; i++) {
            Palmon validPalmon = getPalmonById("Which entities.Palmon do you want in your team? Enter its ID: ", CSVProcessing.palmons);
            if(add(validPalmon)) System.out.println(validPalmon.name + " has been added to your team.");
        }
    }

    private Palmon getPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        int selectedId = Input.number(prompt,1,10194);
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

    private static void printTable(ArrayList<Palmon> rows) {
        //TODO: Adjust string length to 26 if space is available
        //TODO: Make more adaptive to column width and content
        System.out.printf("┌───────┬──────────────────────┐%n");
        System.out.printf("│ %5s │ %-20s │%n", "ID", "Name");

        for (Palmon entry: rows) {
            System.out.printf("├───────┼──────────────────────┤%n");
            System.out.printf("│ %5d │ %-20s │%n", entry.id, entry.name);
        }
        System.out.printf("└───────┴──────────────────────┘%n");
    }
    public static void main(String[] args) {

        System.out.printf("┌───────┬──────────────────────┐%n");
        System.out.printf("│ %5s │ %-20s │%n", "ID", "Name");


        System.out.printf("├───────┼──────────────────────┤%n");
        System.out.printf("│ %5d │ %-20s │%n", 4, "Palmon");

        System.out.printf("├───────┼──────────────────────┤%n");
        System.out.printf("│ %5d │ %-20s │%n", 103, "Crazy Attacker");

        System.out.printf("├───────┼──────────────────────┤%n");
        System.out.printf("│ %5d │ %-20s │%n", 10823, "Long Sick Palmon Name");

        System.out.printf("└───────┴──────────────────────┘%n");
    }
}