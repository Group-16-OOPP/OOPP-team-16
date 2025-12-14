package main.controller.entities;

import entities.Entity;
import main.model.entities.SpikeModel;


public class SpikeController {

    private final SpikeModel model;

    public SpikeController(SpikeModel model) {
        this.model = model;
    }

    /**
     * Main update method for spike - handles movement logic
     */
    public void update() {
        if (!model.isTriggered() || model.isReachedTarget()) {
            return;
        }

        if (model.isWaitingAtTarget()) {
            if (System.currentTimeMillis() - model.getWaitStartTime() >= model.getWaitDurationMs()) {
                model.setWaitingAtTarget(false);
                model.setMovingToTarget(false);
            }
            return;
        }

        float destX = model.isMovingToTarget() ? model.getTargetX() : model.getStartX();
        float destY = model.isMovingToTarget() ? model.getTargetY() : model.getStartY();

        float dx = destX - model.getHitbox().x;
        float dy = destY - model.getHitbox().y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist <= model.getSpeed()) {
            model.getHitbox().x = destX;
            model.getHitbox().y = destY;

            if (model.isMovingToTarget() && model.isShouldReturn()) {
                model.setWaitingAtTarget(true);
                model.setWaitStartTime(System.currentTimeMillis());
            } else {
                model.setReachedTarget(true);
            }
        } else {
            model.moveHitbox((dx / dist) * model.getSpeed(), (dy / dist) * model.getSpeed());
        }
    }

    /**
     * Checks if player is within trigger distance of this spike
     */
    public boolean checkTriggerDistance(Entity player) {
        float px = player.getHitbox().x + player.getHitbox().width / 2;
        float py = player.getHitbox().y + player.getHitbox().height / 2;
        float sx = model.getHitbox().x + model.getHitbox().width / 2;
        float sy = model.getHitbox().y + model.getHitbox().height / 2;

        float dist = (float) Math.sqrt((px - sx) * (px - sx) + (py - sy) * (py - sy));
        return dist <= model.getTriggerDistance();
    }

    /**
     * Triggers the spike movement
     */
    public void trigger() {
        if (!model.isTriggered()) {
            model.setTriggered(true);
        }
    }

    // Delegate methods for external access
    public boolean isTriggered() {
        return model.isTriggered();
    }

    public int getId() {
        return model.getId();
    }

    public SpikeModel getModel() {
        return model;
    }
}
