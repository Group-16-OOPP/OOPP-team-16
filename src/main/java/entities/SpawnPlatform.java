package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class SpawnPlatform extends Entity {
    private static final long WAIT_AT_BOTTOM_MS = 300;

    private float startY;
    private float loweredY;
    private float speed;
    private BufferedImage sprite;
    private boolean lowering = false;
    private boolean raising = false;
    private boolean atBottom = false;
    private long atBottomTime;

    public SpawnPlatform(float x, float y, int width, int height, float lowerDistance,
                         float speed, BufferedImage sprite) {

        super(x, y, width, height);
        this.startY = y;
        this.loweredY = y + lowerDistance;
        this.speed = speed;
        this.sprite = sprite;
        initHitbox(x, y, width, height);
    }

    public void triggerSpawn() {
        lowering = true;
        raising = false;
        atBottom = false;
    }

    public void update() {
        if (lowering) {
            hitbox.y += speed;
            if (hitbox.y >= loweredY) {
                hitbox.y = loweredY;
                lowering = false;
                atBottom = true;
                atBottomTime = System.currentTimeMillis();
            }
        } else if (atBottom) {
            if (System.currentTimeMillis() - atBottomTime >= WAIT_AT_BOTTOM_MS) {
                atBottom = false;
                raising = true;
            }
        } else if (raising) {
            hitbox.y -= speed;
            if (hitbox.y <= startY) {
                hitbox.y = startY;
                raising = false;
            }
        }
    }

    public boolean hasReachedBottom() {
        return atBottom || raising;
    }

    public void render(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height, null);
        } else {
            g.setColor(java.awt.Color.CYAN);
            g.fillRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
        }
    }

    public boolean isAnimating() {
        return lowering || atBottom || raising;
    }

    public void reset() {
        hitbox.y = startY;
        lowering = false;
        raising = false;
        atBottom = false;
    }
}

