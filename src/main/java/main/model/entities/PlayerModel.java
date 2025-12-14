package main.model.entities;

import java.awt.geom.Rectangle2D;

import main.Game;

public class PlayerModel {

    // Constants
    private static final long RESPAWN_DELAY_MS = 500;

    // Position and movement
    private float x;
    private float y;
    private int width;
    private int height;
    private Rectangle2D.Float hitbox;

    // Animation state
    private int aniTick;
    private int aniIndex;
    private int aniSpeed = 15;
    private int playerAction;
    private boolean facingRight;
    private boolean moving = false;

    // Input state
    private boolean left;
    private boolean right;
    private boolean jump = false;
    private float playerSpeed = 1.0f;

    // Physics state
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.5f;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    // Level and collision data
    private int[][] lvlData;

    // Rendering offsets
    private float xDrawOffset = 9.5f * Game.SCALE;
    private float yDrawOffset = 8.25f * Game.SCALE;

    // Spawn position
    private float spawnX;
    private float spawnY;

    // Game state
    private boolean isDead = false;
    private long deathTime = 0;
    private boolean reachedLevelEnd = false;
    private int currDeathCount = 0;

    public PlayerModel(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle2D.Float(x, y, 12 * Game.SCALE, 22 * Game.SCALE);
        this.spawnX = x;
        this.spawnY = y;
        this.playerAction = utilz.Constants.PlayerConstants.IDLE_RIGHT;
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

    public int getAniTick() { return aniTick; }
    public void setAniTick(int aniTick) { this.aniTick = aniTick; }

    public int getAniIndex() { return aniIndex; }
    public void setAniIndex(int aniIndex) { this.aniIndex = aniIndex; }

    public int getAniSpeed() { return aniSpeed; }
    public void setAniSpeed(int aniSpeed) { this.aniSpeed = aniSpeed; }

    public int getPlayerAction() { return playerAction; }
    public void setPlayerAction(int playerAction) { this.playerAction = playerAction; }

    public boolean isFacingRight() { return facingRight; }
    public void setFacingRight(boolean facingRight) { this.facingRight = facingRight; }

    public boolean isMoving() { return moving; }
    public void setMoving(boolean moving) { this.moving = moving; }

    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }

    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }

    public boolean isJump() { return jump; }
    public void setJump(boolean jump) { this.jump = jump; }

    public float getPlayerSpeed() { return playerSpeed; }
    public void setPlayerSpeed(float playerSpeed) { this.playerSpeed = playerSpeed; }

    public float getAirSpeed() { return airSpeed; }
    public void setAirSpeed(float airSpeed) { this.airSpeed = airSpeed; }

    public float getGravity() { return gravity; }
    public void setGravity(float gravity) { this.gravity = gravity; }

    public float getJumpSpeed() { return jumpSpeed; }
    public void setJumpSpeed(float jumpSpeed) { this.jumpSpeed = jumpSpeed; }

    public float getFallSpeedAfterCollision() { return fallSpeedAfterCollision; }
    public void setFallSpeedAfterCollision(float fallSpeedAfterCollision) {
        this.fallSpeedAfterCollision = fallSpeedAfterCollision;
    }

    public boolean isInAir() { return inAir; }
    public void setInAir(boolean inAir) { this.inAir = inAir; }

    public int[][] getLvlData() { return lvlData; }
    public void setLvlData(int[][] lvlData) { this.lvlData = lvlData; }

    public float getXDrawOffset() { return xDrawOffset; }
    public void setXDrawOffset(float xDrawOffset) { this.xDrawOffset = xDrawOffset; }

    public float getYDrawOffset() { return yDrawOffset; }
    public void setYDrawOffset(float yDrawOffset) { this.yDrawOffset = yDrawOffset; }

    public float getSpawnX() { return spawnX; }
    public void setSpawnX(float spawnX) { this.spawnX = spawnX; }

    public float getSpawnY() { return spawnY; }
    public void setSpawnY(float spawnY) { this.spawnY = spawnY; }

    public boolean isDead() { return isDead; }
    public void setDead(boolean isDead) { this.isDead = isDead; }

    public long getDeathTime() { return deathTime; }
    public void setDeathTime(long deathTime) { this.deathTime = deathTime; }

    public boolean hasReachedLevelEnd() { return reachedLevelEnd; }
    public void setReachedLevelEnd(boolean reachedLevelEnd) { this.reachedLevelEnd = reachedLevelEnd; }

    public int getCurrDeathCount() { return currDeathCount; }
    public void setCurrDeathCount(int currDeathCount) { this.currDeathCount = currDeathCount; }

    public long getRespawnDelayMs() { return RESPAWN_DELAY_MS; }

    // Utility methods for hitbox manipulation
    public void setHitboxPosition(float x, float y) {
        hitbox.x = x;
        hitbox.y = y;
    }

    public void moveHitbox(float deltaX, float deltaY) {
        hitbox.x += deltaX;
        hitbox.y += deltaY;
    }
}
