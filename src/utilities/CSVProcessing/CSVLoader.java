package utilities.CSVProcessing;

import entities.Palmon;
import entities.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Loads data from CSV files asynchronously using CompletableFuture.
 */
public class CSVLoader {
    private final PalmonProcessor palmonProcessor = new PalmonProcessor();
    private final MoveProcessor moveProcessor = new MoveProcessor();
    private final PalmonMoveProcessor palmonMoveProcessor = new PalmonMoveProcessor();
    private final EffectivityProcessor effectivityProcessor = new EffectivityProcessor();

    /**
     * Loads data from CSV files asynchronously.
     *
     * @return a CompletableFuture representing the asynchronous operation
     */
    public CompletableFuture<Void> loadCSVFiles() {
        final String basePath = "src/resources/csv/";
        Map<String, LineProcessor<?>> processors = Map.of(
                "palmon", palmonProcessor,
                "moves", moveProcessor,
                "palmon_move", palmonMoveProcessor,
                "effectivity", effectivityProcessor
        );

        return CompletableFuture.runAsync(() -> {
            // ExecutorService helps manage multiple threads and read multiple CSV files concurrently
            try (ExecutorService executor = Executors.newFixedThreadPool(processors.size())) {
                // Read data from each CSV file in separate thread
                for (Map.Entry<String, LineProcessor<?>> processor : processors.entrySet()) {
                    executor.submit(new CSVReader(basePath + processor.getKey() + ".csv", processor.getValue()));
                }
                executor.shutdown(); // When all tasks are submitted, no new tasks can be added
                try {
                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        executor.shutdownNow(); // Stop all running tasks if not finished after 10 seconds
                    }
                } catch (InterruptedException e) {
                    // Handle interruption
                    Thread.currentThread().interrupt();
                    executor.shutdownNow();
                    System.out.println("File loading stopped.");
                }
            }
        });
    }

    /**
     * Retrieves the processed palmon data.
     *
     * @return the processed palmon data
     */
    public ArrayList<Palmon> getPalmonData() {
        return palmonProcessor.getData();
    }

    /**
     * Retrieves the processed move data.
     *
     * @return the processed move data
     */
    public ArrayList<Move> getMoveData() {
        return moveProcessor.getData();
    }

    /**
     * Retrieves the processed palmon-move data.
     *
     * @return the processed palmon-move data
     */
    public HashMap<Integer, HashMap<Integer, Integer>> getPalmonMoveData() {
        return palmonMoveProcessor.getData();
    }

    /**
     * Retrieves the processed effectivity data.
     *
     * @return the processed effectivity data
     */
    public HashMap<String, HashMap<String, Float>> getEffectivityData() {
        return effectivityProcessor.getData();
    }
}
