package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class MovingPlatform extends Entity {

    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float speed;
    private boolean movingToEnd = true;
    private BufferedImage sprite;

    public MovingPlatform(float startX, float startY, float endX, float endY,
                          int width, int height, float speed, BufferedImage sprite) {
        super(startX, startY, width, height);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.speed = speed;
        this.sprite = sprite;
        initHitbox(startX, startY, width, height);
    }

    public void update() {
        float targetX = movingToEnd ? endX : startX;
        float targetY = movingToEnd ? endY : startY;

        float dirX = targetX - hitbox.x;
        float dirY = targetY - hitbox.y;
        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        if (distance < speed) {
            // Reached target, switch direction
            hitbox.x = targetX;
            hitbox.y = targetY;
            movingToEnd = !movingToEnd;
        } else {
            // Move towards target
            hitbox.x += (dirX / distance) * speed;
            hitbox.y += (dirY / distance) * speed;
        }

        x = hitbox.x;
        y = hitbox.y;
    }

    public void render(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) hitbox.x, (int) hitbox.y, width, height, null);
        } else {
            g.setColor(java.awt.Color.GRAY);
            g.fillRect((int) hitbox.x, (int) hitbox.y, width, height);
        }
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }
}
