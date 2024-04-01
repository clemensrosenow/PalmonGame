import java.util.Iterator;
import java.util.Map;

public class Round {
    Character winner;
    Character loser;
    Character player;
    Character opponent;

    public Round(Character player, Character opponent) {
        this.player = player;
        this.opponent = opponent;
    }

    public void assembleTeams() {
        // Palmon Count Selection
        final int totalPalmons = 1092; //Checked in Palmon CSV
        int playerPalmonCount = Input.number("How many Palmons do you want to have in your team? ", 1, totalPalmons);
        int opponentPalmonCount = Input.number("How many Palmons does your opponent have in his team? ", 1, totalPalmons);

        // Assemble Method Selection
        Map<String, String> assembleMethods = Map.of("random", "randomly (default)", "id", "by ID", "type", "by Type");
        String assembleMethod = Input.select("How do you want to select your Palmons?", assembleMethods);

        // Level Range Selection
        final int totalLevels = 100; //Based on max value of all rows in palmon_move.csv
        int minLevel = 0;
        int maxLevel = totalLevels;

        if (Input.confirm("Do you want to set a level range for your Palmons?")) {
            minLevel = Input.number("What's the lowest possible level of your Palmons? ", 0, totalLevels);
            maxLevel = Input.number("What's the highest possible level of your Palmons? ", minLevel, totalLevels);
        }


        // Setup
        player.team = new Team(playerPalmonCount, minLevel, maxLevel);
        switch (assembleMethod) {
            case "id":
                player.team.assembleById();
                break;
            case "type":
                player.team.assembleByType();
                break;
            case "random":
            default:
                player.team.assembleRandomly();
        }

        // Opponent team always consists of random palmons in full level range
        opponent.team = new Team(opponentPalmonCount, 0, totalLevels);
        opponent.team.assembleRandomly();
    }

    public void fight() {
        Iterator<Palmon> playerPalmons = player.team.palmons.iterator();
        Iterator<Palmon> opponentPalmons = opponent.team.palmons.iterator();
        Palmon playerPalmon = getNextPalmon(playerPalmons);
        Palmon opponentPalmon = getNextPalmon(opponentPalmons);

        while (playerPalmon != null && opponentPalmon != null) {
            if (playerPalmon.speed >= opponentPalmon.speed) {
                opponentPalmon = attackSequence(playerPalmon, opponentPalmon, playerPalmons, playerPalmon.selectAttack(), opponentPalmon.getRandomAttack());
                if (playerPalmon.isDefeated()) {
                    playerPalmon = getNextPalmon(playerPalmons);
                }
            } else {
                playerPalmon = attackSequence(opponentPalmon, playerPalmon, opponentPalmons, opponentPalmon.getRandomAttack(), playerPalmon.selectAttack());
                if (opponentPalmon.isDefeated()) {
                    opponentPalmon = getNextPalmon(opponentPalmons);
                }
            }
        }

        determineResult(playerPalmon);
    }

    private Palmon attackSequence(Palmon attacker, Palmon defender, Iterator<Palmon> defenderIterator, Move attackMove, Move defendMove) {
        attacker.performAttack(defender, attackMove);
        if (defender.isDefeated()) {
            return getNextPalmon(defenderIterator);
        }
        defender.performAttack(attacker, defendMove);
        return defender;
    }


    private static Palmon getNextPalmon(Iterator<Palmon> palmonIterator) {
        return palmonIterator.hasNext() ? palmonIterator.next() : null;
    }

    private void determineResult(Palmon playerPalmon) {
        winner = playerPalmon == null ? opponent : player;
        loser = playerPalmon == null ? player : opponent;
    }
}


