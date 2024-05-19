package entities;

import utilities.CSVProcessing;
import utilities.MaxHeap;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.*;
import java.util.stream.Collectors;

public class Palmon {
    public final static int lowestLevelPossible = 0;
    public final static int highestLevelPossible = 100;
    public final static int totalCount = 1092;
    public static final int maxMoves = 4;
    public final int id;
    public final String name;
    public final String[] types = new String[2];
    public final int speed;
    public final int attack;
    public final int defense;
    private final HashSet<Move> moves = new HashSet<>();
    public int hp;
    int level;



    public Palmon(int id, String name, int height, int weight, String type1, String type2,
                  int hp, int attack, int defense, int speed) {
        this.id = id;
        this.name = name;
        this.types[0] = type1;
        this.types[1] = type2;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.level = highestLevelPossible;
    }


    public void setMoves() {
        //Insert all possible moves filtered by level into custom MaxHeap data structure
        MaxHeap possibleMoves = new MaxHeap();
        CSVProcessing.palmonMoves.get(id).forEach((learnedOnLevel, moveId) -> {
            //make insertion filtered by level
            if (learnedOnLevel >= level) {
                Move move = CSVProcessing.getMoveById(moveId);
                if (move != null) {
                    possibleMoves.insert(moveId, move.damage);
                }
            }
        });

        //get top 4 damage moves using Max Heap delete method
        for (int i = 0; i < maxMoves; i++) {
            Integer maxDamageMoveId = possibleMoves.delete();
            if (maxDamageMoveId == null) { break;}
            moves.add(CSVProcessing.getMoveById(maxDamageMoveId));
        }
    }

    // Checks if the Palmon is combat-ready based on its HP
    public boolean isDefeated() {
        return hp <= 0;
    }

    private Move selectAttack(boolean randomSelection) {
        if(randomSelection) {
            Random random = new Random();
            return availableMoves.get(random.nextInt(availableMoves.size()));
        }

        System.out.println("\nSelect your attacking move!");

        TableOutput.printMoveTable(availableMoves);

        return selectMoveById("Enter the ID of the move you want to use: ", availableMoves);
    }

    private Move selectMoveById(String prompt, ArrayList<Move> dataSource) {
        final int maxMoveId = 10008;
        int selectedId = UserInput.number(prompt,1, maxMoveId);

        Optional<Move> optionalMove = dataSource.stream().filter(move -> move.id == selectedId).findFirst();

        return optionalMove.orElseGet(() -> selectMoveById("No Move exists for this ID. Enter a different one: ", dataSource));
    }


    private ArrayList<Move> getAvailableMoves() {
        //Filters all moves by their availability
        return moves.stream().filter(Move::isAvailable).collect(Collectors.toCollection(ArrayList::new));
    }

    public void performAttack(Palmon victim, boolean randomMoveSelection) { //Todo: Merge with select attack because directly interconnected
        System.out.println(this.name + "starts attacking.");
        Move attack = selectAttack(randomMoveSelection);
        attack.use();

        if (!attack.hits()) {
            System.out.println("Attack has missed the enemy.");
            return;
        }

        int rawDamage = this.attack - victim.defense + attack.damage;
        float effectivity = CSVProcessing.effectivity.get(types[0]).get(victim.types[0]); //Use the primary type of attacker and victim to determine effectivity
        int effectiveDamage = (int) (rawDamage * effectivity);
        System.out.println(effectiveDamage + " damage points dealt with attack " + attack.name + ".");
        victim.hp -= effectiveDamage;
        System.out.println(victim.name + " has " + victim.hp + " hit points remaining.");
    }

    public void setRandomLevel(int min, int max) {
        level = new Random().nextInt(max - min) + min;
    }
}


