package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.TableOutput;
import utilities.UserInput;
import utilities.MaxHeap;
import utilities.Localization;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a Pokémon, which is a combat creature with various attributes and abilities.
 */
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

    /**
     * Constructs a Palmon with the specified attributes.
     *
     * @param id       the unique identifier for the Palmon
     * @param name     the name of the Palmon
     * @param height   the height of the Palmon (not used)
     * @param weight   the weight of the Palmon (not used)
     * @param type1    the primary type of the Palmon
     * @param type2    the secondary type of the Palmon
     * @param hp       the hit points of the Palmon
     * @param attack   the attack stat of the Palmon
     * @param defense  the defense stat of the Palmon
     * @param speed    the speed stat of the Palmon
     */
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

    /**
     * Assigns the highest damage moves to the Palmon based on its level.
     *
     * @see MaxHeap for custom Node data structure implementation
     */
    public void assignHighestDamageMoves() {
        // Get all possible moves of the Palmon
        HashMap<Integer, Integer> palmonMoves = DB.getPalmonMoves(id);

        // Insert all possible moves into a MaxHeap
        MaxHeap maxHeap = new MaxHeap();
        palmonMoves.forEach((unlockLevel, moveId) -> {
            // Filter if the move is unlocked at the Palmon level
            if (level >= unlockLevel) {
                // Only insert if move exists in the database
                Optional<Move> optionalMove = DB.getMoveById(moveId);
                optionalMove.ifPresent(move -> maxHeap.insert(move.id, move.damage));
            }
        });

        // Add the 4 highest-damage moves using the Max Heap delete method
        for (int i = 0; i < maxMoves; i++) {
            Integer maxDamageMoveId = maxHeap.delete(); //Delete always returns the maximum value (tree root)
            if (maxDamageMoveId == null) break; //Cancel prematurely if no more moves are available
            DB.getMoveById(maxDamageMoveId).ifPresent(moves::add); //Moves are always present because of the previous filtering
        }
    }

    /**
     * Checks if the Palmon is defeated based on its HP.
     *
     * @return true if the Palmon has no HP left, false otherwise
     */
    public boolean isDefeated() {
        return hp <= 0;
    }

    /**
     * Selects an attack move from a list of available moves.
     *
     * @param availableMoves the list of available moves
     * @param randomSelection whether to select the move randomly
     * @return the selected move
     */
    private Move selectAttack(ArrayList<Move> availableMoves, boolean randomSelection) {
        // Random selection for opponent
        if(randomSelection) {
            Random random = new Random();
            int randomIndex = random.nextInt(availableMoves.size());
            return availableMoves.get(randomIndex);
        }

        // Display table of available moves that user can select from
        TableOutput.printMoveTable(availableMoves);
        return selectMoveById(Localization.getMessage("palmon.select.move"), availableMoves);
    }

    /**
     * Prompts the user to select a move by its ID from the available moves.
     *
     * @param prompt the prompt message for the user
     * @param dataSource the data source containing the moves to choose from
     * @return the selected move
     */
    private static Move selectMoveById(String prompt, ArrayList<Move> dataSource) {
        final int maxMoveId = 10008; // Derived from CSV file
        int selectedId = UserInput.number(prompt, 1, maxMoveId);

        Optional<Move> selectedMove = DB.getMoveById(selectedId);
        // Error handling if move is not existing or unavailable in dataSource
        if (selectedMove.isEmpty() || !dataSource.contains(selectedMove.get())) {
            return selectMoveById(Localization.getMessage("palmon.select.move.available"), dataSource);
        }

        return selectedMove.get();
    }

    /**
     * Gets the list of available moves for the Palmon.
     *
     * @return the list of available moves
     */
    private ArrayList<Move> getAvailableMoves() {
        return moves.stream().filter(Move::isAvailable).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Performs an attack on another Palmon.
     *
     * @param victim the Palmon being attacked
     * @param randomMoveSelection whether opponent is attacking and move is selected randomly
     *
     * @see Palmon#selectAttack(ArrayList, boolean) for move selection
     */
    public void performAttack(Palmon victim, boolean randomMoveSelection) {
        ArrayList<Move> availableMoves = getAvailableMoves();

        // Early return if no moves are available
        if (availableMoves.isEmpty()) {
            System.out.println(Localization.getMessage("palmon.no.moves.available", this.name));
            return;
        }

        // Display call to action to user
        if (!randomMoveSelection) System.out.println(Localization.getMessage("palmon.select.attack", this.name, victim.name));

        // Select and use the attack
        Move attack = selectAttack(availableMoves, randomMoveSelection);
        attack.use();

        // Early return if attack missed based on accuracy
        if (!attack.hits()) {
            System.out.println(Localization.getMessage("palmon.attack.missed"));
            return;
        }

        // Calculate damage to victim
        int rawDamage = this.attack + attack.damage - victim.defense;
        float effectivity = DB.getEffectivity(types[0], victim.types[0]); // Use the primary type of attacker and victim to determine effectivity
        int effectiveDamage = (int) (rawDamage * effectivity); //Effectivity serves as a multiplier for the raw damage

        if (effectiveDamage <= 0) {
            // Defense is higher than total attack, so the attack is defended
            System.out.println(Localization.getMessage("palmon.attack.defended", victim.name, attack.name));
        } else {
            // Deal damage to the victim
            System.out.println(Localization.getMessage("palmon.attack.damage", effectiveDamage, attack.name));
            victim.hp -= effectiveDamage;
            // Display remaining HP of the victim or defeat message
            System.out.println(Localization.getMessage(victim.hp > 0 ? "palmon.hp.remaining" : "palmon.defeated", victim.name, victim.hp));
        }

        ExecutionPause.sleep(2);
    }

    /**
     * Sets the Palmon's level to a random value within the specified range.
     *
     * @param min the minimum level
     * @param max the maximum level
     */
    public void setRandomLevel(int min, int max) {
        Random random = new Random();
        level = random.nextInt(max - min) + min;
    }
}
