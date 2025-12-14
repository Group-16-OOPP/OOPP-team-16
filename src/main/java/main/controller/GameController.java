package main.controller;

import Levels.LevelManager;
import entities.Player;
import main.model.GameModel;

public class GameController {

    private final GameModel model;
    private final Player player;
    private final LevelManager levelManager;

    public GameController(GameModel model, Player player, LevelManager levelManager) {
        this.model = model;
        this.player = player;
        this.levelManager = levelManager;
    }

    /**
     * Updates the playing state - handles game logic and rules
     */
    public void updatePlaying() {
        if (model.isPaused()) {
            return;
        }

        // Reset edge flags at the start of the frame
        model.resetEdgeFlags();

        // Check player death state
        boolean isPlayerDead = player.getHitbox().x > 1500;
        if (!model.wasPlayerDead() && isPlayerDead) {
            // Player has just died this frame
            model.setDead(true);
        }
        if (model.wasPlayerDead() && !isPlayerDead) {
            // Player has just respawned this frame
            model.setRespawn(true);
        }
        model.setWasPlayerDead(isPlayerDead);

        player.update();
        levelManager.update();

        if (player.hasReachedLevelEnd()) {
            model.setEndOfLevel(true);
            startLevelTransition();
            player.resetLevelEnd();
        }
    }

    /**
     * Updates the level transition animation and logic
     */
    public void updateTransition() {
        if (model.isScalingUp()) {
            model.setTransitionScale(model.getTransitionScale() + model.getTransitionSpeed());
            if (model.getTransitionScale() >= 2f) {
                model.setTransitionScale(2f);
                if (!model.isLevelLoaded()) {
                    levelManager.setLevelScore(player.getDeathCount());
                    player.resetDeathCount();
                    levelManager.loadNextLevel();
                    player.resetLevelEnd();
                    model.setLevelLoaded(true);
                }
                model.setScalingUp(false);
            }
        } else {
            model.setTransitionScale(model.getTransitionScale() - model.getTransitionSpeed());
            if (model.getTransitionScale() <= 0f) {
                model.setTransitionScale(0f);
                model.setInTransition(false);
            }
        }
    }

    /**
     * Handles player death event - increments death counter
     */
    public void onPlayerDeath() {
        model.incrementTotalDeathsForRun();
    }

    /**
     * Starts the level transition animation
     */
    public void startLevelTransition() {
        model.setInTransition(true);
        model.setScalingUp(true);
        model.setLevelLoaded(false);
        model.setTransitionScale(0f);
    }

    // Delegate methods for checking game state
    public boolean checkIsDead() {
        return model.checkIsDead();
    }

    public boolean checkIsRespawn() {
        return model.checkIsRespawn();
    }

    public boolean checkIsEndOfLevel() {
        return model.checkIsEndOfLevel();
    }
}
