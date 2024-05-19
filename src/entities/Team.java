package entities;

import java.util.*;

public class Team extends TeamAssembler {
    private final Iterator<Palmon> fightingQueue;
    Palmon fightingPalmon;

    public Team(int palmonCount, int minLevel, int maxLevel, TeamAssembler.Method assemblyMethod) {
        super(palmonCount, minLevel, maxLevel, assemblyMethod);
        this.fightingQueue = palmons.iterator();
        this.fightingPalmon = fightingQueue.next();
    }


    public void swapFightingPalmon() {
        if (!fightingQueue.hasNext()) {
            fightingPalmon = null;
            return;
        }
        fightingPalmon = fightingQueue.next(); //get next palmon
    }

    boolean isDefeated() {
        return palmons.stream().allMatch(Palmon::isDefeated);
    }

    int remainingPalmons() {
        return palmons.stream().filter(palmon -> !palmon.isDefeated()).toList().size();
    }
}