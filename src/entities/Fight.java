package entities;

import interfaces.TeamAssembly;
import utilities.UserInput;

import java.util.ArrayList;

public class Fight {
    Player user;
    Player opponent;
    Player winner;
    Player loser;

    public Fight(Player user, Player opponent) {
        this.user = user;
        this.opponent = opponent;
    }

    public void assembleTeams() {
        assembleUserTeam();
        assembleOpponentTeam();
    }

    private void assembleUserTeam() {
        int playerPalmonCount = UserInput.number("How many Palmons do you want to have in your team? ", 1, Palmon.totalCount);
        final int minimumRange = Palmon.highestLevelPossible - Move.medianUnlockLevel;
        int minLevel = Palmon.lowestLevelPossible;
        int maxLevel = Palmon.highestLevelPossible;

        // Optional Level Range Selection
        if (UserInput.confirm("Do you want to set a level range for your Palmons?")) {
            minLevel = UserInput.number("Lowest possible level: ", Palmon.lowestLevelPossible, Palmon.highestLevelPossible - minimumRange);
            maxLevel = UserInput.number("Highest possible level: ", minLevel + minimumRange, Palmon.highestLevelPossible);
        }

        // Player Team Setup
        user.team = new Team(playerPalmonCount, minLevel, maxLevel);
        TeamAssembly.Method assemblyMethod = TeamAssembly.Method.valueOf(UserInput.select("By which attribute do you want to select your Palmons?", TeamAssembly.Method.values()));
        switch (assemblyMethod) {
            case id:
                user.team.assembleById();
                break;
            case type:
                user.team.assembleByType();
                break;
            case random:
            default:
                user.team.assembleRandomly();
        }
        System.out.println("\nYour team consists of the following Palmons:");
        user.team.printTable(new ArrayList<>(user.team.palmons));
    }

    private void assembleOpponentTeam() {
        int opponentPalmonCount = UserInput.number("How many Palmons does your opponent have in his team? ", 1, Palmon.totalCount);
        opponent.team = new Team(opponentPalmonCount, Palmon.lowestLevelPossible, Palmon.highestLevelPossible); //opponent team always has full level range
        opponent.team.assembleRandomly(); //opponent team is always assembled randomly
    }

    public void battle() {
        if (user.team.fightingPalmon == null) {
            setResult(opponent, user);
            return;
        }
        if (opponent.team.fightingPalmon == null) {
            setResult(user, opponent);
            return;
        }

        if (user.team.fightingPalmon.speed >= opponent.team.fightingPalmon.speed) { //User preferred in case of equal speed
            if (battleRound(user.team, opponent.team, false)) {
                battle(); // Continue with next round, if opponent's Palmon got defeated
            }
            battleRound(opponent.team, user.team, true);
        } else {
            if (battleRound(opponent.team, user.team, true)) {
                battle(); // Continue with next round, if user's Palmon got defeated
            }
            battleRound(user.team, opponent.team, false);
        }

    }

    //Returns status if defending Palmon got defeated
    private boolean battleRound(Team attackingTeam, Team defendingTeam, boolean isRandom) {
        attackingTeam.fightingPalmon.performAttack(defendingTeam.fightingPalmon, isRandom);
        if (defendingTeam.fightingPalmon.isDefeated()) {
            defendingTeam.swapFightingPalmon();
            return true;
        }
        return false;
    }

    private void setResult(Player winner, Player loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public void printResult() {
        System.out.println(winner.name + " has won.");
        System.out.println(loser.name + " has lost.");
    }
}
