package entities;

import utils.CSVProcessing;
import utils.MaxHeap;
import utils.UserInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

public class Palmon {
    public static final int maxMoves = 4;
    public final int id;
    public final String name;
    public final String[] types = new String[2];
    public final int speed;
    private final int attack;
    private final int defense;
    private final HashSet<Move> moves = new HashSet<>();
    int hp;
    private int level = 100;

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
    }


    public void setMoves() {
        //insert all possible moves filtered by level into max heap data structure
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

    public Move selectAttack() {
        System.out.println("Select your attacking move!");
        //HashMap<String, String> options = new HashMap<>();
        //ArrayList<Move> availableMoves = getAvailableMoves();
        //availableMoves.forEach(move -> options.put(String.valueOf(move.id), move.name));
        //int selectedMoveId = Integer.parseInt(UserInput.select("Select your attacking move!", options));
        //Todo Print table and accept name Input (or Id)
        //int selectedMoveId = Integer.parseInt((UserInput.select("Select your attacking move!", moves)));
        //return moves.stream().filter(move -> move.id == selectedMoveId).findFirst().get();
        return moves.stream().filter(move -> move.id == 1).findFirst().get(); //Todo: Remove and fix
    }

    public Move getRandomAttack() {
        ArrayList<Move> availableMoves = getAvailableMoves();
        return availableMoves.get(new Random().nextInt(availableMoves.size()));
    }

    private ArrayList<Move> getAvailableMoves() {
        //Filters all moves by their availability
        return moves.stream().filter(move -> move.isAvailable()).collect(Collectors.toCollection(ArrayList::new));
    }

    public void performAttack(Palmon victim, Move attack) {
        System.out.println(this.name + "starts attacking.");
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


