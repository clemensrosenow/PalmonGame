package entities;

import resources.DB;
import utilities.ExecutionPause;
import utilities.Localization;
import utilities.TableOutput;
import utilities.UserInput;

import java.util.ArrayList;

public class Fight {
    private final User user;
    private final Opponent opponent;

    /**
     * Constructs a Fight instance between the given user and opponent.
     *
     * @param user the user player
     * @param opponent the opponent player
     */
    public Fight(User user, Opponent opponent) {
        this.user = user;
        this.opponent = opponent;
    }

    /**
     * Assembles the teams of both players.
     */
    public void assembleTeams() {
        // Prompt user team size, limited to half count of all Palmons
        int palmonCount = UserInput.number(Localization.getMessage("fight.prompt.team.size"), 1, DB.halfPalmonCount());

        user.assembleTeam(palmonCount);
        opponent.assembleTeam(palmonCount);
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
