
import java.util.*;


public class Team {
    HashSet<Palmon> palmons;
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
        palmons.add(palmon);
        //TODO: Call Move Assigment for Palmon
        return true;
    }



    public void assembleRandomly() {
        Random random = new Random();
        for (int i = 0; i < palmonCount; i++) {
            Palmon randomPalmon;
            do {
                int randomIndex = random.nextInt(Data.palmons.size());
                randomPalmon = Data.palmons.get(randomIndex);
            } while (!add(randomPalmon));
            System.out.println(randomPalmon.name + " has been added to your team.");
        }
    }

    public void assembleByType() {
        //Setup types and according list of palmons
        HashMap<String, ArrayList<Palmon>> palmonTypes = new HashMap<>();
        for (Palmon palmon : Data.palmons) {
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
                System.out.println("Palmon: " + palmon.name);
            }
        }

        for (int i = 0; i < palmonCount; i++) {
            String selectedType = Input.select("Which Palmon type do you want? ", palmonTypes.keySet());
            System.out.println("Palmons of type " + selectedType);
            //TODO: Display data table with subareas for types using System.out.printf and Unicode characters
            palmonTypes.get(selectedType).forEach(palmon -> System.out.println("- " + palmon.id + ": " + palmon.name));
            //TODO: Change data source -> addPalmonById uses all palmons, subArrayList should be used
            Palmon validPalmon = addPalmonById("Which Palmon do you want in your team? Enter its ID: ");
            System.out.println(validPalmon.name + "has been added to your team.");
        }
    }


    public void assembleById() {
        System.out.println("Now select your favorite Palmons by their ID!");
        Data.palmons.forEach(palmon -> System.out.println("- " + palmon.id + ": " + palmon.name));
        for (int i = 0; i < palmonCount; i++) {
            Palmon validPalmon = addPalmonById("Which Palmon do you want in your team? Enter its ID: ");
            System.out.println(validPalmon.name + "has been added to your team.");
        }
    }

    private Palmon addPalmonById(String prompt) {
        int selectedId = Input.number(prompt,1,10194);
        Optional<Palmon> optionalPalmon = Data.palmons.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if(optionalPalmon.isEmpty()) {
            return addPalmonById("No Palmon exists for this ID. Enter a different one: ");
        }

        if(!add(optionalPalmon.get())) {
            return addPalmonById("The selected Palmon is already in your team. Please select a different one: ");
        }

        return optionalPalmon.get();
    }

        //Use selection Input to display all palmons with id
        //Get multiple ids, seperated with Enter
        //Check that id is valid and palmon not already in Team
}