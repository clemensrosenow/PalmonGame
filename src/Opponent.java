//Implemented using Singleton Pattern for single creation with early initialization
public class Opponent extends Character {
        private static final Opponent instance = new Opponent();

        // Prevent instantiation from outside the class
        private Opponent() {}

        // Public method to provide access to the instance
        public static Opponent getInstance() {
            return instance;
        }

}
