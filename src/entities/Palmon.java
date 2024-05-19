package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.TableOutput;
import utilities.UserInput;
import utilities.MaxHeap;

import java.util.*;
import java.util.stream.Collectors;

public class Palmon {
    public final static int lowestLevelPossible = 0;
    public final static int highestLevelPossible = 100;
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


    public void assignHighestDamageMoves() {
        //Insert all possible moves filtered by level into custom MaxHeap data structure
        MaxHeap maxHeap = new MaxHeap();
        HashMap<Integer, Integer> palmonMoves = DB.getPalmonMoves(id);
        palmonMoves.forEach((learnedOnLevel, moveId) -> {
            //make insertion filtered by level
            if (level >= learnedOnLevel) {
                Optional<Move> optionalMove = DB.getMoveById(moveId);
                optionalMove.ifPresent(move -> maxHeap.insert(move.id, move.damage)); //Check for existence of move before inserting into MaxHeap
            }
        });

        //get top 4 damage moves using Max Heap delete method
        for (int i = 0; i < maxMoves; i++) {
            Integer maxDamageMoveId = maxHeap.delete();
            if (maxDamageMoveId == null) break;
            DB.getMoveById(maxDamageMoveId).ifPresent(moves::add);
        }
    }

    // Checks if the Palmon is combat-ready based on its HP
    public boolean isDefeated() {
        return hp <= 0;
    }

    private Move selectAttack(ArrayList<Move> availableMoves, boolean randomSelection) {
        if(randomSelection) {
            Random random = new Random();
            int randomIndex = random.nextInt(availableMoves.size());
            return availableMoves.get(randomIndex);
        }

        TableOutput.printMoveTable(availableMoves);
        return selectMoveById("Enter the ID of the move you want to use: ", availableMoves);
    }


    private static Move selectMoveById(String prompt, ArrayList<Move> dataSource) {
        final int maxMoveId = 10008;
        int selectedId = UserInput.number(prompt,1, maxMoveId);

        Optional<Move> selectedMove = DB.getMoveById(selectedId);
        if (selectedMove.isEmpty() || !dataSource.contains(selectedMove.get())) {
            return selectMoveById("Select one of the available moves: ", dataSource);
        }

        return selectedMove.get();
    }

    private ArrayList<Move> getAvailableMoves() {
        //Filters all moves by their availability
        return moves.stream().filter(Move::isAvailable).collect(Collectors.toCollection(ArrayList::new));
    }

    public void performAttack(Palmon victim, boolean randomMoveSelection) {
        ArrayList<Move> availableMoves = getAvailableMoves();
        if (availableMoves.isEmpty()) {
            System.out.println("No moves available. " + this.name + " can not attack.");
            return;
        }
        if (!randomMoveSelection) System.out.println("Select " + this.name + "'s move to attack " + victim.name + "!");

        Move attack = selectAttack(availableMoves, randomMoveSelection);
        attack.use();

        if (!attack.hits()) {
            System.out.println("Attack has missed the enemy.");
            return;
        }

        int rawDamage = this.attack - victim.defense + attack.damage;
        float effectivity = DB.getEffectivity(types[0], victim.types[0]); //Use the primary type of attacker and victim to determine effectivity
        int effectiveDamage = (int) (rawDamage * effectivity);
        if (effectiveDamage <= 0) {
            System.out.println(victim.name + " defended himself against " +  attack.name + ". No damage dealt.");
        } else {
            System.out.println(effectiveDamage + " damage points dealt with attack " + attack.name + ".");
            victim.hp -= effectiveDamage;
            System.out.println(victim.name + (victim.hp > 0 ? " has " + victim.hp + " hit points remaining." : " has been defeated."));
        }

        ExecutionPause.sleep(2);
    }

    public void setRandomLevel(int min, int max) {
        Random random = new Random();
        level = random.nextInt(max - min) + min;
    }
}


