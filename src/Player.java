//Singleton Pattern with early initialization
public class Player extends Character {
    private static final Player instance = new Player();
    private Player() {};
    public static Player getInstance() {
        return instance;
    }

}
