package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import main.events.PlayerEventListener;

import static utilz.Constants.PlayerConstants.getSpriteAmount;
import static utilz.Constants.PlayerConstants.IDLE_LEFT;
import static utilz.Constants.PlayerConstants.IDLE_RIGHT;
import static utilz.Constants.PlayerConstants.JUMPING_LEFT;
import static utilz.Constants.PlayerConstants.JUMPING_RIGHT;
import static utilz.Constants.PlayerConstants.RUNNING_LEFT;
import static utilz.Constants.PlayerConstants.RUNNING_RIGHT;
import static utilz.HelpMethods.canMoveHere;
import static utilz.HelpMethods.getEntityXPosNextToWall;
import static utilz.HelpMethods.getEntityYPosUnderOrAbove;
import static utilz.HelpMethods.isEntityDead;
import static utilz.HelpMethods.isEntityOnFloor;
import static utilz.HelpMethods.isOnLevelEnd;

import utilz.LoadSave;

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

    //Jumping / gravity mechanic
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
    private boolean isDead = false;
    private long deathTime = 0;
    private boolean reachedLevelEnd = false;
    private int currDeathCount = 0;
    private Levels.Level currentLevel;

    private PlayerEventListener playerEventListener; // replaces direct Game coupling for death

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimatons();
        initHitbox(x, y, 12 * Game.SCALE, 22 * Game.SCALE);
        spawnX = x;
        spawnY = y;
    }

    public void setPlayerEventListener(PlayerEventListener listener) {
        this.playerEventListener = listener;
    }

    public void update() {
        if (isDead) {
            if (System.currentTimeMillis() - deathTime >= RESPAWN_DELAY_MS) {
                respawn();
            }
            return;
        }
        if (isEntityDead(hitbox, lvlData)
                || (currentLevel != null && currentLevel.checkSpikeCollision(this))
                || (currentLevel != null && currentLevel.checkTriggerSpikeCollision(this))) {
            die();
            return;
        }
        if (isOnLevelEnd(hitbox, lvlData)) {
            reachedLevelEnd = true;
        }
        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    public boolean hasReachedLevelEnd() {
        return reachedLevelEnd;
    }

    public void resetLevelEnd() {
        reachedLevelEnd = false;
    }

    private void die() {
        currDeathCount += 1;
        if (playerEventListener != null) {
            playerEventListener.onPlayerDeath();
        }
        isDead = true;
        deathTime = System.currentTimeMillis();

        // Record death position before moving player off screen
        if (currentLevel != null) {
            BufferedImage deathSprite = LoadSave.getSpriteAtlas(LoadSave.PLAYER_DEAD);
            currentLevel.recordDeathPosition(hitbox.x - xDrawOffset, hitbox.y - yDrawOffset, deathSprite);
        }

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

    public void render(Graphics g) {
        g.drawImage(animation[playerAction][aniIndex],
                (int) (hitbox.x - xDrawOffset),
                (int) (hitbox.y - yDrawOffset), width, height, null);
        //drawHitbox(g);
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

    private void updatePos() {
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

                // Check if landed on solid platform
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

    private void loadAnimatons() {

        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
        //Storleken på spritesheet
        animation = new BufferedImage[4][8];
        for (int j = 0; j < animation.length; j++) {
            for (int i = 0; i < animation[j].length; i++) {
                //sprite size
                animation[j][i] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }

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

    public void setCurrentLevel(Levels.Level level) {
        this.currentLevel = level;
    }
}
