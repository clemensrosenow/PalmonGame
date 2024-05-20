package entities;

/**
 * Represents a move that a Palmon can perform in a battle.
 */
public class Move {
    final static int medianUnlockLevel = 15;
    public final int id;
    public final String name;
    public final int damage;
    private int availableUsages;
    public final float accuracy;
    public final String type;

    /**
     * Constructs a Move with the specified attributes.
     *
     * @param id           the unique identifier for the move
     * @param name         the name of the move
     * @param damage       the damage dealt by the move
     * @param maxUsages    the maximum number of times the move can be used
     * @param accuracy     the accuracy percentage of the move
     * @param type         the type of the move
     */
    public Move(int id, String name, int damage, int maxUsages, float accuracy, String type) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.availableUsages = maxUsages;
        this.accuracy = accuracy;
        this.type = type;
    }

    /**
     * Decreases the available usages of the move by one.
     */
    public void use() {
        availableUsages--;
    }

    /**
     * Checks if the move can still be used.
     *
     * @return true if the move has remaining usages, false otherwise
     */
    public boolean isAvailable() {
        return availableUsages > 0;
    }

    /**
     * Determines if the move hits based on its accuracy.
     *
     * @return true if the move hits, false otherwise
     */
    public boolean hits() {
        return accuracy >= Math.random();
    }
}

