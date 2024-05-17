package entities;

import resources.Constants;
import utils.CSVProcessing;
import utils.MaxHeap;
import utils.TableOutput;
import utils.UserInput;

import java.util.*;
import java.util.stream.Collectors;

public class Palmon {
    public static final int maxMoves = 4;
    public final int id;
    public final String name;
    public final String[] types = new String[2];
    public final int speed;
    public final int attack;
    public final int defense;
    private final HashSet<Move> moves = new HashSet<>();
    public int hp;
    int level = 100;

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
        System.out.println("\nSelect your attacking move!");

        ArrayList<Move> availableMoves = getAvailableMoves();

        ArrayList< TableOutput.Column> columns = new ArrayList<>();
        columns.add(new TableOutput.Column("id", 5, TableOutput.Column.Formatting.digit, availableMoves.stream().map(move -> move.id).toArray()));
        columns.add(new TableOutput.Column("name", 20, TableOutput.Column.Formatting.string, availableMoves.stream().map(move -> move.name).toArray()));
        columns.add(new TableOutput.Column("damage", 6, TableOutput.Column.Formatting.digit, availableMoves.stream().map(move -> move.damage).toArray()));
        columns.add(new TableOutput.Column("accuracy", 8, TableOutput.Column.Formatting.digit, availableMoves.stream().map(move -> move.accuracy).toArray()));
        columns.add(new TableOutput.Column("type", 10, TableOutput.Column.Formatting.string, availableMoves.stream().map(move -> move.type).toArray()));
        new TableOutput(columns).print();

        return selectMoveById("Enter the ID of the move you want to use: ", availableMoves);
    }

    private Move selectMoveById(String prompt, ArrayList<Move> dataSource) {
        int selectedId = UserInput.number(prompt,1, Constants.maxMoveId);

        Optional<Move> optionalMove = dataSource.stream().filter(move -> move.id == selectedId).findFirst();

        return optionalMove.orElseGet(() -> selectMoveById("No Move exists for this ID. Enter a different one: ", dataSource));

    }

    public Move getRandomAttack() {
        Random random = new Random();
        ArrayList<Move> availableMoves = getAvailableMoves();
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }

    private ArrayList<Move> getAvailableMoves() {
        //Filters all moves by their availability
        return moves.stream().filter(Move::isAvailable).collect(Collectors.toCollection(ArrayList::new));
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


