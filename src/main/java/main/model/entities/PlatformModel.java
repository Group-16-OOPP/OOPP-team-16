package main.model.entities;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlatformModel {

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
    private float offsetX;
    private float offsetY;
    private float speed;

    // Movement state
    private boolean triggered = false;
    private boolean reachedTarget = false;
    private boolean movingToTarget = true; // true = moving to target, false = returning to start
    private boolean waitingAtTarget = false;
    private long waitStartTime;
    private long waitDurationMs = 1000; // 1 second wait

    // Platform properties
    private boolean solid = false; // If true, player can stand on this platform
    private boolean loop = false; // If true, platform continuously moves back and forth
    private boolean shouldReturn;

    // Sprite data
    private BufferedImage sprite;
    private int spriteWidth;
    private float spriteHeight;
    private float spriteOffsetX;
    private float spriteOffsetY;

    // Multi-tile platform data
    private List<float[]> tilePositions = new ArrayList<>(); // Relative positions of tiles
    private List<BufferedImage> tileSprites = new ArrayList<>();
    private float firstTileOffsetX = 0;
    private float firstTileOffsetY = 0; // Offset of first tile within bounding box

    // Original sprite bounds (for collision when hitbox is enlarged)
    private float originalX;
    private float originalY;
    private int originalWidth;
    private float originalHeight;

    public PlatformModel(float x, float y, float targetX, float targetY,
                        int width, int height, float speed, BufferedImage sprite,
                        boolean shouldReturn) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle2D.Float(x, y, width, height);

        this.startX = x;
        this.startY = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.offsetX = targetX - x;
        this.offsetY = targetY - y;
        this.speed = speed;
        this.sprite = sprite;
        this.shouldReturn = shouldReturn;
        this.spriteWidth = width;
        this.spriteHeight = height;
        this.originalX = x;
        this.originalY = y;
        this.originalWidth = width;
        this.originalHeight = height;
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

    public float getOffsetX() { return offsetX; }
    public void setOffsetX(float offsetX) { this.offsetX = offsetX; }

    public float getOffsetY() { return offsetY; }
    public void setOffsetY(float offsetY) { this.offsetY = offsetY; }

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

    public boolean isSolid() { return solid; }
    public void setSolid(boolean solid) { this.solid = solid; }

    public boolean isLoop() { return loop; }
    public void setLoop(boolean loop) { this.loop = loop; }

    public boolean isShouldReturn() { return shouldReturn; }
    public void setShouldReturn(boolean shouldReturn) { this.shouldReturn = shouldReturn; }

    public BufferedImage getSprite() { return sprite; }
    public void setSprite(BufferedImage sprite) { this.sprite = sprite; }

    public int getSpriteWidth() { return spriteWidth; }
    public void setSpriteWidth(int spriteWidth) { this.spriteWidth = spriteWidth; }

    public float getSpriteHeight() { return spriteHeight; }
    public void setSpriteHeight(float spriteHeight) { this.spriteHeight = spriteHeight; }

    public float getSpriteOffsetX() { return spriteOffsetX; }
    public void setSpriteOffsetX(float spriteOffsetX) { this.spriteOffsetX = spriteOffsetX; }

    public float getSpriteOffsetY() { return spriteOffsetY; }
    public void setSpriteOffsetY(float spriteOffsetY) { this.spriteOffsetY = spriteOffsetY; }

    public List<float[]> getTilePositions() { return tilePositions; }
    public void setTilePositions(List<float[]> tilePositions) { this.tilePositions = tilePositions; }

    public List<BufferedImage> getTileSprites() { return tileSprites; }
    public void setTileSprites(List<BufferedImage> tileSprites) { this.tileSprites = tileSprites; }

    public float getFirstTileOffsetX() { return firstTileOffsetX; }
    public void setFirstTileOffsetX(float firstTileOffsetX) { this.firstTileOffsetX = firstTileOffsetX; }

    public float getFirstTileOffsetY() { return firstTileOffsetY; }
    public void setFirstTileOffsetY(float firstTileOffsetY) { this.firstTileOffsetY = firstTileOffsetY; }

    public float getOriginalX() { return originalX; }
    public void setOriginalX(float originalX) { this.originalX = originalX; }

    public float getOriginalY() { return originalY; }
    public void setOriginalY(float originalY) { this.originalY = originalY; }

    public int getOriginalWidth() { return originalWidth; }
    public void setOriginalWidth(int originalWidth) { this.originalWidth = originalWidth; }

    public float getOriginalHeight() { return originalHeight; }
    public void setOriginalHeight(float originalHeight) { this.originalHeight = originalHeight; }

    // Utility methods
    public void addTile(float relX, float relY, BufferedImage tileSprite) {
        tilePositions.add(new float[]{relX, relY});
        tileSprites.add(tileSprite);
    }

    public void moveHitbox(float deltaX, float deltaY) {
        hitbox.x += deltaX;
        hitbox.y += deltaY;
    }
}
