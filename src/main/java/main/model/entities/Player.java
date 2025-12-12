package main.model.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.controller.Game;
import utilities.LoadSave;

import static utilities.Constants.PlayerConstants.*;
import static utilities.HelpMethods.*;

public class Player extends Entity {

    private static final long RESPAWN_DELAY_MS = 500;

    private BufferedImage[][] animation;
    private int aniTick;
    private int aniIndex;
    private int aniSpeed = 15;
    private int playerAction = IDLE_RIGHT;
    private boolean left;
    private boolean right;
    private boolean facingRight;
    private boolean moving = false;
    private boolean jump = false;
    private float playerSpeed = 1.0f;

    // Jumping / gravity mechanic
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -2.5f;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;

    private int[][] lvlData;
    private float xDrawOffset = 9.5f * Game.SCALE;
    private float yDrawOffset = 8.25f * Game.SCALE;

    private float spawnX;
    private float spawnY;

    // State flags
    private boolean isDead = false;
    private long deathTime = 0;
    private boolean reachedLevelEnd = false;
    private int currDeathCount = 0;

    private main.model.Levels.Level currentLevel;

    // REMOVED: PlayerEventListener playerEventListener;
    // The Player is now a pure Entity. It does not know about the Controller.

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimatons();
        initHitbox(x, y, 12 * Game.SCALE, 22 * Game.SCALE);
        spawnX = x;
        spawnY = y;
    }

    // REMOVED: setPlayerEventListener(Game listener)

    public void update() {
        if (isDead) {
            // Wait for respawn timer
            if (System.currentTimeMillis() - deathTime >= RESPAWN_DELAY_MS) {
                respawn();
            }
            return;
        }

        // Check for hazards
        if (isEntityDead(hitbox, lvlData)
                || (currentLevel != null && currentLevel.checkSpikeCollision(this))
                || (currentLevel != null && currentLevel.checkTriggerSpikeCollision(this))) {
            die();
            return;
        }

        // Check for level completion
        if (isOnLevelEnd(hitbox, lvlData)) {
            reachedLevelEnd = true;
        }

        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    private void die() {
        currDeathCount += 1;
        isDead = true;
        deathTime = System.currentTimeMillis();

        // 1. Record visual death position for the level to render a corpse/blood
        if (currentLevel != null) {
            BufferedImage deathSprite = LoadSave.getSpriteAtlas(LoadSave.PLAYER_DEAD);
            currentLevel.recordDeathPosition(hitbox.x - xDrawOffset, hitbox.y - yDrawOffset, deathSprite);
        }

        // 2. Move player off-screen "Into the Abyss"
        // The GameModel detects this specific coordinate change (x > 1500)
        // to trigger the "onPlayerDied" observer event.
        hitbox.x = 2000;
        hitbox.y = 2000;

        resetInAir();
        resetDirBooleans();
    }

    private void respawn() {
        isDead = false;
        hitbox.x = spawnX;
        hitbox.y = spawnY;

        resetDirBooleans();
        airSpeed = 0;
        moving = false;
        jump = false;

        inAir = !isEntityOnFloor(hitbox, lvlData);
    }

    public void updatePos() {
        moving = false;
        if (jump) {
            jump();
        }

        if (!left && !right && !inAir) {
            return;
        }
        float xSpeed = 0;

        if (left) {
            xSpeed -= playerSpeed;
        }

        if (right) {
            xSpeed += playerSpeed;
        }

        if (!inAir) {
            if (!isEntityOnFloor(hitbox, lvlData) && (currentLevel == null ||
                    !currentLevel.isOnSolidPlatform(hitbox))) {
                inAir = true;
            }
        }

        if (inAir) {
            if (canMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width,
                    hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);

                if (currentLevel != null && airSpeed > 0) {
                    float platformY = currentLevel.getSolidPlatformY(hitbox, airSpeed);
                    if (platformY >= 0) {
                        hitbox.y = platformY;
                        resetInAir();
                    }
                }
            } else {
                hitbox.y = getEntityYPosUnderOrAbove(hitbox, airSpeed);
                if (airSpeed > 0) {
                    resetInAir();
                } else {
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        } else {
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void jump() {
        if (inAir) {
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if (canMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width,
                hitbox.height, lvlData)) {
            hitbox.x += xSpeed;
        } else {
            hitbox.x = getEntityXPosNextToWall(hitbox, xSpeed);
        }
    }

    // --- State Getters / Setters ---

    public boolean hasReachedLevelEnd() {
        return reachedLevelEnd;
    }

    public void resetLevelEnd() {
        reachedLevelEnd = false;
    }

    public int getDeathCount() {
        return currDeathCount;
    }

    public void resetDeathCount() {
        currDeathCount = 0;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void setCurrentLevel(main.model.Levels.Level level) {
        this.currentLevel = level;
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!isEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
    }

    public void setSpawnPoint(float x, float y) {
        this.spawnX = x;
        this.spawnY = y;
    }

    public void spawnAtLevelStart() {
        hitbox.x = spawnX;
        hitbox.y = spawnY;
        resetDirBooleans();
        airSpeed = 0;
        moving = false;
        jump = false;
        isDead = false;
        reachedLevelEnd = false;
        inAir = !isEntityOnFloor(hitbox, lvlData);
    }

    // --- Rendering / Animations ---
    // (Ideally, these would be moved to GameView in a strict MVC refactor,
    // but kept here to ensure the rendering system remains functional for now)

    public void render(Graphics g) {
        g.drawImage(animation[playerAction][aniIndex],
                (int) (hitbox.x - xDrawOffset),
                (int) (hitbox.y - yDrawOffset), width, height, null);
    }

    private void loadAnimatons() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
        animation = new BufferedImage[4][8];
        for (int j = 0; j < animation.length; j++) {
            for (int i = 0; i < animation[j].length; i++) {
                animation[j][i] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    private void setAnimation() {
        int startAni = playerAction;

        if (moving) {
            if (left) {
                playerAction = RUNNING_LEFT;
                facingRight = false;
            } else if (right) {
                facingRight = true;
                playerAction = RUNNING_RIGHT;
            }
        } else {
            if (!facingRight) {
                playerAction = IDLE_LEFT;
            } else {
                playerAction = IDLE_RIGHT;
            }
        }

        if (jump) {
            if (!facingRight) {
                playerAction = JUMPING_LEFT;
            } else {
                playerAction = JUMPING_RIGHT;
            }
        }

        if (startAni != playerAction) {
            resetAniTick();
        }
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= getSpriteAmount(playerAction)) {
                aniIndex = 0;
            }
        }
    }
}