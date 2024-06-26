package utilities.CSVProcessing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Reads data from a CSV file and processes each line.
 * Can be invoked asynchronously by an ExecutorService.
 */
class CSVReader implements Runnable {
    private final String path;
    private final LineProcessor<?> processor;

    public CSVReader(String path, LineProcessor<?> processor) {
        this.path = path;
        this.processor = processor;
    }

    @Override
    public void run() {
        try (Stream<String> entries = Files.lines(Paths.get(path))) {
            entries.skip(1). //Skip Header
                    forEach(entry -> processor.processLine(entry.split(";")));
        } catch (IOException e) {
            System.out.println("Error processing file " + path + ": " + e.getMessage());
        }
    }
}
