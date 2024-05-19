package utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.Character;

import entities.Move;
import entities.Palmon;

/*public class CSVProcessing {
    public static ArrayList<Palmon> palmons;
    static CopyOnWriteArrayList<Move> moves;
    public static HashMap<Integer, HashMap<Integer, Integer>> palmonMoves;
    public static HashMap<String, HashMap<String, Float>> effectivity;

    static final private DataProcessor.PalmonProcessor palmonProcessor = new DataProcessor.PalmonProcessor();
    static final private DataProcessor.MoveProcessor moveProcessor = new DataProcessor.MoveProcessor();
    static final private DataProcessor.PalmonMoveProcessor palmonMoveProcessor = new DataProcessor.PalmonMoveProcessor();
    static final private DataProcessor.EffectivityProcessor effectivityProcessor = new DataProcessor.EffectivityProcessor();

    public static Palmon getPalmonById(int palmonId) {
        return palmons.stream().filter(palmon -> palmon.id == palmonId).findFirst().orElse(null);
    }

    public static Move getMoveById(int moveId) {
        return moves.stream().filter(move -> move.id == moveId).findFirst().orElse(null);
    }

    public static CompletableFuture<Void> loadCSVFiles() {
        Map<String, DataProcessor> processors = Map.of(
                "palmon", palmonProcessor,
                "moves", moveProcessor,
                "palmon_move", palmonMoveProcessor,
                "effectivity", effectivityProcessor
        );

        CompletableFuture<Void> asyncFileReader = CompletableFuture.runAsync(() -> {
            //Parallel processing of the CSV files
            try (ExecutorService executor = Executors.newFixedThreadPool(processors.size())) {
                for (Map.Entry<String, DataProcessor> processor : processors.entrySet()) {
                    executor.submit(new CSVReader("src/resources/csv/" + processor.getKey() + ".csv", processor.getValue()));
                }

                //Initiate orderly shutdown, no new tasks will be accepted
                executor.shutdown();

                //Graceful shutdown: Avoid loading longer than 10 seconds
                try {
                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    executor.shutdownNow();
                    System.out.println("File loading stopped.");
                }
            }
        });

        return asyncFileReader.thenRun(() -> {
            //Assign data once all files are loaded
            palmons = palmonProcessor.getData();
            moves = moveProcessor.getData();
            palmonMoves = palmonMoveProcessor.getData();
            effectivity = effectivityProcessor.getData();
        });
    }
}

*/
