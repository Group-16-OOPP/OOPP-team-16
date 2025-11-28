package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TriggerSpike extends Entity {
    
    private float startX, startY;
    private float targetX, targetY;
    private float speed;
    private BufferedImage sprite;
    private boolean triggered = false;
    private boolean reachedTarget = false;
    private float triggerDistance;
    private boolean shouldReturn;
    private boolean movingToTarget = true;
    private boolean waitingAtTarget = false;
    private long waitStartTime;
    private long waitDurationMs = 500;
    
    public TriggerSpike(float x, float y, float targetX, float targetY, int width, int height, 
                        float speed, float triggerDistance, BufferedImage sprite, boolean shouldReturn) {
        super(x, y, width, height);
        this.startX = x;
        this.startY = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.speed = speed;
        this.triggerDistance = triggerDistance;
        this.sprite = sprite;
        this.shouldReturn = shouldReturn;
        initHitbox(x, y, width, height / 2);
    }
    
    public void update() {
        if (!triggered || reachedTarget) {
            return;
        }
        
        if (waitingAtTarget) {
            if (System.currentTimeMillis() - waitStartTime >= waitDurationMs) {
                waitingAtTarget = false;
                movingToTarget = false;
            }
            return;
        }
        
        float destX = movingToTarget ? targetX : startX;
        float destY = movingToTarget ? targetY : startY;
        
        float dx = destX - hitbox.x;
        float dy = destY - hitbox.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (dist <= speed) {
            hitbox.x = destX;
            hitbox.y = destY;
            
            if (movingToTarget && shouldReturn) {
                waitingAtTarget = true;
                waitStartTime = System.currentTimeMillis();
            } else {
                reachedTarget = true;
            }
        } else {
            hitbox.x += (dx / dist) * speed;
            hitbox.y += (dy / dist) * speed;
        }
    }
    
    public void render(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) hitbox.x, (int) (hitbox.y - hitbox.height), 
                       (int) hitbox.width, (int) (hitbox.height * 2), null);
        } else {
            g.setColor(java.awt.Color.MAGENTA);
            g.fillRect((int) hitbox.x, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);
        }
    }
    
    public boolean checkTriggerDistance(Entity player) {
        float px = player.getHitbox().x + player.getHitbox().width / 2;
        float py = player.getHitbox().y + player.getHitbox().height / 2;
        float sx = hitbox.x + hitbox.width / 2;
        float sy = hitbox.y + hitbox.height / 2;
        
        float dist = (float) Math.sqrt((px - sx) * (px - sx) + (py - sy) * (py - sy));
        return dist <= triggerDistance;
    }
    
    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }
    
    public void trigger() {
        triggered = true;
    }
    
    public boolean isTriggered() {
        return triggered;
    }
    
    public void reset() {
        hitbox.x = startX;
        hitbox.y = startY;
        triggered = false;
        reachedTarget = false;
        movingToTarget = true;
        waitingAtTarget = false;
    }
}

