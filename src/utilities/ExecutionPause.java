package utilities;

/**
 * Utility for pausing execution for a specified duration to improve console output UX.
 */
public class ExecutionPause {
    /**
     * Pauses execution for the specified number of seconds.
     *
     * @param seconds the number of seconds to pause execution
     */
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L); //Explicit long conversion
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //Re-assert the interrupted status
        }
    }
}
