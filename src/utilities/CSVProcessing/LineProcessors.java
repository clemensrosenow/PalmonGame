package utilities.CSVProcessing;

import entities.Move;
import entities.Palmon;
import resources.LearnableMove;
import utilities.DataNormalization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Abstract class to process lines from CSV files.
 *
 * @param <ProcessingType> the type of data being processed
 */
abstract class LineProcessor<ProcessingType> {
    protected ProcessingType data;

    /**
     * Constructs a LineProcessor with the base data structure.
     *
     * @param baseDataStructure the base object to process
     */
    protected LineProcessor(ProcessingType baseDataStructure) {
        this.data = baseDataStructure;
    }

    /**
     * Processes a line from the CSV file.
     *
     * @param values the values parsed from the CSV line
     */
    abstract void processLine(String[] values);

    /**
     * Retrieves the processed data.
     *
     * @return the processed data
     */
    ProcessingType getData() {
        return data;
    }
}

/**
 * Processes lines from a CSV file containing the moves.
 */
class MoveProcessor extends LineProcessor<ArrayList<Move>> {
    MoveProcessor() {
        super(new ArrayList<>());
    }
    @Override
    void processLine(String[] values) {
        data.add(new Move(
                DataNormalization.number(values[0]),
                DataNormalization.name(values[1], '-', ' '),
                DataNormalization.number(values[2]),
                DataNormalization.number(values[3]),
                DataNormalization.percentage(values[4]),
                DataNormalization.word(values[5])
        ));
    }
}


/**
 * Processes lines from a CSV file containing the effectivity mapping of Palmon types.
 */
class EffectivityProcessor extends LineProcessor<HashMap<String, HashMap<String, Float>>> {
    EffectivityProcessor() {
        super(new HashMap<>());
    }

    @Override
    void processLine(String[] values) {
        String attackerType = DataNormalization.word(values[0]);
        String targetType = DataNormalization.word(values[1]);
        float damageFactor = DataNormalization.percentage(values[2]);

        data.putIfAbsent(attackerType, new HashMap<>());
        data.get(attackerType).put(targetType, damageFactor);
    }
}

/**
 * Processes lines from a CSV file containing the mapping of PalmonId to the amount of learnable moves.
 */
class PalmonMoveProcessor extends LineProcessor<HashMap<Integer, HashSet<LearnableMove>>> {
    PalmonMoveProcessor() {
        super(new HashMap<>());
    }

    @Override
    void processLine(String[] values) {
        int palmonID = DataNormalization.number(values[0]);
        int moveID = DataNormalization.number(values[1]);
        int learnedOnLevel = DataNormalization.number(values[2]);

        data.putIfAbsent(palmonID, new HashSet<>());
        data.get(palmonID).add(new LearnableMove(moveID, learnedOnLevel));
    }
}

/**
 * Processes lines from a CSV file containing Palmon data.
 */
class PalmonProcessor extends LineProcessor<ArrayList<Palmon>> {
    PalmonProcessor() {
        super(new ArrayList<>());
    }

    @Override
    public void processLine(String[] values) {
        data.add(new Palmon(
                DataNormalization.number(values[0]),
                DataNormalization.name(values[1], '-', ' '),
                DataNormalization.number(values[2]),
                DataNormalization.number(values[3]),
                DataNormalization.word(values[4]),
                DataNormalization.word(values[5]),
                DataNormalization.number(values[6]),
                DataNormalization.number(values[7]),
                DataNormalization.number(values[8]),
                DataNormalization.number(values[9])
        ));
    }
}
