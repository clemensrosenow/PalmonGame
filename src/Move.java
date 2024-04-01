import java.util.Random;

public class Move {
    final int id;
    final String name;
    final int damage;
    private int maxUsages;
    private int usages = 0;
    final int accuracy;
    private final String type;

    public Move(int id, String name, int damage, int maxUsages, int accuracy, String type) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.maxUsages = maxUsages;
        this.accuracy = accuracy;
        this.type = type;
    }

    public void useMove() {
        if (usages < maxUsages) {
            usages++;
        } else {
            System.out.println("Not usable");
        }
    }

    public boolean hits() {
        return accuracy >= new Random().nextInt(100);
    }
}

