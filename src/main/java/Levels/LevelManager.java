package Levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static main.Game.TILES_SIZE;
import utilz.LoadSave;

public class LevelManager {
    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;


    public LevelManager(Game game){
        this.game = game;
        importOutsideSprites();
        levelOne = new Level(LoadSave.GetLevelData());
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS); 
        levelSprite = new BufferedImage[81];
        for (int j = 0; j < 9; j++) {
            for (int i = 0; i < 9; i++) {
                int index = j*9 + i;
                levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    public void draw(Graphics g){
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.BG_DATA);
        g.drawImage(img, 0, 0,GAME_WIDTH,(int)(GAME_HEIGHT * 0.9f) , null); 
        
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], i*TILES_SIZE, j*TILES_SIZE,TILES_SIZE,TILES_SIZE, null);
            }
        }
        
    }

    public void update(){

    }

    public Level getCurrentLvl() {
        return levelOne;
    }
}
