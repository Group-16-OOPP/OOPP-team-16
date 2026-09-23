package main.model;

import java.util.ArrayList;
import java.util.List;

import main.model.entities.Player;
import main.model.levels.Level;
import main.model.levels.LevelManager;
import main.model.observerEvents.GameObserver;
import utilities.LoadSave;

public class GameModel {

    private static final float TRANSITION_SPEED = 0.015f;

    private final Player player;
    private final LevelManager levelManager;

    // Observer list
    private final List<GameObserver> observers = new ArrayList<>();

    // Controller-independent playing flag
    private boolean isActive = false;

    // Transition state
    private boolean inTransition = false;
    private float transitionScale = 0f;
    private boolean scalingUp = true;
    private boolean isLevelLoaded = false;

    // Logic flags
    private boolean wasPlayerDead = false;
    private boolean isDead = false;
    private boolean isRespawn = false;
    private boolean isEndOfLevel = false;

    // Stats
    private String playerName = "Player1";
    private long startTime;
    private int totalDeaths;

    private boolean isPaused = false;

    public GameModel(Player player, LevelManager levelManager) {
        this.player = player;
        this.levelManager = levelManager;
    }

    // Observer -----------------------------------

    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyPlayerDied() {
        for (GameObserver obs : observers) {
            obs.onPlayerDied();
        }
    }

    private void notifyPlayerRespawn() {
        for (GameObserver obs : observers) {
            obs.onPlayerRespawn();
        }
    }

    private void notifyLevelCompleted() {
        for (GameObserver obs : observers) {
            obs.onLevelCompleted();
        }
    }

    private void notifyLevelLoadRequested() {
        for (GameObserver obs : observers) {
            obs.onLevelLoadRequested();
        }
    }

    private void notifyTransitionComplete() {
        for (GameObserver obs : observers) {
            obs.onTransitionComplete();
        }
    }

    // Update loop --------------------------------

    public void update() {
        if (inTransition) {
            updateTransition();
            return;
        }

        if (isActive) {
            updatePlaying();
        }
    }

    private void updatePlaying() {
        if (isPaused) {
            return;
        }

        player.update();
        levelManager.update();

        boolean isPlayerDead = player.getHitbox().x > 1500;

        if (!wasPlayerDead && isPlayerDead) {
            totalDeaths++;
            notifyPlayerDied();
        } else if (wasPlayerDead && !isPlayerDead) {
            notifyPlayerRespawn();
        }

        wasPlayerDead = isPlayerDead;

        if (player.hasReachedLevelEnd()) {
            notifyLevelCompleted();
            startLevelTransition();
            player.resetLevelEnd();
        }
    }

    private void updateTransition() {
        if (scalingUp) {
            transitionScale += TRANSITION_SPEED;

            if (transitionScale >= 2f) {
                transitionScale = 2f;

                if (!isLevelLoaded) {
                    levelManager.setLevelScore(player.getDeathCount());
                    player.resetDeathCount();
                    levelManager.loadNextLevel();
                    player.resetLevelEnd();

                    isLevelLoaded = true;
                    notifyLevelLoadRequested();
                }

                scalingUp = false;
            }
        } else {
            transitionScale -= TRANSITION_SPEED;

            if (transitionScale <= 0f) {
                transitionScale = 0f;
                inTransition = false;
                notifyTransitionComplete();
            }
        }
    }

    // Transition ---------------------------------

    public void startLevelTransition() {
        inTransition = true;
        scalingUp = true;
        isLevelLoaded = false;
        transitionScale = 0f;
    }

    public void resetTransition() {
        inTransition = false;
        scalingUp = true;
        isLevelLoaded = false;
        transitionScale = 0f;
    }

    // Statistics ---------------------------------

    public void resetStats() {
        totalDeaths = 0;
        startTime = 0L;
    }

    public void startNewTimer() {
        totalDeaths = 0;
        startTime = System.nanoTime();
    }

    public void incrementTotalDeathsForRun() {
        totalDeaths++;
    }

    // Game state ---------------------------------

    public void togglePause() {
        if (isActive && !inTransition) {
            isPaused = !isPaused;
        }
    }

    public void onEnterMenuFromPlaying() {
        resetTransition();
        resetStats();
        levelManager.resetToFirstLevel();
        reloadPlayerForCurrentLevel();
    }

    public void onEnterPlayingFromMenu() {
        resetTransition();
        resetStats();
        startNewTimer();
    }

    public void onEnterPlayingFromLevelSelect() {
        resetTransition();
        resetStats();
        reloadPlayerForCurrentLevel();
        startNewTimer();
    }

    private void reloadPlayerForCurrentLevel() {
        Level currentLevel = levelManager.getCurrentLvl();

        player.setSpawnPoint(
                currentLevel.getSpawnX(),
                currentLevel.getSpawnY());

        player.loadLvlData(currentLevel.getLevelData());
        player.setCurrentLevel(currentLevel);
        player.spawnAtLevelStart();

        currentLevel.resetPlatforms();
        currentLevel.clearDeathPositions();
    }

    // Scoring ------------------------------------

    public void recordLevelCompletion() {
        long runEndTimeNanos = System.nanoTime();

        double timeSeconds =
                (runEndTimeNanos - startTime) / 1_000_000_000.0;

        int levelIndex = levelManager.getCurrentLevelIndex();

        LoadSave.appendToScoreFile(
                playerName,
                levelIndex,
                timeSeconds,
                totalDeaths);
    }

    // Edge flags ---------------------------------

    public void resetEdgeFlags() {
        isDead = false;
        isRespawn = false;
        isEndOfLevel = false;
    }

    public boolean checkIsDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean checkIsRespawn() {
        return isRespawn;
    }

    public void setRespawn(boolean respawn) {
        isRespawn = respawn;
    }

    public boolean checkIsEndOfLevel() {
        return isEndOfLevel;
    }

    public void setEndOfLevel(boolean endOfLevel) {
        isEndOfLevel = endOfLevel;
    }

    // Getters and setters ------------------------

    public Player getPlayer() {
        return player;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isInTransition() {
        return inTransition;
    }

    public void setInTransition(boolean inTransition) {
        this.inTransition = inTransition;
    }

    public float getTransitionScale() {
        return transitionScale;
    }

    public void setTransitionScale(float transitionScale) {
        this.transitionScale = transitionScale;
    }

    public boolean isScalingUp() {
        return scalingUp;
    }

    public void setScalingUp(boolean scalingUp) {
        this.scalingUp = scalingUp;
    }

    public boolean isLevelLoaded() {
        return isLevelLoaded;
    }

    public void setLevelLoaded(boolean levelLoaded) {
        isLevelLoaded = levelLoaded;
    }

    public float getTransitionSpeed() {
        return TRANSITION_SPEED;
    }

    public boolean wasPlayerDead() {
        return wasPlayerDead;
    }

    public void setWasPlayerDead(boolean wasPlayerDead) {
        this.wasPlayerDead = wasPlayerDead;
    }

    public long getRunStartTimeNanos() {
        return startTime;
    }

    public int getTotalDeathsForRun() {
        return totalDeaths;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        if (name != null && !name.isBlank()) {
            this.playerName = name.trim();
        }
    }

    public void setGameActive(boolean isPlaying) {
        this.isActive = isPlaying;
    }

}
