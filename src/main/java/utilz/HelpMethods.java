package utilz;

import java.awt.geom.Rectangle2D;

import main.Game;
import static main.Game.GAME_HEIGHT;

public class HelpMethods {

    public static boolean CanMoveHere(float x,float y,float width,float height,int[][] lvlData){
        if (!IsSolid(x, y, lvlData))
            if (!IsSolid(x + width, y + height, lvlData))
                if (!IsSolid(x + width, y, lvlData))
                    if (!IsSolid(x, y + height, lvlData))
                        return true;
        return false;
    }

    private static boolean IsSolid(float x,float y, int[][] lvlData){
        if (x < 0 || x >= Game.GAME_WIDTH)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];
        //antal tiles - ej negativa - genomskinliga tilen
        if (value >= 81 || value < 0 || value != 80)
            return true;
        return false;
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed){
        int currentTile = (int)(hitbox.x / Game.TILES_SIZE);
        if (xSpeed > 0) {
            //höger
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int)(Game.TILES_SIZE - hitbox.width);
            return tileXPos + xOffset - 1;
        }else{
            //vänster
            return currentTile * Game.TILES_SIZE;
        }
    }


    public static float GetEntityYPosUnderOrAbove(Rectangle2D.Float hitbox, float airSpeed){
        int currentTile = (int)(hitbox.y / Game.TILES_SIZE);
        if (airSpeed > 0) {
            // faller / på mark
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int)(Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        }else{
            // hoppar
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData){
        //kolla pixel nere höger och vänster
        if (hitbox.y + hitbox.height >= GAME_HEIGHT - 5) {
                    System.out.println("DIE");
            }
        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) {
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData)) {
                return false;
                
            }
        }
        return true;
    }

    public static boolean IsEntityDead(Rectangle2D.Float hitbox, int[][] lvlData){
        if (hitbox.y + hitbox.height >= GAME_HEIGHT - 5) {
            return true;
        }
        //implement collision with obstacle.


        return false;

    }

    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000); // 2000 milliseconds = 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
