package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.controller.AudioController;
import main.controller.entities.PlatformController;
import main.model.entities.PlatformModel;
import main.view.entities.PlatformRenderer;

public class TriggerPlatform extends Entity {

    // MVC Architecture Components
    private PlatformModel model;
    private PlatformController controller;
    private PlatformRenderer renderer;

    // Legacy support - minimal fields for backward compatibility
    private AudioController audioController;

    public TriggerPlatform(float x, float y, float targetX, float targetY,
                           int width, int height, float speed, BufferedImage sprite,
                           boolean shouldReturn) {

        super(x, y, width, height);

        // Initialize MVC components
        model = new PlatformModel(x, y, targetX, targetY, width, height, speed, sprite, shouldReturn);
        controller = new PlatformController(model);
        renderer = new PlatformRenderer(model);

        // Initialize hitbox through model
        model.getHitbox().setRect(x, y, width, height);
    }

    // Set the offset of the first tile sprite within the bounding box
    public void setFirstTileOffset(float offsetX, float offsetY) {
        model.setFirstTileOffsetX(offsetX);
        model.setFirstTileOffsetY(offsetY);
    }

    // Add a tile to this platform (position relative to bounding box top-left)
    public void addTile(float relX, float relY, BufferedImage tileSprite) {
        model.addTile(relX, relY, tileSprite);
    }

    public void setLoop(boolean loop) {
        model.setLoop(loop);
    }

    public void update() {
        controller.update();
    }

    public void render(Graphics g) {
        renderer.render(g);
        // Optional debug hitbox rendering
        // renderer.renderHitbox(g);
    }

    // Check if player is touching this platform
    public boolean checkPlayerCollision(Entity player) {
        return controller.checkPlayerCollision(player);
    }

    public void trigger() {
        controller.trigger();
        // Handle audio if needed
        if (audioController != null && !model.isTriggered()) {
            audioController.playPlatformSound();
        }
    }

    public boolean isTriggered() {
        return controller.isTriggered();
    }

    public boolean hasReachedTarget() {
        return model.isReachedTarget();
    }

    public void reset() {
        model.getHitbox().x = model.getStartX();
        model.getHitbox().y = model.getStartY();
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
        float offsetX = newX - model.getHitbox().x;
        float offsetY = newY - model.getHitbox().y;

        model.getHitbox().width = hitboxWidth;
        model.getHitbox().height = hitboxHeight;
        model.getHitbox().x = newX;
        model.getHitbox().y = newY;
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
        return controller.isSolid();
    }

    public void setAudioController(AudioController audioController) {
        this.audioController = audioController;
    }

    // Get the sprite hitbox (the actual collidable area for standing)
    public java.awt.geom.Rectangle2D.Float getSpriteHitbox() {
        // Sprite area is centered in hitbox (hitbox is 1.5x sprite area)
        // Sprite = hitbox / 1.5 = hitbox * 2/3, offset = hitbox / 6
        java.awt.geom.Rectangle2D.Float hb = model.getHitbox();
        float spriteAreaW = hb.width * 2 / 3;
        float spriteAreaH = hb.height * 2 / 3;
        float spriteAreaX = hb.x + hb.width / 6;
        float spriteAreaY = hb.y + hb.height / 6;

        return new java.awt.geom.Rectangle2D.Float(spriteAreaX, spriteAreaY, spriteAreaW, spriteAreaH);
    }

    @Override
    public Rectangle2D.Float getHitbox() {
        return model.getHitbox();
    }
}

