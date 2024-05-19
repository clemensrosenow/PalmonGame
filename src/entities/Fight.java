package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.Localization;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

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
        int playerPalmonCount = UserInput.number(Localization.getMessage("fight.prompt.team.size"), 1, DB.totalPalmonCount());
        final int minimumRange = Palmon.highestLevelPossible - Move.medianUnlockLevel;
        int minLevel = Palmon.lowestLevelPossible;
        int maxLevel = Palmon.highestLevelPossible;

        // Optional Level Range Selection
        if (UserInput.confirm(Localization.getMessage("fight.prompt.level.range"))) {
            minLevel = UserInput.number(Localization.getMessage("fight.prompt.lowest.level"), Palmon.lowestLevelPossible, Palmon.highestLevelPossible - minimumRange);
            maxLevel = UserInput.number(Localization.getMessage("fight.prompt.highest.level"), minLevel + minimumRange, Palmon.highestLevelPossible);
        }

        // Player Team Setup
        String selectedMethod = UserInput.select(Localization.getMessage("fight.prompt.select.attribute"), TeamAssembler.Method.getLocalizedOptions());
        TeamAssembler.Method assemblyMethod = Arrays.stream(TeamAssembler.Method.values()).filter(method -> method.getLocalized().equals(selectedMethod)).findFirst().orElse(TeamAssembler.Method.random);
        user.team = new Team(playerPalmonCount, minLevel, maxLevel, assemblyMethod);
        System.out.println("\n" + Localization.getMessage("fight.team.consists.of"));
        TableOutput.printPalmonTable(new ArrayList<>(user.team.palmons));
        ExecutionPause.sleep(3);
    }

    private void assembleOpponentTeam() {
        Random random = new Random();
        int opponentPalmonCount = random.nextInt(user.team.palmons.size() * 2 - 1) + 1;
        if (UserInput.confirm(Localization.getMessage("fight.prompt.opponent.team.size", opponent.name))) {
            opponentPalmonCount = UserInput.number(Localization.getMessage("fight.prompt.opponent.team.size.input"), 1, DB.totalPalmonCount());
        }
        opponent.team = new Team(opponentPalmonCount, Palmon.lowestLevelPossible, Palmon.highestLevelPossible, TeamAssembler.Method.random); // opponent team always has full level range
        ExecutionPause.sleep(2);
        System.out.println(Localization.getMessage("fight.opponent.ready", opponent.name));
        ExecutionPause.sleep(2);
    }

    public void battle() {
        while (!user.team.isDefeated() && !opponent.team.isDefeated()) {
            if (UserInput.confirm(Localization.getMessage("fight.prompt.battle.status"))) {
                printBattleStatus();
            }

            if (user.team.fightingPalmon.speed >= opponent.team.fightingPalmon.speed) { // User preferred in case of equal speed
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

    // Returns status if defending Palmon got defeated
    private boolean battleRound(Team attackingTeam, Team defendingTeam, boolean isRandom) {
        System.out.println("\n" + Localization.getMessage("fight.turn.message", isRandom ? opponent.name + "'s" : "your", attackingTeam.fightingPalmon.name));
        attackingTeam.fightingPalmon.performAttack(defendingTeam.fightingPalmon, isRandom);
        if (defendingTeam.fightingPalmon.isDefeated()) {
            defendingTeam.swapFightingPalmon();
            return true;
        }
        return false;
    }

    void printBattleStatus() {
        System.out.println("\n" + Localization.getMessage("fight.you.have.left", user.team.remainingPalmons()));
        System.out.println(Localization.getMessage("fight.opponent.has.left", opponent.name, opponent.team.remainingPalmons()));

        System.out.println(Localization.getMessage("fight.currently.fighting"));
        ArrayList<Palmon> fightingPalmons = new ArrayList<>();
        fightingPalmons.add(user.team.fightingPalmon);
        fightingPalmons.add(opponent.team.fightingPalmon);
        TableOutput.printPalmonTable(fightingPalmons);
        ExecutionPause.sleep(3);
    }

    public void printResult() {
        if (opponent.team.isDefeated()) {
            System.out.println(Localization.getMessage("fight.congratulations", user.name, opponent.name));
            TableOutput.printPalmonTable(new ArrayList<>(user.team.palmons));
        } else {
            System.out.println(Localization.getMessage("fight.loss.message", opponent.name));
        }
        ExecutionPause.sleep(1);
    }
}
