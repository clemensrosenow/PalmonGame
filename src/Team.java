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
        palmon.setRandomLevel(minLevel, maxLevel);
        palmon.setMoves();
        palmons.add(palmon);
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
            printTable(palmonTypes.get(selectedType));
            Palmon validPalmon = getPalmonById("Which Palmon do you want in your team? Enter its ID: ", palmonTypes.get(selectedType));
            System.out.println(validPalmon.name + "has been added to your team.");
        }
    }


    public void assembleById() {
        System.out.println("Now select your favorite Palmons by their ID!");
        printTable(Data.palmons);
        for (int i = 0; i < palmonCount; i++) {
            Palmon validPalmon = getPalmonById("Which Palmon do you want in your team? Enter its ID: ", Data.palmons);
            System.out.println(validPalmon.name + "has been added to your team.");
        }
    }

    private Palmon getPalmonById(String prompt, ArrayList<Palmon> dataSource) {
        int selectedId = Input.number(prompt,1,10194);
        Optional<Palmon> optionalPalmon = dataSource.stream().filter(palmon -> palmon.id == selectedId).findFirst();

        if(optionalPalmon.isEmpty()) {
            return getPalmonById("No Palmon exists for this ID. Enter a different one: ", dataSource);
        }

        if(!add(optionalPalmon.get())) {
            return getPalmonById("The selected Palmon is already in your team. Please select a different one: ", dataSource);
        }

        return optionalPalmon.get();
    }

        //Use selection Input to display all palmons with id
        //Get multiple ids, seperated with Enter
        //Check that id is valid and palmon not already in Team

    private static void printTable(ArrayList<Palmon> rows) {
        System.out.printf("%5s %-20s %n,", "ID", "Name");
        System.out.printf("%5s %-20s %n,", "-----", "----------------------");
        for (Palmon entry: rows) {
            System.out.printf("%5d %-20s", entry.id, entry.name);
        }
    }
}