package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Spike extends Entity {
    
    private BufferedImage sprite;
    private int spriteWidth;
    private int spriteHeight;
    
    public Spike(float x, float y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height);
        this.sprite = sprite;
        this.spriteWidth = width;
        this.spriteHeight = height;
        // Hitbox is half height, positioned at bottom of sprite
        initHitbox(x, y + height / 2, width, height / 2);
    }
    
    public void render(Graphics g) {
        // Draw sprite at top of hitbox (since hitbox is bottom half)
        int spriteY = (int) (hitbox.y - spriteHeight / 2);
        if (sprite != null) {
            g.drawImage(sprite, (int) hitbox.x, spriteY, spriteWidth, spriteHeight, null);
        } else {
            g.setColor(java.awt.Color.RED);
            g.fillRect((int) hitbox.x, spriteY, spriteWidth, spriteHeight);
        }
    }
    
    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }
    
    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }
}

