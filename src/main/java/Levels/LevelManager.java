package Levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import entities.SpawnPlatform;
import main.Game;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
import static main.Game.TILES_SIZE;
import utilz.LoadSave;

public class LevelManager {
    private Game game;
    private BufferedImage[] levelSprite;
    private BufferedImage[] objectSprite;
    private BufferedImage spawnTube;
    private BufferedImage deathSprite;
    private List<Level> levels;
    private int currentLevelIndex = 0;


    public LevelManager(Game game){
        this.game = game;
        importOutsideSprites();
        buildAllLevels();
    }
    
    private void buildAllLevels() {
        levels = new ArrayList<>();
        spawnTube = LoadSave.GetSpriteAtlas(LoadSave.SPAWN_TUBE);
        deathSprite = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_DEAD);
        
        // Level 1
        Level level1 = new Level(
            LoadSave.GetLevelData(LoadSave.LEVEL_ONE_DATA),
            LoadSave.GetLevelObstacleData(LoadSave.LEVEL_ONE_OBSTACLE_DATA),
            LoadSave.GetLevelObjData(LoadSave.LEVEL_ONE_OBJ_DATA),
            230, 600);
        level1.createGroupedTriggerPlatformFromTile(1, 32, 0,2f, levelSprite, true, true);
        level1.createGroupedTriggerPlatformFromTile(2, 32, 0,2f, levelSprite, true, true);
        level1.createSpikesFromTile(3, 41, objectSprite);
        level1.setSpawnPlatform(new SpawnPlatform(224, 448, TILES_SIZE * 1, TILES_SIZE * 4, 64, 3f, spawnTube));
        levels.add(level1);
        
        // Level 2
        Level level2 = new Level(LoadSave.GetLevelData(LoadSave.LEVEL_TWO_DATA),
            LoadSave.GetLevelObstacleData(LoadSave.LEVEL_TWO_OBSTACLE_DATA),
            LoadSave.GetLevelObjData(LoadSave.LEVEL_TWO_OBJ_DATA),
            100, 400);
        level2.setSpawnPlatform(new SpawnPlatform(96, 256, TILES_SIZE * 1, TILES_SIZE * 4, 64, 3f, spawnTube));
        level2.createGroupedTriggerPlatformFromTile(1, 32, 0, 3f, levelSprite, false, true);
        level2.createGroupedTriggerPlatformFromTile(3, 62, 0, 3f, levelSprite, true, true);
        level2.createGroupedTriggerPlatformFromTile(4, -32, 32, 3f, levelSprite, false, true);
        level2.createTriggerSpikesFromTile(2, 41, 0, -16, 5f, 100f, objectSprite, false);
        levels.add(level2);
        
        // Level 3
        Level level3 = new Level(LoadSave.GetLevelData(LoadSave.LEVEL_THREE_DATA),
            LoadSave.GetLevelObstacleData(LoadSave.LEVEL_THREE_OBSTACLE_DATA),
            LoadSave.GetLevelObjData(LoadSave.LEVEL_THREE_OBJ_DATA),
            100, 150);
        level3.setSpawnPlatform(new SpawnPlatform(96, 96, TILES_SIZE * 1, TILES_SIZE * 4, 64, 3f, spawnTube));
        level3.createTriggerSpikesFromTile(1, 41, 0, -16, 5f, 50f, objectSprite, false);
        level3.createTriggerSpikesFromTile(2, 41, 0, -16, 5f, 50f, objectSprite, false);
        level3.createTriggerSpikesFromTile(3, 41, 0, -16, 5f, 50f, objectSprite, false);
        level3.createTriggerSpikesFromTile(4, 41, 0, -16, 5f, 50f, objectSprite, false);
        level3.createTriggerSpikesFromTile(5, 42, 0, 48, 5f, 50f, objectSprite, false);
        level3.createGroupedTriggerPlatformFromTile(6, 64, 0, 5f, levelSprite, false, true);
        level3.createTriggerSpikesFromTile(7, 41, 0, -16, 5f, 60f, objectSprite, true);
        level3.createTriggerSpikesFromTile(8, 41, 0, -16, 5f, 60f, objectSprite, true);
        level3.createTriggerSpikesFromTile(9, 41, 0, -96, 2f, 50f, objectSprite, true);
        level3.createTriggerSpikesFromTile(11, 42, 0, 48, 2f, 80f, objectSprite, true);
        level3.createTriggerSpikesFromTile(12, 42, 0, 48, 2f, 80f, objectSprite, true);
        level3.createTriggerSpikesFromTile(13, 42, 0, 48, 2f, 80f, objectSprite, true);
        level3.createTriggerSpikesFromTile(14, 42, 0, 48, 2f, 80f, objectSprite, true);
        level3.createGroupedTriggerPlatformFromTile(10, -64, 0, 5f, levelSprite, true, true);
        level3.setSpawnPlatform(new SpawnPlatform(96, 0, TILES_SIZE * 1, TILES_SIZE * 4, 64, 3f, spawnTube));
        levels.add(level3);
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
        
        BufferedImage objImg = LoadSave.GetSpriteAtlas(LoadSave.OBJECT_ATLAS);
        objectSprite = new BufferedImage[48];
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 8; i++) {
                int index = j*8 + i;
                objectSprite[index] = objImg.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    public void draw(Graphics g){
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.BG_DATA);
        g.drawImage(img, 0, 0,GAME_WIDTH,GAME_HEIGHT , null); 
        
        Level currentLevel = getCurrentLvl();
        currentLevel.drawTriggerSpikes(g);
        
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int index = currentLevel.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], i*TILES_SIZE, j*TILES_SIZE,TILES_SIZE,TILES_SIZE, null);
            }
        }
        
        currentLevel.drawPlatforms(g);
        currentLevel.drawSpikes(g);
        currentLevel.drawDeathSprites(g);
    }
    
    public void drawObjectLayer(Graphics g) {
        Level currentLevel = getCurrentLvl();
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int index = currentLevel.getObjectSpriteIndex(i, j);
                if (index > 0 && index < objectSprite.length) {
                    g.drawImage(objectSprite[index], i*TILES_SIZE, j*TILES_SIZE, TILES_SIZE, TILES_SIZE, null);
                }
            }
        }
    }
    
    public BufferedImage getDeathSprite() {
        return deathSprite;
    }

    public void update(){
        getCurrentLvl().updatePlatforms(game.getPlayer());
        getCurrentLvl().updateTriggerSpikes(game.getPlayer());
        getCurrentLvl().updateSpawnPlatform();
    }

    public Level getCurrentLvl() {
        //return levels.get(0); // For Testing
        return levels.get(currentLevelIndex);
    }
    
    public void loadNextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
        } else {
            // IMPLEMENT RETURN HOME? GAME FINISHED SCREEN?s
        }
    }
    
    public int getLevelCount() {
        return levels.size();
    }
    
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setLevelScore(int death){
        Level currLevel = levels.get(getCurrentLevelIndex());
        currLevel.updateDeathScore(death);
    }
    
    public BufferedImage getSprite(int tileId) {
        if (tileId >= 0 && tileId < levelSprite.length) {
            return levelSprite[tileId];
        }
        return levelSprite[0];
    }
}