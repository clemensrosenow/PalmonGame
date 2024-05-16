import entities.Player;
import entities.Move;
import entities.Palmon;
import entities.Team;
import utils.Input;
import utils.CSVProcessing;
import utils.Localization;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

import resources.Constants;

public class Game {
    static Player player = new Player();
    static Player opponent = new Player();

    public static void main(String[] args) {
        CompletableFuture<Void> asyncFileLoading = CSVProcessing.loadCSVFiles();
        System.out.println("Welcome to Palmon!");
        //Setup
        Localization.configureLanguage();
        assignCharacterNames();
        asyncFileLoading.join(); //Block until CSV processing is complete

        //Game Loop
        do {
            //Todo: Make steps more explicit -> add resultDetermination and processing
            Fight fightRound = new Fight();
            fightRound.assembleTeams();
            fightRound.fight();
        } while (Input.confirm("Do you want to play another round?"));

    }

    public static void assignCharacterNames() {
        Game.player.name = CSVProcessing.normalize(Input.text(Localization.getMessage("name_player_question")));
        System.out.println(Localization.getMessage("greeting") + " " + Game.player.name + "!");
        Game.opponent.name = CSVProcessing.normalize(Input.text(Localization.getMessage("name_opponent_question")));
    }


    private static class Fight {
        Player winner;
        Player loser;
        //Todo: inner class for results of winner / loser + fighting palmons


        public void assembleTeams() {
            // entities.Palmon Count Selection
            int playerPalmonCount = Input.number("How many Palmons do you want to have in your team? ", 1, Constants.PalmonCount);
            int opponentPalmonCount = Input.number("How many Palmons does your opponent have in his team? ", 1, Constants.PalmonCount);

            // Assemble Method Selection
            Team.AssembleMethod assembleMethod = Team.AssembleMethod.valueOf(Input.select("By which attribute do you want to select your Palmons?", Team.AssembleMethod.values(), Team.AssembleMethod.random.name()));

            // Player Level Range Selection
            int minLevel = 0;
            int maxLevel = Constants.totalLevels;

            // Set optional level range
            if (Input.confirm("Do you want to set a level range for your Palmons?")) {
                minLevel = Input.number("What's the lowest possible level of your Palmons? ", 0, Constants.totalLevels);
                maxLevel = Input.number("What's the highest possible level of your Palmons? ", minLevel, Constants.totalLevels);
            }

            // Setup
            player.team = new Team(playerPalmonCount, minLevel, maxLevel);
            player.team.assemble(assembleMethod);

            // Opponent team always consists of randomly selected palmons in full level range
            opponent.team = new Team(opponentPalmonCount, 0, Constants.totalLevels);
            opponent.team.assemble(Team.AssembleMethod.random);
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
}