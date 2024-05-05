package entities;

import utils.Input;
import utils.CSVProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import utils.MaxHeap;

public class Palmon {
    public final int id;
    public final String name;
    private final int height;
    private final int weight;
    public final String[] types = new String[2];
    int hp;
    private final int attack;
    private final int defense;
    public final int speed;
    private int level = 100;
    public static final int maxMoves = 4;
    private final ArrayList<Move> moves = new ArrayList<>();

    public Palmon(int id, String name, int height, int weight, String type1, String type2,
                  int hp, int attack, int defense, int speed) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.types[0] = type1;
        this.types[1] = type2;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }


    public void setMoves() {
        //insert all possible moves filtered by level into max heap
        MaxHeap possibleMoves = new MaxHeap();
        CSVProcessing.palmonMoves.get(id).forEach((learnedOnLevel, moveId) -> {
            if (learnedOnLevel >= level) {
                Move move = CSVProcessing.getMoveById(moveId);
                if (move != null) {
                    possibleMoves.insert(moveId, move.damage);
                }
            }
        });

        //get top 4 damage moves using delete method
        for (int i = 0; i < maxMoves; i++) {
            Integer maxDamageMoveId = possibleMoves.delete();
            if (maxDamageMoveId != null) {
                moves.set(i, CSVProcessing.getMoveById(maxDamageMoveId));
            }
        }
    }

    // Checks if the entities.Palmon is combat-ready based on its HP
    public boolean isDefeated() {
        return hp <= 0;
    }

    public Move selectAttack() {
        System.out.println("Select your attacking move!");
        HashMap<String, String> options = new HashMap<>();
        moves.forEach(move -> options.put(String.valueOf(move.id), move.name));
        int selectedMoveId = Integer.parseInt(Input.select("Select your attacking move!", options));
        return moves.stream().filter(move -> move.id == selectedMoveId).findFirst().get();
    }

    public Move getRandomAttack() {
        return moves.get(new Random().nextInt(maxMoves));
    }
    public void performAttack(Palmon victim, Move attack) {
        if (!attack.hits()) {
            System.out.println("Attack hasn't hit the enemy.");
            return;
        }

        int healthDecrease = this.attack - victim.defense + attack.damage;
        float effectivity = CSVProcessing.effectivity.get(types[0]).get(victim.types[0]);

        victim.hp -= (int) (healthDecrease * effectivity);
    }

    public void setRandomLevel(int min, int max) {
        level = new Random().nextInt(max - min) + min;
    }
}


