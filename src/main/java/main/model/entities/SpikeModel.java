package main.model.entities;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class SpikeModel {

    // Position and movement
    private float x;
    private float y;
    private int width;
    private int height;
    private Rectangle2D.Float hitbox;

    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private float speed;

    // Movement state
    private boolean triggered = false;
    private boolean reachedTarget = false;
    private boolean movingToTarget = true;
    private boolean waitingAtTarget = false;
    private long waitStartTime;
    private long waitDurationMs = 500;

    // Trigger properties
    private float triggerDistance;
    private boolean shouldReturn;
    private int id = -1; // Default ID means no group

    // Sprite data
    private BufferedImage sprite;

    public SpikeModel(float x, float y, float targetX, float targetY, int width, int height,
                     float speed, float triggerDistance, BufferedImage sprite, boolean shouldReturn,
                     int id, int collisionWidth, int collisionHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.startX = x;
        this.startY = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.triggerDistance = triggerDistance;
        this.sprite = sprite;
        this.shouldReturn = shouldReturn;
        this.id = id;

        // Calculate centered collision box relative to tile
        int xOffset = (width - collisionWidth) / 2;
        int yOffset = (height - collisionHeight) / 2;
        this.hitbox = new Rectangle2D.Float(x + xOffset, y + yOffset, collisionWidth, collisionHeight);
    }

    // Getters and setters for all state data

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public Rectangle2D.Float getHitbox() { return hitbox; }
    public void setHitbox(Rectangle2D.Float hitbox) { this.hitbox = hitbox; }

    public float getStartX() { return startX; }
    public void setStartX(float startX) { this.startX = startX; }

    public float getStartY() { return startY; }
    public void setStartY(float startY) { this.startY = startY; }

    public float getTargetX() { return targetX; }
    public void setTargetX(float targetX) { this.targetX = targetX; }

    public float getTargetY() { return targetY; }
    public void setTargetY(float targetY) { this.targetY = targetY; }

    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public boolean isTriggered() { return triggered; }
    public void setTriggered(boolean triggered) { this.triggered = triggered; }

    public boolean isReachedTarget() { return reachedTarget; }
    public void setReachedTarget(boolean reachedTarget) { this.reachedTarget = reachedTarget; }

    public boolean isMovingToTarget() { return movingToTarget; }
    public void setMovingToTarget(boolean movingToTarget) { this.movingToTarget = movingToTarget; }

    public boolean isWaitingAtTarget() { return waitingAtTarget; }
    public void setWaitingAtTarget(boolean waitingAtTarget) { this.waitingAtTarget = waitingAtTarget; }

    public long getWaitStartTime() { return waitStartTime; }
    public void setWaitStartTime(long waitStartTime) { this.waitStartTime = waitStartTime; }

    public long getWaitDurationMs() { return waitDurationMs; }
    public void setWaitDurationMs(long waitDurationMs) { this.waitDurationMs = waitDurationMs; }

    public float getTriggerDistance() { return triggerDistance; }
    public void setTriggerDistance(float triggerDistance) { this.triggerDistance = triggerDistance; }

    public boolean isShouldReturn() { return shouldReturn; }
    public void setShouldReturn(boolean shouldReturn) { this.shouldReturn = shouldReturn; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BufferedImage getSprite() { return sprite; }
    public void setSprite(BufferedImage sprite) { this.sprite = sprite; }

    // Utility methods
    public void moveHitbox(float deltaX, float deltaY) {
        hitbox.x += deltaX;
        hitbox.y += deltaY;
    }
}
