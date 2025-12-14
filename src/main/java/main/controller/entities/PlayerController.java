package main.controller.entities;

import java.awt.image.BufferedImage;

import Levels.Level;
import entities.Entity;
import main.model.entities.PlayerModel;
import main.observerEvents.PlayerEventListener;
import static utilz.Constants.PlayerConstants.IDLE_LEFT;
import static utilz.Constants.PlayerConstants.IDLE_RIGHT;
import static utilz.Constants.PlayerConstants.JUMPING_LEFT;
import static utilz.Constants.PlayerConstants.JUMPING_RIGHT;
import static utilz.Constants.PlayerConstants.RUNNING_LEFT;
import static utilz.Constants.PlayerConstants.RUNNING_RIGHT;
import static utilz.Constants.PlayerConstants.getSpriteAmount;
import static utilz.HelpMethods.canMoveHere;
import static utilz.HelpMethods.getEntityXPosNextToWall;
import static utilz.HelpMethods.getEntityYPosUnderOrAbove;
import static utilz.HelpMethods.isEntityDead;
import static utilz.HelpMethods.isEntityOnFloor;
import static utilz.HelpMethods.isOnLevelEnd;

public class PlayerController {

    private final PlayerModel model;
    private Level currentLevel;
    private PlayerEventListener playerEventListener;

