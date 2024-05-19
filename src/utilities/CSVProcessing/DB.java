package utilities.CSVProcessing;

import java.util.*;
import java.util.concurrent.*;

import entities.Move;
import entities.Palmon;
public class DB {
    //Non-static attributes for isolation and concurrency
    private static ArrayList<Palmon> palmons; //Todo: palmons / moves to HashMap for easier key access
    private static ArrayList<Move> moves;
    private static HashMap<Integer, HashMap<Integer, Integer>> palmonMoves;
    private static HashMap<String, HashMap<String, Float>> effectivity;


    public DB(CSVLoader csvLoader) {
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

    public static ArrayList<Move> getMoves() {
        return moves;
    }

    public static HashMap<Integer, HashMap<Integer, Integer>> getPalmonMoves() {
        return palmonMoves;
    }

    public static float getEffectivity(String attackerType, String victimType) {
        return effectivity.get(attackerType).get(victimType);
    }

    public static int totalPalmonCount() {
        return palmons.size();
    }

    public static Palmon getPalmonById(int palmonId) {
        return palmons.stream().filter(palmon -> palmon.id == palmonId).findFirst().orElse(null);
    }

    public static Move getMoveById(int moveId) {
        return moves.stream().filter(move -> move.id == moveId).findFirst().orElse(null);
    }

    //Group palmons by type in a HashMap
    public static HashMap<String, ArrayList<Palmon>> getPalmonsByType() {
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
}



