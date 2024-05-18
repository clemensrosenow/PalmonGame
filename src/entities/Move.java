package entities;
import java.util.Random;

public class Move {
    public final static int medianUnlockLevel = 15;
    public final int id;
    final String name;
    final int damage;
    public int availableUsages;
    final int accuracy;
    final String type;

    public Move(int id, String name, int damage, int maxUsages, int accuracy, String type) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.availableUsages = maxUsages;
        this.accuracy = accuracy;
        this.type = type;
    }

    public void use() {
        availableUsages--;
    }
    public boolean isAvailable() {
        return availableUsages > 0;
    }

    public boolean hits() {
        return accuracy >= new Random().nextInt(100);
    }
}

