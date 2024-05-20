package resources;

import java.util.*;
import java.util.concurrent.*;

import entities.Move;
import entities.Palmon;
import utilities.CSVProcessing.CSVLoader;


/**
 * Utility class for accessing the processed CSV data.
 */
public class DB {
    private static ArrayList<Palmon> palmons;
    private static ArrayList<Move> moves;
    private static HashMap<Integer, HashMap<Integer, Integer>> palmonMoves; //PalmonId -> Level -> MoveId
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
     * Retrieves the list of all Palmons.
     *
     * @return the list of Palmons
     */
    public static ArrayList<Palmon> getPalmons() {
        return palmons;
    }

    /**
     * Retrieves the moves learned by a specific Palmon at various levels.
     *
     * @param palmonId the ID of the Palmon
     * @return a HashMap where keys are levels and values are move IDs
     */
    public static  HashMap<Integer, Integer> getPalmonMoves(int palmonId) {
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
     * Retrieves the total count of Palmons.
     *
     * @return the total count of Palmons
     */
    public static int totalPalmonCount() {
        return palmons.size();
    }

    /**
     * Retrieves a random Palmon from the list.
     *
     * @return a random Palmon
     */
    public static Palmon getRandomPalmon() {
        Random random = new Random();
        return palmons.get(random.nextInt(palmons.size()));
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
    public static HashMap<String, ArrayList<Palmon>> getPalmonsByType() {
        HashMap<String, ArrayList<Palmon>> palmonsByType = new HashMap<>();
        for (Palmon palmon : palmons) {
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