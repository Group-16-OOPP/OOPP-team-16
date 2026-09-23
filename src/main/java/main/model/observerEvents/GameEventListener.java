package main.model.observerEvents;

public interface GameEventListener {
    
    default void onPlayerDeath() {
    }

    default void onLevelCompleted(
        int levelIndex,
        int totalDeaths,
        double time) {
    }
}
