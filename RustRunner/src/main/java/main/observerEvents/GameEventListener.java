package main.observerEvents;

/**
 * Observer for high-level game events such as player death and level completion,
 * keep adding more here if needed.
 * https://refactoring.guru/design-patterns/observer
 */
public interface GameEventListener {

    //Called whenever the player dies.
    default void onPlayerDeath() {
    }

    /**
     * when a level is completed.
     * @param levelIndex : index of the level
     * @param totalDeaths : total deaths
     * @param time : time taken
     */
    default void onLevelCompleted(int levelIndex, int totalDeaths, double time) {
    }
}

