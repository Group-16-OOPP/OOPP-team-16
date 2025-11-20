package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import static utilz.Constants.PlayerConstants.GetSpriteAmount;
import static utilz.Constants.PlayerConstants.IDLE;
import static utilz.Constants.PlayerConstants.JUMPING;
import static utilz.Constants.PlayerConstants.RUNNING;

public class Player extends Entity{

    private BufferedImage[][] animation;
    private int aniTick, aniIndex, aniSpeed = 30;
    private int playerAction = IDLE;
    private boolean left,up,right,down;
    private boolean moving = false, jump = false, isjumping = false;
    private float playerSpeed = 1.0f;
    
    // jump mechanics
    private float airSpeedY = 0f;
    private float gravity = 0.04f;
    private float jumpSpeed = -2.5f;
    private float groundY;

    public Player(float x, float y) {
        super(x, y);
        this.groundY = y;
        loadAnimatons();
        
    }

    public void update(){
        updatePos();
        updateAnimationTick();
        setAnimation();
    }
    public void render(Graphics g){
        g.drawImage(animation[playerAction][aniIndex], (int)x, (int)y,175,175, null);
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
        
        // Horizontal movement
        if (left && !right) {
            x -= playerSpeed;
            moving = true;
        }else if(right && !left){
            x += playerSpeed;
            moving = true;
        }

        // jump mechanics
        if (jump) {
            // Apply gravity
            airSpeedY += gravity;
            y += airSpeedY;
            System.out.println("y is:" + y + "groundy is:" + groundY);
            
            // Check if landed back on ground
            if (y >= groundY) {
                y = groundY;
                airSpeedY = 0f;
                jump = false;
            }
        }
        
        // Vertical movement (only when not jump)
        if (!jump) {
            if (down && !up) {
                y += playerSpeed;
                moving = true;
            }
        }
    }

    private void loadAnimatons() {

        InputStream is = getClass().getResourceAsStream("/Character.png");

        try {
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                //Storleken på spritesheet
                animation = new BufferedImage[2][8];
                for (int j = 0; j < animation.length; j++) {
                    for (int i = 0; i < animation[j].length; i++) {
                        //sprite size
                        animation[j][i] = img.getSubimage(i*175, j*175, 175, 175);
                    }
                }
            } else {
                System.err.println("Could not load image: /Character.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        down = false;
        up = false;
    }

    public void setJump(boolean isJump){
        if (isJump && !jump && y >= groundY) {
            this.jump = true;
            this.isjumping = true;
            this.airSpeedY = jumpSpeed;
        }
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

}
