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

public class CSVLoader {
    private final PalmonProcessor palmonProcessor = new PalmonProcessor();
    private final MoveProcessor moveProcessor = new MoveProcessor();
    private final PalmonMoveProcessor palmonMoveProcessor = new PalmonMoveProcessor();
    private final EffectivityProcessor effectivityProcessor = new EffectivityProcessor();

    public CompletableFuture<Void> loadCSVFiles() {
        final String basePath = "src/resources/csv/";
        Map<String, LineProcessor<?>> processors = Map.of(
                "palmon", palmonProcessor,
                "moves", moveProcessor,
                "palmon_move", palmonMoveProcessor,
                "effectivity", effectivityProcessor
        );

        return CompletableFuture.runAsync(() -> {
            try (ExecutorService executor = Executors.newFixedThreadPool(processors.size())) {
                for (Map.Entry<String, LineProcessor<?>> processor : processors.entrySet()) {
                    executor.submit(new CSVReader(basePath + processor.getKey() + ".csv", processor.getValue()));
                }
                executor.shutdown();
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
    }

    public ArrayList<Palmon> getPalmonData() {
        return palmonProcessor.getData();
    }
    public ArrayList<Move> getMoveData() {
        return moveProcessor.getData();
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getPalmonMoveData() {
        return palmonMoveProcessor.getData();
    }

    public HashMap<String, HashMap<String, Float>> getEffectivityData() {
        return effectivityProcessor.getData();
    }
}
