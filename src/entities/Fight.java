package entities;

import resources.Constants;
import utils.UserInput;

public class Fight {
    Player user;
    Player opponent;
    Palmon fightingPlayerPalmon;
    Palmon fightingOpponentPalmon;
    Player winner;
    Player loser;

    public Fight(Player user, Player opponent) {
        this.user = user;
        this.opponent = opponent;
    }

    public void assembleTeams() {
        // Palmon Count Selection
        int playerPalmonCount = UserInput.number("How many Palmons do you want to have in your team? ", 1, Constants.PalmonCount);
        int opponentPalmonCount = UserInput.number("How many Palmons does your opponent have in his team? ", 1, Constants.PalmonCount);

        // Player optional Level Range Selection
        int minLevel = 0;
        int maxLevel = Constants.totalLevels;
        if (UserInput.confirm("Do you want to set a level range for your Palmons?")) {
            minLevel = UserInput.number("Lowest possible level: ", 0, Constants.totalLevels);
            maxLevel = UserInput.number("Highest possible level: ", minLevel, Constants.totalLevels);
        }

        // Player Team Setup
        user.team = new Team(playerPalmonCount, minLevel, maxLevel);
        Team.AssembleMethod assembleMethod = Team.AssembleMethod.valueOf(UserInput.select("By which attribute do you want to select your Palmons?", Team.AssembleMethod.values(), Team.AssembleMethod.random.name()));
        user.team.assemble(assembleMethod);

        // Opponent team always consists of randomly selected palmons in full level range
        opponent.team = new Team(opponentPalmonCount, 0, Constants.totalLevels);
        opponent.team.assemble(Team.AssembleMethod.random);
    }

    public void battle() {
        fightingPlayerPalmon = user.team.getNextPalmon();
        fightingOpponentPalmon = opponent.team.getNextPalmon();

        while (fightingPlayerPalmon != null && fightingOpponentPalmon != null) {
            attackSequence();
        }
    }

    private void attackSequence() {
        // Speed determines which Palmon starts attacking
        if (fightingPlayerPalmon.speed >= fightingOpponentPalmon.speed) { //light advantage for player
            fightingPlayerPalmon.performAttack(fightingOpponentPalmon, fightingPlayerPalmon.selectAttack());
            if(fightingOpponentPalmon.isDefeated()) {
                fightingOpponentPalmon = opponent.team.getNextPalmon();
                return;
            }
            fightingOpponentPalmon.performAttack(fightingPlayerPalmon, fightingOpponentPalmon.getRandomAttack());
            if(fightingPlayerPalmon.isDefeated()) {
                fightingPlayerPalmon = user.team.getNextPalmon();
            }
        } else {
            fightingOpponentPalmon.performAttack(fightingPlayerPalmon, fightingOpponentPalmon.getRandomAttack());
            if(fightingPlayerPalmon.isDefeated()) {
                fightingPlayerPalmon = user.team.getNextPalmon();
                return;
            }
            fightingPlayerPalmon.performAttack(fightingOpponentPalmon, fightingPlayerPalmon.selectAttack());
            if(fightingOpponentPalmon.isDefeated()) {
                fightingOpponentPalmon = user.team.getNextPalmon();
            }
        }
    }


    public void determineResult() {
         winner = fightingOpponentPalmon == null ? user : opponent; //User wins when opponent has no remaining palmons
         loser = fightingPlayerPalmon == null ? user : opponent; //User loses when he has no remaining palmons

        System.out.println(winner.name + " has won.");
        System.out.println(loser.name + " has lost.");
    }
}
