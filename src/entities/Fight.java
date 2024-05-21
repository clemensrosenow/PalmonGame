package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.Localization;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Fight {
    private final Player user;
    private final Player opponent;

    /**
     * Constructs a Fight instance between the given user and opponent.
     *
     * @param user the user player
     * @param opponent the opponent player
     */
    public Fight(Player user, Player opponent) {
        this.user = user;
        this.opponent = opponent;
    }

    /**
     * Assembles the teams of both players.
     */
    public void assembleTeams() {
        assembleUserTeam();
        assembleOpponentTeam();
    }

    /**
     * Assembles the team for the user by prompting for parameters.
     * These include the team size, optionally a level range, and the assembly method.
     * @see TeamAssembler for the available assembly methods
     */
    private void assembleUserTeam() {
        // Palmon Count Selection
        int playerPalmonCount = UserInput.number(Localization.getMessage("fight.prompt.team.size"), 1, DB.halfPalmonCount());

        // Level Range Base Values
        int minLevel = Palmon.lowestLevelPossible;
        int maxLevel = Palmon.highestLevelPossible;

        // Optional Level Range Selection
        if (UserInput.confirm(Localization.getMessage("fight.prompt.level.range"))) {
            // Inclusion of minimumRange, so enough moves can be assigned
            minLevel = UserInput.number(Localization.getMessage("fight.prompt.lowest.level"), Palmon.lowestLevelPossible, Move.medianUnlockLevel);
            int minimumRange = Palmon.highestLevelPossible - Move.medianUnlockLevel; // Ensures a minimum level range based on the median unlock level
            maxLevel = UserInput.number(Localization.getMessage("fight.prompt.highest.level"), minLevel + minimumRange, Palmon.highestLevelPossible);
        }

        // Assembly Method Selection
        String selectedMethod = UserInput.select(Localization.getMessage("fight.prompt.select.attribute"), TeamAssembler.Method.getLocalizedOptions());
        TeamAssembler.Method assemblyMethod = Arrays.stream(TeamAssembler.Method.values()).filter(method -> method.getLocalized().equals(selectedMethod)).findFirst().orElse(TeamAssembler.Method.random); //Map selected method to enum, value is always found

        // Set up user team
        user.team = new Team(playerPalmonCount, minLevel, maxLevel, assemblyMethod);

        // Print overview of user team Palmons
        System.out.println("\n" + Localization.getMessage("fight.team.consists.of"));
        ExecutionPause.sleep(2);
        TableOutput.printPalmonTable(new ArrayList<>(user.team.palmons));

        ExecutionPause.sleep(3);
    }

    /**
     * Assembles the team for the opponent, optionally allowing the user to specify the team size.
     * Level range and assembly method can't be influenced by the user.
     */
    private void assembleOpponentTeam() {
        int opponentPalmonCount;
        // Set opponent team size, limited to half count of all Palmons
        // Allow user to set specific opponent team size if desired
        if (UserInput.confirm(Localization.getMessage("fight.prompt.opponent.team.size", opponent.name))) {
            opponentPalmonCount = UserInput.number(Localization.getMessage("fight.prompt.opponent.team.size.input"), 1, DB.halfPalmonCount());
        } else {
            // If not, randomly generate opponent team size based on user team size
            Random random = new Random();
            int randomPalmonCount = random.nextInt(user.team.palmons.size() * 2 - 1) + 1; // set count to random int >= 1, which averages the user team size
            opponentPalmonCount = Math.min(randomPalmonCount, DB.halfPalmonCount());
        }

        // Set up opponent team
        // Always random assembly with full level range
        opponent.team = new Team(opponentPalmonCount, Palmon.lowestLevelPossible, Palmon.highestLevelPossible, TeamAssembler.Method.random);
        ExecutionPause.sleep(2);
    }

    /**
     * Conducts the battle between the user's team and the opponent's team.
     * Consists of attack exchanges until one of the teams is defeated.
     * @see Fight#attack(Team, Team, boolean)
     */
    public void battle() {
        // Notify user of battle start
        System.out.println(Localization.getMessage("fight.opponent.ready", opponent.name));
        ExecutionPause.sleep(2);

        // Battle round loop while no team is defeated
        while (!user.team.isDefeated() && !opponent.team.isDefeated()) {
            // At the beginning of each round, the user can check the battle status
            if (UserInput.confirm(Localization.getMessage("fight.prompt.battle.status"))) {
                printBattleStatus();
            }

            //Faster Palmon attacks first
            // User team preferred in case of equal speed
            if (user.team.fightingPalmon.speed >= opponent.team.fightingPalmon.speed) {
                if (attack(user.team, opponent.team, false)) {
                    // If opponent's Palmon got defeated, continue with next round
                    continue;
                }
                ExecutionPause.sleep(1);
                attack(opponent.team, user.team, true); // Counterattack by opponent
            } else {
                if (attack(opponent.team, user.team, true)) {
                    // If user's Palmon got defeated, continue with next round
                    continue;
                }
                ExecutionPause.sleep(1);
                attack(user.team, opponent.team, false); // Counterattack by user
            }
        }
    }

    /**
     * Executes a single battle round between an attacking and defending team.
     *
     * @param attackingTeam the team that is attacking
     * @param defendingTeam the team that is defending
     * @param randomMove whether a random attacking move will be selected, also indicator of attacking player
     * @return true if attack was successful and the defending Palmon got defeated, false otherwise
     */
    private boolean attack(Team attackingTeam, Team defendingTeam, boolean randomMove) {
        System.out.println(Localization.getMessage("fight.turn.message", randomMove ? opponent.name : user.name, attackingTeam.fightingPalmon.name));

        // Attacking Palmon attacks the defending Palmon
        attackingTeam.fightingPalmon.performAttack(defendingTeam.fightingPalmon, randomMove);

        if (defendingTeam.fightingPalmon.isDefeated()) {
            // If defender got defeated, replace Palmon with the next one in line
            defendingTeam.swapFightingPalmon();
            return true;
        }
        return false;
    }

    /**
     * Prints the count of remaining and a table of currently fighting Palmons.
     */
    private void printBattleStatus() {
        // Display remaining Palmons of both players
        System.out.println("\n" + Localization.getMessage("fight.you.have.left", user.team.remainingPalmons()));
        System.out.println(Localization.getMessage("fight.opponent.has.left", opponent.name, opponent.team.remainingPalmons()));

        // Set up a list of currently fighting Palmons
        ArrayList<Palmon> fightingPalmons = new ArrayList<>();
        fightingPalmons.add(user.team.fightingPalmon);
        fightingPalmons.add(opponent.team.fightingPalmon);

        // Display the currently fighting Palmons
        System.out.println(Localization.getMessage("fight.currently.fighting"));
        TableOutput.printPalmonTable(fightingPalmons);

        ExecutionPause.sleep(3);
    }

    /**
     * Prints the final fight outcome and motivates the user to continue playing.
     */
    public void printResult() {
        if (opponent.team.isDefeated()) {
            // If user wins, print congratulations message and display the user's full team
            System.out.println(Localization.getMessage("fight.congratulations", user.name, opponent.name));
            TableOutput.printPalmonTable(new ArrayList<>(user.team.palmons));
        } else {
            // If user loses, print loss message
            System.out.println(Localization.getMessage("fight.loss.message", opponent.name));
        }
        ExecutionPause.sleep(1); // Pause before asking user to play again
    }
}
