package main.observerEvents;

import java.io.File;

/** Listener for high-level level-completed events. */
public class LevelCompletedListener implements EventListener {

    /**
     * Called when a level has just been completed.
     *
     * @param levelIndex  index of the completed level (0-based)
     * @param totalDeaths total deaths for the current run
     * @param timeMs      time taken in milliseconds
     */
    void onLevelCompleted(int levelIndex, int totalDeaths, double timeMs) {

    }

    @Override
    public void update(String eventType, File file) {

    }
}

