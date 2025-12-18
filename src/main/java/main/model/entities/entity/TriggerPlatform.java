package main.model.entities.entity;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import audio.controller.AudioController;
import main.controller.Game;
import main.model.entities.states.TriggerPlatformModel;

public class TriggerPlatform extends Entity {

    private final TriggerPlatformModel model;

    //sprite hitbox object to avoid creating garbage
    private final java.awt.geom.Rectangle2D.Float cachedSpriteHitbox = new java.awt.geom.Rectangle2D.Float();

    //Audio controller
    private AudioController audioController;

    public TriggerPlatform(float x, float y, float targetX, float targetY,
                           int width, int height, float speed, BufferedImage sprite,
                           boolean shouldReturn) {

        super(x, y, width, height);
        initHitbox(x, y, width, height);

        this.model = new TriggerPlatformModel(
                hitbox, x, y, targetX, targetY, speed, width, height,
                sprite, shouldReturn
        );
    }

    private static final class Destination {
        final float x;
        final float y;
        final boolean toTarget;

        Destination(float x, float y, boolean toTarget) {
            this.x = x;
            this.y = y;
            this.toTarget = toTarget;
        }
    }

    //offset of the first tile sprite within the box
    public void setFirstTileOffset(float offsetX, float offsetY) {
        model.setFirstTileOffsetX(offsetX);
        model.setFirstTileOffsetY(offsetY);
    }

    //add tile to this platform ,position is the bounding box: TOP LEFT
    public void addTile(float relX, float relY, BufferedImage tileSprite) {
        model.addTile(relX, relY, tileSprite);
    }

    public void setLoop(boolean loop) {
        model.setLoop(loop);
    }

    private void triggerImmediatelyIfLooping() {
        if (model.isLoop() && !model.isTriggered()) {
            model.setTriggered(true);
        }
    }

    private boolean shouldMove() {
        return model.isTriggered() && !model.isReachedTarget();
    }

    private boolean handleWaiting() {
        if (!model.isWaitingAtTarget()) {
            return false;
        }

        long now = System.currentTimeMillis();
        long waitedMs = now - model.getWaitStartTime();
        if (waitedMs >= model.getWaitDurationMs()) {
            model.setWaitingAtTarget(false);
            model.setMovingToTarget(!model.isMovingToTarget());
        }
        return true;
    }

    private Destination getCurrentDestination() {
        boolean toTarget = model.isMovingToTarget();
        float x = toTarget ? model.getTargetX() : model.getStartX();
        float y = toTarget ? model.getTargetY() : model.getStartY();
        return new Destination(x, y, toTarget);
    }

    private void moveOrArrive(float destX, float destY) {
        float dirX = destX - hitbox.x;
        float dirY = destY - hitbox.y;
        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        float speed = model.getSpeed();
        if (distance >= speed) {
            stepTowards(dirX, dirY, distance, speed);
            return;
        }

        snapTo(destX, destY);
        onArrived();
    }

    private void stepTowards(float dirX, float dirY, float distance, float speed) {
        hitbox.x += (dirX / distance) * speed;
        hitbox.y += (dirY / distance) * speed;
    }

    private void snapTo(float x, float y) {
        hitbox.x = x;
        hitbox.y = y;
    }

    private void onArrived() {
        boolean loop = model.isLoop();
        boolean toTarget = model.isMovingToTarget();
        boolean shouldReturn = model.isShouldReturn();

        if (loop) {
            startWait();
            return;
        }

        if (toTarget && shouldReturn) {
            startWait();
            model.setMovingToTarget(false);
            return;
        }

        model.setReachedTarget(true);

        //if (!shouldReturn || !toTarget) {
        //    model.setReachedTarget(true);
    }

    private void startWait() {
        model.setWaitingAtTarget(true);
        model.setWaitStartTime(System.currentTimeMillis());
    }

