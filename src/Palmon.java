import java.util.Random;

public class Palmon {
    public final int id;
    public final String name;
    public final int height;
    public final int weight;
    public final String[] types = new String[2];
    private int hp;
    private final int attack;
    private final int defense;
    private final int speed;
    private int level = 0;
    private Move[] moves = new Move[4];

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

    // Simulates the Palmon performing an attack
    public void performAttack(Palmon palmon, Move move, double effectivity) {
        // Implementation can be fleshed out based on game logic
        //System.out.println(this.name + " is performing " + move.getName() + " on " + palmon.getName() + " with effectivity " + effectivity);
    }

    // Assigns moves to the Palmon based on its ID and level
    public void assignMoves(int palmonID, int level) {

        System.out.println("Assigning moves to Palmon ID " + palmonID + " at level " + level);
    }

    // Checks if the Palmon is combat-ready based on its HP
    public boolean isCombatReady() {
        return hp > 0;
    }


    public void setRandomLevel(int min, int max) {
        level = new Random().nextInt(max - min) + min;
    }
}


