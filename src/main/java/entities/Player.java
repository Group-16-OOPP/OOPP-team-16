package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import static utilz.Constants.PlayerConstants.GetSpriteAmount;
import static utilz.Constants.PlayerConstants.IDLE;
import static utilz.Constants.PlayerConstants.JUMPING;
import static utilz.Constants.PlayerConstants.RUNNING;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityXPosNextToWall;
import static utilz.HelpMethods.GetEntityYPosUnderOrAbove;
import static utilz.HelpMethods.IsEntityDead;
import static utilz.HelpMethods.IsEntityOnFloor;
import utilz.LoadSave;

public class Player extends Entity{

    private BufferedImage[][] animation;
    private int aniTick, aniIndex, aniSpeed = 15;
    private int playerAction = IDLE;
    private boolean left,right;
    private boolean moving = false, jump = false;
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
    private float spawnX, spawnY;
    private boolean isDead = false;
    private long deathTime = 0;
    private static final long RESPAWN_DELAY_MS = 500; // 2 seconds
    
    
    
    
    

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);
        loadAnimatons();
        initHitbox(x, y , 12*Game.SCALE, 20*Game.SCALE);
        spawnX = x;
        spawnY = y;
        
    }

    public void update(){
        if (isDead) {
            if (System.currentTimeMillis() - deathTime >= RESPAWN_DELAY_MS) {
                respawn();
            }
            return;
        }
        if (IsEntityDead(hitbox, lvlData)) {
            die();
            return;
        }
        updatePos();
        updateAnimationTick();
        setAnimation();
    }
    
    private void die() {
        isDead = true;
        deathTime = System.currentTimeMillis();
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

        inAir = !IsEntityOnFloor(hitbox, lvlData);
    }
    public void render(Graphics g){
        g.drawImage(animation[playerAction][aniIndex], (int)(hitbox.x - xDrawOffset), (int)(hitbox.y - yDrawOffset),width,height, null);
        //drawHitbox(g);
    }

    private void setAnimation() {

        int startAni = playerAction;

        if (moving) {
            playerAction = RUNNING;
        }else{
            playerAction = IDLE;
        }

        if (jump) {
            playerAction = JUMPING;
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
        aniTick ++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex ++;
            if (aniIndex >= GetSpriteAmount(playerAction)) {
                aniIndex = 0;
            }
        }
    }

    private void updatePos() {
        moving = false;
        if (jump) {
            jump();
        }
        if (!left && !right && !inAir)
            return;
        float xSpeed = 0;


        if (left)
            xSpeed -= playerSpeed;

        if(right)
            xSpeed += playerSpeed;

        if (!inAir)
            if (!IsEntityOnFloor(hitbox,lvlData))
                inAir = true;

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            }else{
                hitbox.y = GetEntityYPosUnderOrAbove(hitbox,airSpeed);
                if (airSpeed > 0) {
                    resetInAir();
                }else{
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        }else{
            updateXPos(xSpeed);
        }
        moving = true;
    }

    private void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if(CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)){
            hitbox.x += xSpeed;
        }else{
            hitbox.x = GetEntityXPosNextToWall(hitbox,xSpeed);
        }
    }

    private void loadAnimatons() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        //Storleken på spritesheet
        animation = new BufferedImage[2][8];
        for (int j = 0; j < animation.length; j++) {
            for (int i = 0; i < animation[j].length; i++) {
                //sprite size
                animation[j][i] = img.getSubimage(i*32, j*32, 32, 32);
            }
        }

    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
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



}