    public PlayerController(PlayerModel model) {
        this.model = model;
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    public void setPlayerEventListener(PlayerEventListener listener) {
        this.playerEventListener = listener;
    }

    /**
     * Main update method for player - handles all player logic
     */
    public void update() {
        if (model.isDead()) {
            if (System.currentTimeMillis() - model.getDeathTime() >= model.getRespawnDelayMs()) {
                respawn();
            }
            return;
        }

        // Create a temporary player-like entity for collision checking
        Entity tempPlayer = new Entity(model.getHitbox().x, model.getHitbox().y,
                                     (int) model.getHitbox().width, (int) model.getHitbox().height) {
            @Override
            public java.awt.geom.Rectangle2D.Float getHitbox() {
                return model.getHitbox();
            }
        };

        if (isEntityDead(model.getHitbox(), model.getLvlData())
                || (currentLevel != null && currentLevel.checkSpikeCollision(tempPlayer))
                || (currentLevel != null && currentLevel.checkTriggerSpikeCollision(tempPlayer))) {
            die();
            return;
        }

        if (isOnLevelEnd(model.getHitbox(), model.getLvlData())) {
            model.setReachedLevelEnd(true);
        }

        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    /**
     * Handles player death
     */
    private void die() {
        model.setCurrDeathCount(model.getCurrDeathCount() + 1);
        if (playerEventListener != null) {
            playerEventListener.onPlayerDeath();
        }
        model.setDead(true);
        model.setDeathTime(System.currentTimeMillis());

        // Record death position before moving player off screen
        if (currentLevel != null) {
            BufferedImage deathSprite = utilz.LoadSave.getSpriteAtlas(utilz.LoadSave.PLAYER_DEAD);
            currentLevel.recordDeathPosition(model.getHitbox().x - model.getXDrawOffset(),
                                           model.getHitbox().y - model.getYDrawOffset(), deathSprite);
        }

        model.getHitbox().x = 2000;
        model.getHitbox().y = 2000;
        resetInAir();
        resetDirBooleans();
    }

    /**
     * Handles player respawn
     */
    private void respawn() {
        model.setDead(false);
        model.setHitboxPosition(model.getSpawnX(), model.getSpawnY());

        resetDirBooleans();
        model.setAirSpeed(0);
        model.setMoving(false);
        model.setJump(false);

        model.setInAir(!isEntityOnFloor(model.getHitbox(), model.getLvlData()));
    }

    /**
     * Updates player position and physics
     */
    private void updatePos() {
        if (model.isJump()) {
            jump();
        }

        // Always check if player is on ground or solid platform
        if (!model.isInAir()) {
            if (!isEntityOnFloor(model.getHitbox(), model.getLvlData()) &&
                (currentLevel == null || !currentLevel.isOnSolidPlatform(model.getHitbox()))) {
                model.setInAir(true);
            }
        }

        float xSpeed = 0;
        model.setMoving(false);

        if (model.isLeft()) {
            xSpeed -= model.getPlayerSpeed();
            model.setMoving(true);
        }
        if (model.isRight()) {
            xSpeed += model.getPlayerSpeed();
            model.setMoving(true);
        }

        if (model.isInAir()) {
            if (canMoveHere(model.getHitbox().x, model.getHitbox().y + model.getAirSpeed(),
                           model.getHitbox().width, model.getHitbox().height, model.getLvlData())) {
                model.getHitbox().y += model.getAirSpeed();
                model.setAirSpeed(model.getAirSpeed() + model.getGravity());
                updateXPos(xSpeed);

                // Check if landed on a solid (moving) trigger platform while falling
                if (currentLevel != null && model.getAirSpeed() > 0) {
                    float platformY = currentLevel.getSolidPlatformY(model.getHitbox(), model.getAirSpeed());
                    if (platformY >= 0) {
                        model.getHitbox().y = platformY;
                        resetInAir();
                    }
                }
            } else {
                model.getHitbox().y = getEntityYPosUnderOrAbove(model.getHitbox(), model.getAirSpeed());
                if (model.getAirSpeed() > 0) {
                    resetInAir();
                } else {
                    model.setAirSpeed(model.getFallSpeedAfterCollision());
                }
                updateXPos(xSpeed);
            }
        } else {
            updateXPos(xSpeed);
        }

        model.setMoving(model.isLeft() || model.isRight());
    }

    /**
     * Updates horizontal position
     */
    private void updateXPos(float xSpeed) {
        if (canMoveHere(model.getHitbox().x + xSpeed, model.getHitbox().y,
                       model.getHitbox().width, model.getHitbox().height, model.getLvlData())) {
            model.getHitbox().x += xSpeed;
        } else {
            model.getHitbox().x = getEntityXPosNextToWall(model.getHitbox(), xSpeed);
        }
    }

    /**
     * Handles jumping
     */
    private void jump() {
        if (model.isInAir()) {
            return;
        }
        model.setAirSpeed(model.getJumpSpeed());
        model.setInAir(true);
    }

    /**
     * Updates animation tick
     */
    private void updateAnimationTick() {
        model.setAniTick(model.getAniTick() + 1);
        if (model.getAniTick() >= model.getAniSpeed()) {
            model.setAniTick(0);
            model.setAniIndex(model.getAniIndex() + 1);
            if (model.getAniIndex() >= getSpriteAmount(model.getPlayerAction())) {
                model.setAniIndex(0);
            }
        }
    }

    /**
     * Sets appropriate animation based on player state
     */
    private void setAnimation() {
        int startAni = model.getPlayerAction();

        if (model.isMoving()) {
            if (model.isLeft()) {
                model.setPlayerAction(RUNNING_LEFT);
                model.setFacingRight(false);
            } else if (model.isRight()) {
                model.setFacingRight(true);
                model.setPlayerAction(RUNNING_RIGHT);
            }
        } else {
            if (!model.isFacingRight()) {
                model.setPlayerAction(IDLE_LEFT);
            } else {
                model.setPlayerAction(IDLE_RIGHT);
            }
        }

        if (model.isJump()) {
            if (!model.isFacingRight()) {
                model.setPlayerAction(JUMPING_LEFT);
            } else {
                model.setPlayerAction(JUMPING_RIGHT);
            }
        }

        if (startAni != model.getPlayerAction()) {
            resetAniTick();
        }
    }

    /**
     * Resets animation tick
     */
    private void resetAniTick() {
        model.setAniTick(0);
        model.setAniIndex(0);
    }

    /**
     * Resets in-air state
     */
    private void resetInAir() {
        model.setInAir(false);
        model.setAirSpeed(0);
    }

    /**
     * Resets direction booleans
     */
    private void resetDirBooleans() {
        model.setLeft(false);
        model.setRight(false);
    }


    // Delegate methods for external access
    public boolean hasReachedLevelEnd() {
        return model.hasReachedLevelEnd();
    }

    public void resetLevelEnd() {
        model.setReachedLevelEnd(false);
    }

    public void resetDeathCount() {
        model.setCurrDeathCount(0);
    }

    public int getDeathCount() {
        return model.getCurrDeathCount();
    }

    public PlayerModel getModel() {
        return model;
    }
}
