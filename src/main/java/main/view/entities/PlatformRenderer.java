package main.view.entities;

import java.awt.Graphics;

import main.Game;
import main.model.entities.PlatformModel;

/**
 * Renderer for Platform entity.
 * Handles all rendering logic for platforms.
 */
public class PlatformRenderer {

    private final PlatformModel model;

    public PlatformRenderer(PlatformModel model) {
        this.model = model;
    }

    /**
     * Renders the platform
     */
    public void render(Graphics g) {
        int tileSize = Game.TILES_SIZE;

        // Sprite area is centered in the hitbox (hitbox is 1.5x the sprite area)
        float spriteAreaX = model.getHitbox().x + model.getHitbox().width / 6;
        float spriteAreaY = model.getHitbox().y + model.getHitbox().height / 6;

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
            g.drawImage(model.getTileSprites().get(i), tileX, tileY, tileSize, tileSize, null);
        }
    }

    /**
     * Optional debug rendering of hitbox
     */
    public void renderHitbox(Graphics g) {
        g.setColor(java.awt.Color.RED);
        g.drawRect((int) model.getHitbox().x, (int) model.getHitbox().y,
                  (int) model.getHitbox().width, (int) model.getHitbox().height);
    }
}
