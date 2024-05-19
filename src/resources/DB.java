package resources;

import java.util.*;
import java.util.concurrent.*;

import entities.Move;
import entities.Palmon;
import utilities.CSVProcessing.CSVLoader;

public class DB {
    private static ArrayList<Palmon> palmons;
    private static ArrayList<Move> moves;
    private static HashMap<Integer, HashMap<Integer, Integer>> palmonMoves; //PalmonId -> Level -> MoveId
    private static HashMap<String, HashMap<String, Float>> effectivity;


    public static void fetchData(CSVLoader csvLoader) {
        CompletableFuture<Void> asyncFileReader = csvLoader.loadCSVFiles();
        asyncFileReader.thenRun(() -> {
            palmons = csvLoader.getPalmonData();
            moves = csvLoader.getMoveData();
            palmonMoves = csvLoader.getPalmonMoveData();
            effectivity = csvLoader.getEffectivityData();
        });
    }


    public static ArrayList<Palmon> getPalmons() {
        return palmons;
    }

    public static  HashMap<Integer, Integer> getPalmonMoves(int palmonId) {
        return palmonMoves.get(palmonId);
    }

    public static float getEffectivity(String attackerType, String victimType) {
        return effectivity.get(attackerType).get(victimType);
    }

    public static void getTotalEffectivity() {
        for (String attackerType : effectivity.keySet()) {
            for (String victimType : effectivity.get(attackerType).keySet()) {
                System.out.println(attackerType + " -> " + victimType + " : " + effectivity.get(attackerType).get(victimType));
            }
        }
    }
    public static int totalPalmonCount() {
        return palmons.size();
    }

    public static Palmon getRandomPalmon() {
        Random random = new Random();
        return palmons.get(random.nextInt(palmons.size()));
    }
    /*public static Optional<Palmon> getPalmonById(int palmonId) {
        return palmons.stream().filter(palmon -> palmon.id == palmonId).findFirst();
    }*/
    public static Optional<Move> getMoveById(int moveId) {
        return moves.stream().filter(move -> move.id == moveId).findFirst();
    }



    public static HashMap<String, ArrayList<Palmon>> getPalmonsByType() {
        //Group palmons by type in a HashMap
        HashMap<String, ArrayList<Palmon>> palmonsByType = new HashMap<>();
        for (Palmon palmon : palmons) {
            for (String type : palmon.types) {
                palmonsByType.putIfAbsent(type, new ArrayList<>());
                palmonsByType.get(type).add(palmon);
            }
        }
        palmonsByType.remove(""); //Empty second type is not considered a type
        return palmonsByType;
    }

    private DB(){}; //Private constructor to prevent instantiation
}



