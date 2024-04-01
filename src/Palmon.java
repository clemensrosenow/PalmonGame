import java.util.Random;

public class Palmon {
    public final int id;
    public final String name;
    private final int height;
    private final int weight;
    public final String[] types = new String[2];
    int hp;
    private final int attack;
    private final int defense;
    final int speed;
    private int level = 100;
    public static final int maxMoves = 4;
    private Move[] moves = new Move[maxMoves]; //Todo: Zu ArrayList für responsive Größe

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
        Data.palmonMoves.get(id).forEach((learnedOnLevel, moveId) -> {
            if (learnedOnLevel >= level) {
                Move move = Data.getMoveById(moveId);
                if (move != null) {
                    possibleMoves.insert(moveId, move.damage);
                }
            }
        });

        //get top 4 damage moves using delete method
        for (int i = 0; i < maxMoves; i++) {
            Integer maxDamageMoveId = possibleMoves.delete();
            if (maxDamageMoveId != null) {
                moves[i] = Data.getMoveById(maxDamageMoveId);
            }
        }
    }

    // Checks if the Palmon is combat-ready based on its HP
    public boolean isDefeated() {
        return hp <= 0;
    }

    public Move selectAttack() {
        System.out.println("Select your attacking move!");
        for (int i = 1; i <= moves.length; i++) {
            System.out.println(i + ": " + moves[i].name);
        }
        return moves[Input.number("Number", 1, maxMoves) - 1]; //Todo: Is upper bound inclusive?
    }

    public Move getRandomAttack() {
        return moves[new Random().nextInt(maxMoves)];
    }
    public void performAttack(Palmon victim, Move attack) {
        if (!attack.hits()) {
            System.out.println("Attack hasn't hit the enemy.");
            return;
        }

        int healthDecrease = this.attack - victim.defense + attack.damage;
        float effectivity = Data.effectivity.get(types[0]).get(victim.types[0]);

        victim.hp -= (int) (healthDecrease * effectivity);
    }

    public void setRandomLevel(int min, int max) {
        level = new Random().nextInt(max - min) + min;
    }
}


