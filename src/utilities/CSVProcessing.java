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

public class CSVProcessing {
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

    public static String normalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    public static String normalizeName(String word) {
        String splitSymbol = "-";
        String joinSymbol = " ";
        return Arrays.stream(word.split(splitSymbol))
                .map(CSVProcessing::normalize)
                .collect(Collectors.joining(joinSymbol));
    }
}


class CSVReader implements Runnable {
    private final String path;
    private final DataProcessor processor;

    public CSVReader(String path, DataProcessor processor) {
        this.path = path;
        this.processor = processor;
    }

    @Override
    public void run() { //Automatically invoked by ExecutorService
        try (Stream<String> entries = Files.lines(Paths.get(path))) {
            entries.skip(1). //Skip Header
                    forEach(entry -> processor.processLine(entry.split(";")));
        } catch (IOException e) {
            System.out.println("Error processing file " + path + ": " + e.getMessage());
        }
    }
}

abstract class DataProcessor {
    abstract void processLine(String[] values);

    abstract Object getData();

    private static int number(String attribute) {
        return Integer.parseInt(attribute);
    }

    static class EffectivityProcessor extends DataProcessor {
        private final HashMap<String, HashMap<String, Float>> data = new HashMap<>();

        public void processLine(String[] values) {
            String attackerType = values[0];
            String targetType = values[1];
            Float damageFactor = Float.parseFloat(values[2].replace("%", "")) / 100;

            data.putIfAbsent(attackerType, new HashMap<>());
            data.get(attackerType).put(targetType, damageFactor);
        }

        @Override
        public HashMap<String, HashMap<String, Float>> getData() {
            return data;
        }

    }

    static class MoveProcessor extends DataProcessor {
        private final ArrayList<Move> data = new ArrayList<>();

        public void processLine(String[] values) {
            data.add(new Move(
                    number(values[0]),
                    CSVProcessing.normalizeName(values[1]),
                    number(values[2]),
                    number(values[3]),
                    number(values[4]),
                    CSVProcessing.normalize(values[5])
            ));
        }

        @Override
        public CopyOnWriteArrayList<Move> getData() {
            return new CopyOnWriteArrayList<>(data);
        }
    }

    static class PalmonMoveProcessor extends DataProcessor {
        private final HashMap<Integer, HashMap<Integer, Integer>> data = new HashMap<>();

        @Override
        public void processLine(String[] values) {
            int palmonID = number(values[0]);
            int moveID = number(values[1]);
            int learnedOnLevel = number(values[2]);

            data.putIfAbsent(palmonID, new HashMap<>());
            data.get(palmonID).put(learnedOnLevel, moveID);
        }

        @Override
        public HashMap<Integer, HashMap<Integer, Integer>> getData() {
            return data;
        }
    }

    static class PalmonProcessor extends DataProcessor {
        private final ArrayList<Palmon> data = new ArrayList<>();

        public void processLine(String[] values) {
            data.add(new Palmon(
                    number(values[0]),
                    CSVProcessing.normalizeName(values[1]),
                    number(values[2]),
                    number(values[3]),
                    CSVProcessing.normalize(values[4]),
                    CSVProcessing.normalize(values[5]),
                    number(values[6]),
                    number(values[7]),
                    number(values[8]),
                    number(values[9])
            ));
        }

        @Override
        public ArrayList<Palmon> getData() {
            return data;
        }

    }
}