    // Check if player is touching this platform
    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }

    public void trigger() {
        if (!model.isTriggered()) { // Only play sound on first trigger
            model.setTriggered(true);
            if (audioController != null) {
                audioController.playPlatformSound();
            }
        }
    }

    public boolean isTriggered() {
        return model.isTriggered();
    }

    public void reset() {
        hitbox.x = model.getStartX();
        hitbox.y = model.getStartY();
        model.setTriggered(false);
        model.setReachedTarget(false);
        model.setMovingToTarget(true);
        model.setWaitingAtTarget(false);
    }

    public void setSprite(BufferedImage sprite) {
        model.setSprite(sprite);
    }

    public void setHitboxSize(int hitboxWidth, int hitboxHeight, int newX, int newY) {
        // Calculate offset from old position
        float offsetX = newX - hitbox.x;
        float offsetY = newY - hitbox.y;

        hitbox.width = hitboxWidth;
        hitbox.height = hitboxHeight;
        hitbox.x = newX;
        hitbox.y = newY;

        model.setStartX(newX);
        model.setStartY(newY);

        // Also update target to maintain the same movement offset
        model.setTargetX(model.getTargetX() + offsetX);
        model.setTargetY(model.getTargetY() + offsetY);
    }

    public void setSolid(boolean solid) {
        model.setSolid(solid);
    }

    public boolean isSolid() {
        return model.isSolid();
    }

    public void setAudioController(AudioController audioController) {
        this.audioController = audioController;
    }

    // Get the sprite hitbox (the actual collidable area for standing)
    public java.awt.geom.Rectangle2D.Float getSpriteHitbox() {
        // Sprite area is centered in hitbox (hitbox is 1.5x sprite area)
        // Sprite = hitbox / 1.5 = hitbox * 2/3, offset = hitbox / 6
        float spriteAreaW = hitbox.width * 2f / 3f;
        float spriteAreaH = hitbox.height * 2f / 3f;
        float spriteAreaX = hitbox.x + hitbox.width / 6f;
        float spriteAreaY = hitbox.y + hitbox.height / 6f;

        cachedSpriteHitbox.setRect(spriteAreaX, spriteAreaY, spriteAreaW, spriteAreaH);
        return cachedSpriteHitbox;
    }

    public void update() {
        triggerImmediatelyIfLooping();

        if (!shouldMove()) {
            return;
        }

        if (handleWaiting()) {
            return;
        }

        Destination dest = getCurrentDestination();
        moveOrArrive(dest.x, dest.y);
    }

    public void render(Graphics g) {
        int tileSize = Game.TILES_SIZE;

        // Sprite area is centered in the hitbox (hitbox is 1.5x the sprite area)
        float spriteAreaX = hitbox.x + hitbox.width / 6f;
        float spriteAreaY = hitbox.y + hitbox.height / 6f;

        // Draw first tile sprite at its offset within the sprite area
        int firstX = (int) (spriteAreaX + model.getFirstTileOffsetX());
        int firstY = (int) (spriteAreaY + model.getFirstTileOffsetY());

        if (model.getSprite() != null) {
            g.drawImage(model.getSprite(), firstX, firstY, tileSize, tileSize, null);
        } else {
            g.setColor(java.awt.Color.ORANGE);
            g.fillRect(firstX, firstY, tileSize, tileSize);
        }

        // Draw additional tiles relative to sprite area top-left
        for (int i = 0; i < model.getTilePositions().size(); i++) {
            float[] pos = model.getTilePositions().get(i);
            int tileX = (int) (spriteAreaX + pos[0]);
            int tileY = (int) (spriteAreaY + pos[1]);
            BufferedImage tileSprite = model.getTileSprites().get(i);
            if (tileSprite != null) {
                g.drawImage(tileSprite, tileX, tileY, tileSize, tileSize, null);
            }
        }
        // Uncomment to debug hitbox:
        //g.setColor(java.awt.Color.RED);
        //g.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }
}

