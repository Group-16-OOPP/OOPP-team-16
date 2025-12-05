package main.model;

import Levels.LevelManager;
import entities.Player;
import main.Game.GameState;

//ONLY put state and rules, meaning no rendering, no input. KEEP THIS ADHERANCE!
public class GameModel {

    private final Player player;
    private final LevelManager levelManager;

    private GameState gameState = GameState.MENU;

    // Level transition
    private boolean inTransition = false;
    private float transitionScale = 2f;
    private boolean scalingUp = true;
    private boolean isLevelLoaded = false;
    private final float TRANSITION_SPEED = 0.015f;

    private boolean wasPlayerDead = false;
    private boolean isDead = false;
    private boolean isRespawn = false;
    private boolean isEndOfLevel = false;

    // Player name and run stats
    private String playerName = "Player1";
    private long runStartTimeNanos;
    private int totalDeathsForRun;

    private boolean paused = false;

    public GameModel(Player player, LevelManager levelManager) {
        this.player = player;
        this.levelManager = levelManager;
    }

    public Player getPlayer() {
        return player;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        if (gameState == GameState.PLAYING && !inTransition) {
            paused = !paused;
        }
    }

    public boolean isInTransition() {
        return inTransition;
    }

    public float getTransitionScale() {
        return transitionScale;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        if (name != null && !name.isBlank()) {
            this.playerName = name.trim();
        }
    }

    public long getRunStartTimeNanos() {
        return runStartTimeNanos;
    }

    public int getTotalDeathsForRun() {
        return totalDeathsForRun;
    }


    //TODO are these duplicates or am I tripping?... 2 am here
    public void resetRunStats() {
        totalDeathsForRun = 0;
        runStartTimeNanos = 0L;
    }

    public void startNewRunTimer() {
        totalDeathsForRun = 0;
        runStartTimeNanos = System.nanoTime();
    }

    //TODO add to event listener, fine line between what is model vs controller behaviour....
    public void onPlayerDeath() {
        totalDeathsForRun++;
    }


    //TODO same here..
    public void startLevelTransition() {
        inTransition = true;
        scalingUp = true;
        isLevelLoaded = false;
        transitionScale = 0f;
    }

    //True only on the frame where the player has just transitioned from alive to dead
    public boolean checkIsDead() {
        return isDead;
    }

    //True only on the frame where the player has just transitioned from dead back to alive
    public boolean checkIsRespawn() {
        return isRespawn;
    }

    //True only on the frame where the player has just reached the level end
    public boolean checkIsEndOfLevel() {
        return isEndOfLevel;
    }

    public void updatePlaying() {
        if (paused) {
            return;
        }

        // Reset edge flags at the start of the frame
        isDead = false;
        isRespawn = false;
        isEndOfLevel = false;

        boolean isPlayerDead = player.getHitbox().x > 1500;
        if (!wasPlayerDead && isPlayerDead) {
            // Player has just died this frame
            isDead = true;
        }
        if (wasPlayerDead && !isPlayerDead) {
            // Player has just respawned this frame
            isRespawn = true;
        }
        wasPlayerDead = isPlayerDead;

        player.update();
        levelManager.update();

        if (player.hasReachedLevelEnd()) {
            isEndOfLevel = true;
            startLevelTransition();
            player.resetLevelEnd();
        }
    }

    public void updateTransition() {
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
                }
                scalingUp = false;
            }
        } else {
            transitionScale -= TRANSITION_SPEED;
            if (transitionScale <= 0f) {
                transitionScale = 0f;
                inTransition = false;
            }
        }
    }
}
