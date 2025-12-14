package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import main.controller.entities.SpikeController;
import main.model.entities.SpikeModel;
import main.view.entities.SpikeRenderer;

public class TriggerSpike extends Entity {

    // MVC Architecture Components
    private SpikeModel model;
    private SpikeController controller;
    private SpikeRenderer renderer;

    public TriggerSpike(float x, float y, float targetX, float targetY, int width, int height,
                        float speed, float triggerDistance, BufferedImage sprite, boolean shouldReturn, int id,
                        int collisionWidth, int collisionHeight) {
        super(x, y, width, height);

        // Initialize MVC components
        model = new SpikeModel(x, y, targetX, targetY, width, height, speed, triggerDistance,
                              sprite, shouldReturn, id, collisionWidth, collisionHeight);
        controller = new SpikeController(model);
        renderer = new SpikeRenderer(model);
    }

    public int getId() {
        return controller.getId();
    }

    public void update() {
        controller.update();
    }

    public void render(Graphics g) {
        renderer.render(g);
    }

    public boolean checkTriggerDistance(Entity player) {
        return controller.checkTriggerDistance(player);
    }

    public boolean checkPlayerCollision(Entity player) {
        return model.getHitbox().intersects(player.getHitbox());
    }

    public void trigger() {
        controller.trigger();
    }

    public boolean isTriggered() {
        return controller.isTriggered();
    }

    public void reset() {
        model.getHitbox().x = model.getStartX();
        model.getHitbox().y = model.getStartY();
        model.setTriggered(false);
        model.setReachedTarget(false);
        model.setMovingToTarget(true);
        model.setWaitingAtTarget(false);
    }

    @Override
    public Rectangle2D.Float getHitbox() {
        return model.getHitbox();
    }
}

