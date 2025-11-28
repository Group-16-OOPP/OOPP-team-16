package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TriggerPlatform extends Entity {
    
    private float startX, startY;
    private float targetX, targetY;
    private float offsetX, offsetY; // Movement offset
    private float speed;
    private boolean triggered = false;
    private boolean reachedTarget = false;
    private BufferedImage sprite;
    private boolean shouldReturn;
    private boolean movingToTarget = true; // true = moving to target, false = returning to start
    private int spriteWidth, spriteHeight; // Original sprite size
    private float spriteOffsetX, spriteOffsetY; // Offset to center sprite in hitbox
    private boolean solid = false; // If true, player can stand on this platform
    
    // For waiting at target before returning
    private boolean waitingAtTarget = false;
    private long waitStartTime;
    private long waitDurationMs = 1000; // 1 second wait
    
    // For multi-tile platforms
    private List<float[]> tilePositions = new ArrayList<>(); // Relative positions of tiles
    private List<BufferedImage> tileSprites = new ArrayList<>();
    private float firstTileOffsetX = 0, firstTileOffsetY = 0; // Offset of first tile within bounding box
    
    // Original sprite bounds (for collision when hitbox is enlarged)
    private float originalX, originalY;
    private int originalWidth, originalHeight;
    
    public TriggerPlatform(float x, float y, float targetX, float targetY, 
                           int width, int height, float speed, BufferedImage sprite, boolean shouldReturn) {
        super(x, y, width, height);
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
        initHitbox(x, y, width, height);
        // Don't enlarge hitbox here - let Level do it for grouped platforms
    }
    
    // Set the offset of the first tile sprite within the bounding box
    public void setFirstTileOffset(float offsetX, float offsetY) {
        this.firstTileOffsetX = offsetX;
        this.firstTileOffsetY = offsetY;
    }
    
    // Add a tile to this platform (position relative to bounding box top-left)
    public void addTile(float relX, float relY, BufferedImage tileSprite) {
        tilePositions.add(new float[]{relX, relY});
        tileSprites.add(tileSprite);
    }
    
    public void update() {
        if (!triggered || reachedTarget) {
            return;
        }
        
        // If waiting at target, check if wait is over
        if (waitingAtTarget) {
            if (System.currentTimeMillis() - waitStartTime >= waitDurationMs) {
                waitingAtTarget = false;
                movingToTarget = false;
            }
            return;
        }
        
        // Determine current destination
        float destX = movingToTarget ? targetX : startX;
        float destY = movingToTarget ? targetY : startY;
        
        float dirX = destX - hitbox.x;
        float dirY = destY - hitbox.y;
        float distance = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        
        if (distance < speed) {
            // Reached destination
            hitbox.x = destX;
            hitbox.y = destY;
            
            if (movingToTarget && shouldReturn) {
                // Start waiting at target
                waitingAtTarget = true;
                waitStartTime = System.currentTimeMillis();
            } else {
                // Done moving
                reachedTarget = true;
            }
        } else {
            // Move towards destination
            hitbox.x += (dirX / distance) * speed;
            hitbox.y += (dirY / distance) * speed;
        }
    }
    
    public void render(Graphics g) {
        int tileSize = main.Game.TILES_SIZE;
        
        // Sprite area is centered in the hitbox (hitbox is 1.5x the sprite area)
        float spriteAreaX = hitbox.x + hitbox.width / 6;
        float spriteAreaY = hitbox.y + hitbox.height / 6;
        
        // Draw first tile sprite at its offset within the sprite area
        int firstX = (int) (spriteAreaX + firstTileOffsetX);
        int firstY = (int) (spriteAreaY + firstTileOffsetY);
        
        if (sprite != null) {
            g.drawImage(sprite, firstX, firstY, tileSize, tileSize, null);
        } else {
            g.setColor(java.awt.Color.ORANGE);
            g.fillRect(firstX, firstY, tileSize, tileSize);
        }
        
        // Draw additional tiles relative to sprite area top-left
        for (int i = 0; i < tilePositions.size(); i++) {
            float[] pos = tilePositions.get(i);
            int tileX = (int) (spriteAreaX + pos[0]);
            int tileY = (int) (spriteAreaY + pos[1]);
            BufferedImage tileSprite = tileSprites.get(i);
            if (tileSprite != null) {
                g.drawImage(tileSprite, tileX, tileY, tileSize, tileSize, null);
            }
        }
        // Uncomment to debug hitbox:
        //g.setColor(java.awt.Color.RED);
        //g.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }
    
    // Check if player is touching this platform
    public boolean checkPlayerCollision(Entity player) {
        return hitbox.intersects(player.getHitbox());
    }
    
    public void trigger() {
        triggered = true;
    }
    
    public boolean isTriggered() {
        return triggered;
    }
    
    public boolean hasReachedTarget() {
        return reachedTarget;
    }
    
    public void reset() {
        hitbox.x = startX;
        hitbox.y = startY;
        triggered = false;
        reachedTarget = false;
        movingToTarget = true;
        waitingAtTarget = false;
    }
    
    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }
    
    public void setHitboxSize(int hitboxWidth, int hitboxHeight, int newX, int newY) {
        // Calculate offset from old position
        float offsetX = newX - hitbox.x;
        float offsetY = newY - hitbox.y;
        
        hitbox.width = hitboxWidth;
        hitbox.height = hitboxHeight;
        hitbox.x = newX;
        hitbox.y = newY;
        this.startX = newX;
        this.startY = newY;
        // Also update target to maintain the same movement offset
        this.targetX += offsetX;
        this.targetY += offsetY;
    }
    
    public void setSolid(boolean solid) {
        this.solid = solid;
    }
    
    public boolean isSolid() {
        return solid;
    }
    
    // Get the sprite hitbox (the actual collidable area for standing)
    public java.awt.geom.Rectangle2D.Float getSpriteHitbox() {
        // Sprite area is centered in hitbox (hitbox is 1.5x sprite area)
        // Sprite = hitbox / 1.5 = hitbox * 2/3, offset = hitbox / 6
        float spriteAreaW = hitbox.width * 2 / 3;
        float spriteAreaH = hitbox.height * 2 / 3;
        float spriteAreaX = hitbox.x + hitbox.width / 6;
        float spriteAreaY = hitbox.y + hitbox.height / 6;
        return new java.awt.geom.Rectangle2D.Float(spriteAreaX, spriteAreaY, spriteAreaW, spriteAreaH);
    }
}

