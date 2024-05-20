package entities;

import java.util.*;

public class Team extends TeamAssembler {
    private final Iterator<Palmon> fightingQueue;
    Palmon fightingPalmon;

    /**
     * Constructs a Team with the specified parameters.
     *
     * @param palmonCount the number of Palmons in the team
     * @param minLevel the minimum possible level of the Palmons
     * @param maxLevel the maximum possible level of the Palmons
     * @param assemblyMethod the method used to assemble the team
     */
    Team(int palmonCount, int minLevel, int maxLevel, TeamAssembler.Method assemblyMethod) {
        super(palmonCount, minLevel, maxLevel, assemblyMethod); // Assemble the team based on the given parameters
        this.fightingQueue = palmons.iterator(); // Initialize the fighting queue with the assembled Palmons
        this.fightingPalmon = fightingQueue.next(); // Set the first Palmon in the queue as the fighting Palmon
    }

    /**
     * Replace the current fighting Palmon with the next one in the queue.
     * Called when the current fighting Palmon is defeated.
     */
    void swapFightingPalmon() {
        if (!fightingQueue.hasNext()) {
            fightingPalmon = null; // No more Palmons left to fight
            return;
        }
        fightingPalmon = fightingQueue.next(); // Get next Palmon in the queue
    }

    /**
     * Checks if the whole team is defeated.
     *
     * @return true if all Palmons in the team are defeated, false otherwise
     */
    boolean isDefeated() {
        return palmons.stream().allMatch(Palmon::isDefeated);
    }

    /**
     * Gets the number of remaining combat-ready Palmons.
     *
     * @return the count of Palmons that are not defeated
     */
    int remainingPalmons() {
        return palmons.stream().filter(palmon -> !palmon.isDefeated()).toList().size();
    }
}