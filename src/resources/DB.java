package resources;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import entities.Move;
import entities.Palmon;
import utilities.CSVProcessing.CSVLoader;


/**
 * Utility class for accessing the processed CSV data.
 */
public class DB {
    private static ArrayList<Palmon> palmons;
    private static ArrayList<Move> moves;
    private static HashMap<Integer, HashSet<LearnableMove>> palmonMoves; //PalmonId -> {MoveId, Level}
    private static HashMap<String, HashMap<String, Float>> effectivity;

    /**
     * Fetches data from CSV files using the provided CSVLoader.
     *
     * @param csvLoader the CSVLoader to load data from CSV files
     */
    public static void fetchData(CSVLoader csvLoader) {
        CompletableFuture<Void> asyncFileReader = csvLoader.loadCSVFiles();
        asyncFileReader.thenRun(() -> {
            palmons = csvLoader.getPalmonData();
            moves = csvLoader.getMoveData();
            palmonMoves = csvLoader.getPalmonMoveData();
            effectivity = csvLoader.getEffectivityData();
        });
    }

    /**
     * Retrieves the list of all unused Palmons.
     *
     * @return the list of unused Palmons
     */
    public static ArrayList<Palmon> getUnusedPalmons() {
        return palmons.stream().filter(palmon -> !palmon.inTeam).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves the moves learned by a specific Palmon at various levels.
     *
     * @param palmonId the ID of the Palmon
     * @return a HashSet containing the all moves learned by the Palmon
     */
    public static  HashSet<LearnableMove> getPalmonMoves(int palmonId) {
        return palmonMoves.get(palmonId);
    }

    /**
     * Retrieves the effectiveness of palmon type against another type.
     *
     * @param attackerType the type of the attacker
     * @param victimType the type of the victim
     * @return the effectiveness factor
     */
    public static float getEffectivity(String attackerType, String victimType) {
        return effectivity.get(attackerType).get(victimType);
    }


    /**
     * Retrieves half of the total count of Palmons.
     *
     * @return the half count of Palmons
     */
    public static int halfPalmonCount() {
        return palmons.size() / 2;
    }

    /**
     * Retrieves a random unused Palmon from the list.
     *
     * @return a random unused Palmon
     */
    public static Palmon getRandomUnusedPalmon() {
        Random random = new Random();
        ArrayList<Palmon> unusedPalmons = getUnusedPalmons();
        return unusedPalmons.get(random.nextInt(unusedPalmons.size()));
    }

    /**
     * Retrieves a Move by its ID.
     *
     * @param moveId the ID of the Move
     * @return an Optional containing the Move if found, otherwise empty
     */
    public static Optional<Move> getMoveById(int moveId) {
        return moves.stream().filter(move -> move.id == moveId).findFirst();
    }

    /**
     * Groups Palmons by their types.
     *
     * @return a HashMap where keys are types and values are lists of Palmons
     */
    public static HashMap<String, ArrayList<Palmon>> getUnusedPalmonsGroupedByType() {
        HashMap<String, ArrayList<Palmon>> palmonsByType = new HashMap<>();
        for (Palmon palmon : getUnusedPalmons()) {
            for (String type : palmon.types) {
                palmonsByType.putIfAbsent(type, new ArrayList<>()); // Add type if not present
                palmonsByType.get(type).add(palmon); // Add Palmon to the type list
            }
        }
        palmonsByType.remove(""); //Some Palmons have an empty second type, that's not considered a type
        return palmonsByType;
    }

    private DB(){} //Private constructor to prevent instantiation
}