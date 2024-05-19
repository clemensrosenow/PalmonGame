package utilities.CSVProcessing;

import entities.Move;
import entities.Palmon;
import utilities.DataNormalization;

import java.util.ArrayList;
import java.util.HashMap;

abstract class LineProcessor<ProcessingType> {
    protected ProcessingType data;
    protected LineProcessor(ProcessingType baseDataStructure) {
        this.data = baseDataStructure;
    }

    abstract void processLine(String[] values);

    ProcessingType getData() {
        return data;
    };
}

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
                DataNormalization.number(values[4]),
                DataNormalization.word(values[5])
        ));
    }
}

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


class PalmonMoveProcessor extends LineProcessor<HashMap<Integer, HashMap<Integer, Integer>>> {
    PalmonMoveProcessor() {
        super(new HashMap<>());
    }

    @Override
    void processLine(String[] values) {
        int palmonID = DataNormalization.number(values[0]);
        int moveID = DataNormalization.number(values[1]);
        int learnedOnLevel = DataNormalization.number(values[2]);

        data.putIfAbsent(palmonID, new HashMap<>());
        data.get(palmonID).put(learnedOnLevel, moveID);
    }
}

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
