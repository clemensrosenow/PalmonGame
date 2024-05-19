package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.ArrayList;
import java.util.Random;

public class Fight {
    Player user;
    Player opponent;

    public Fight(Player user, Player opponent) {
        this.user = user;
        this.opponent = opponent;
    }

    public void assembleTeams() {
        assembleUserTeam();
        assembleOpponentTeam();
    }

    private void assembleUserTeam() {
        int playerPalmonCount = UserInput.number("How many Palmons do you want to have in your team? ", 1, DB.totalPalmonCount());
        final int minimumRange = Palmon.highestLevelPossible - Move.medianUnlockLevel;
        int minLevel = Palmon.lowestLevelPossible;
        int maxLevel = Palmon.highestLevelPossible;

        // Optional Level Range Selection
        if (UserInput.confirm("Do you want to set a level range for your Palmons?")) {
            minLevel = UserInput.number("Lowest possible level: ", Palmon.lowestLevelPossible, Palmon.highestLevelPossible - minimumRange);
            maxLevel = UserInput.number("Highest possible level: ", minLevel + minimumRange, Palmon.highestLevelPossible);
        }

        // Player Team Setup
        TeamAssembler.Method assemblyMethod = TeamAssembler.Method.valueOf(UserInput.select("By which attribute do you want to select your Palmons?", TeamAssembler.Method.values()));
        user.team = new Team(playerPalmonCount, minLevel, maxLevel, assemblyMethod);
        System.out.println("\nYour team consists of the following Palmons:");
        TableOutput.printPalmonTable(new ArrayList<>(user.team.palmons));
        ExecutionPause.sleep(3);
    }

    private void assembleOpponentTeam() {
        Random random = new Random();
        int opponentPalmonCount = random.nextInt(user.team.palmons.size() * 2 - 1) + 1;
        if(UserInput.confirm("Do you want to set the number of Palmons for " + opponent.name + "?")) {
            opponentPalmonCount = UserInput.number("How many Palmons does your opponent have in his team? ", 1, DB.totalPalmonCount());
        }
        opponent.team = new Team(opponentPalmonCount, Palmon.lowestLevelPossible, Palmon.highestLevelPossible, TeamAssembler.Method.random); //opponent team always has full level range
        ExecutionPause.sleep(2);
        System.out.println(opponent.name + " is ready to crush you. Let the fight begin!");
        ExecutionPause.sleep(2);
    }

    public void battle() {
        while (!user.team.isDefeated() && !opponent.team.isDefeated()) {
            if(UserInput.confirm("Do you want to see the current battle status?")) {
                printBattleStatus();
            }

            if (user.team.fightingPalmon.speed >= opponent.team.fightingPalmon.speed) { //User preferred in case of equal speed
                if (battleRound(user.team, opponent.team, false)) {
                    continue; // Continue with next round, if opponent's Palmon got defeated
                }
                ExecutionPause.sleep(1);
                battleRound(opponent.team, user.team, true);
            } else {
                if (battleRound(opponent.team, user.team, true)) {
                    continue; // Continue with next round, if user's Palmon got defeated
                }
                ExecutionPause.sleep(1);
                battleRound(user.team, opponent.team, false);
            }
        }
    }

    //Returns status if defending Palmon got defeated
    private boolean battleRound(Team attackingTeam, Team defendingTeam, boolean isRandom) {
        System.out.println("\nIt's " + (isRandom ? opponent.name + "'s" : "your") + " turn with " + attackingTeam.fightingPalmon.name + ".");
        attackingTeam.fightingPalmon.performAttack(defendingTeam.fightingPalmon, isRandom);
        if (defendingTeam.fightingPalmon.isDefeated()) {
            defendingTeam.swapFightingPalmon();
            return true;
        }
        return false;
    }

    void printBattleStatus() {
        System.out.println("\nYou have " + user.team.remainingPalmons() + " Palmons left.");
        System.out.println(opponent.name + " has " + opponent.team.remainingPalmons() + " Palmons left.");

        System.out.println("\n Currently fighting Palmons (yours is the first):");
        ArrayList<Palmon> fightingPalmons = new ArrayList<>();
        fightingPalmons.add(user.team.fightingPalmon);
        fightingPalmons.add(opponent.team.fightingPalmon);
        TableOutput.printPalmonTable(fightingPalmons);
        ExecutionPause.sleep(3);
    }

    public void printResult() {
        if (opponent.team.isDefeated()) {
            System.out.println("\nCongratulations, " + user.name + "! You have defeated " + opponent.name + "with these Palmons:");
            TableOutput.printPalmonTable(new ArrayList<>(user.team.palmons));
        } else {
            System.out.println("\nAs expected, " + opponent.name + " has destroyed you. But even in loss, you have the power to rise again.");
        }
        ExecutionPause.sleep(1);
    }
}
