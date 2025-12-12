package main.observerEvents;

import java.io.File;

/** Listener notified when the current level index changes in the model. */
public class LevelChangeListener implements EventListener {

    /**
     * Call this when model has advanced to a new level index.
     *
     * @param newLevelIndex the newly selected level index
     */
    void onLevelChanged(int newLevelIndex) {

    }

    @Override
    public void update(String eventType, File file) {

    }
}
