package main.observerEvents;

import java.io.File;

/**
 * Observer for high-level game events such as player death and level completion,
 * keep adding more here if needed.
 * https://refactoring.guru/design-patterns/observer
 */
public class PlayerEventListener implements EventListener {

    //Called whenever the player dies.
    public void onPlayerDeath() {
    }

    /**
     * when a level is completed.
     * @param levelIndex : index of the level
     * @param totalDeaths : total deaths
     * @param time : time taken
     */
    void onLevelCompleted() {
    }

    @Override
    public void update(String eventType, File file) {

    }
}

