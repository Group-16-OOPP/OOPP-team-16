package main.controller.entities;

import entities.Entity;
import main.model.entities.PlatformModel;


public class PlatformController {

    private final PlatformModel model;

    public PlatformController(PlatformModel model) {
        this.model = model;
    }

    /**
     * Main update method for platform - handles movement logic
     */
    public void update() {
        // If looping, start immediately without trigger
        if (model.isLoop() && !model.isTriggered()) {
            model.setTriggered(true);
        }

        if (!model.isTriggered() || model.isReachedTarget()) {
            return;
        }

        // If waiting at target, check if wait is over
        if (model.isWaitingAtTarget()) {
            if (System.currentTimeMillis() - model.getWaitStartTime() >= model.getWaitDurationMs()) {
                model.setWaitingAtTarget(false);
                model.setMovingToTarget(!model.isMovingToTarget()); // Toggle direction
            }
            return;
        }

        // Determine current destination
        float destX = model.isMovingToTarget() ? model.getTargetX() : model.getStartX();
        float destY = model.isMovingToTarget() ? model.getTargetY() : model.getStartY();

        float dirX = destX - model.getHitbox().x;
        float dirY = destY - model.getHitbox().y;
        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        if (distance < model.getSpeed()) {
            // Reached destination
            model.getHitbox().x = destX;
            model.getHitbox().y = destY;

            if (model.isLoop()) {
                // If looping, wait then reverse direction
                model.setWaitingAtTarget(true);
                model.setWaitStartTime(System.currentTimeMillis());
            } else if (model.isMovingToTarget() && model.isShouldReturn()) {
                // Start waiting at target
                model.setWaitingAtTarget(true);
                model.setWaitStartTime(System.currentTimeMillis());
                model.setMovingToTarget(false); // Next move is returning
            } else {
                // Done moving (unless it was returning and loop is false)
                if (!model.isMovingToTarget() && model.isShouldReturn()) {
                    // Returned to start, stop
                    model.setReachedTarget(true);
                } else if (model.isMovingToTarget() && !model.isShouldReturn()) {
                    // Reached target, no return
                    model.setReachedTarget(true);
                }
            }
        } else {
            // Move towards destination
            model.moveHitbox((dirX / distance) * model.getSpeed(), (dirY / distance) * model.getSpeed());
        }
    }

    /**
     * Triggers the platform movement
     */
    public void trigger() {
        if (!model.isTriggered()) { // Only play sound on first trigger
            model.setTriggered(true);
            // Audio playing would be handled by a separate audio controller
        }
    }

    /**
     * Checks if player is touching this platform
     */
    public boolean checkPlayerCollision(Entity player) {
        return model.getHitbox().intersects(player.getHitbox());
    }

    /**
     * Gets the sprite hitbox for collision detection
     */
    public java.awt.geom.Rectangle2D.Float getSpriteHitbox() {
        float spriteAreaW = model.getHitbox().width * 2 / 3;
        float spriteAreaH = model.getHitbox().height * 2 / 3;
        float spriteAreaX = model.getHitbox().x + model.getHitbox().width / 6;
        float spriteAreaY = model.getHitbox().y + model.getHitbox().height / 6;

        return new java.awt.geom.Rectangle2D.Float(spriteAreaX, spriteAreaY, spriteAreaW, spriteAreaH);
    }

    // Delegate methods for external access
    public boolean isTriggered() {
        return model.isTriggered();
    }

    public boolean isSolid() {
        return model.isSolid();
    }

    public PlatformModel getModel() {
        return model;
    }
}
