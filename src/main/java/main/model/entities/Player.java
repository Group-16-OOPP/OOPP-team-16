package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import main.Game;
import main.controller.entities.PlayerController;
import main.model.entities.PlayerModel;
import main.observerEvents.PlayerEventListener;
import main.view.entities.PlayerRenderer;

import utilz.LoadSave;

public class Player extends Entity {
    // MVC Architecture Components
    private PlayerModel model;
    private PlayerController controller;
    private PlayerRenderer renderer;

    // Legacy support - keep minimal fields for backward compatibility
    private Levels.Level currentLevel;

    public Player(float x, float y, int width, int height) {
        super(x, y, width, height);

        // Initialize MVC components
        model = new PlayerModel(x, y, width, height);
        BufferedImage[][] animations = loadAnimations();
        controller = new PlayerController(model);
        renderer = new PlayerRenderer(model, animations);

        // Initialize hitbox through model
        model.getHitbox().setRect(x, y, 12 * Game.SCALE, 22 * Game.SCALE);
    }

    public void setPlayerEventListener(PlayerEventListener listener) {
        controller.setPlayerEventListener(listener);
    }

    public void update() {
        controller.update();
    }

    public boolean hasReachedLevelEnd() {
        return controller.hasReachedLevelEnd();
    }

    public void resetLevelEnd() {
        controller.resetLevelEnd();
    }


    public void render(Graphics g) {
        renderer.render(g);
        //drawHitbox(g); // Optional debug rendering
    }


    private BufferedImage[][] loadAnimations() {
        BufferedImage img = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
        //Storleken på spritesheet
        BufferedImage[][] animations = new BufferedImage[4][8];
        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                //sprite size
                animations[j][i] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
        return animations;
    }

    public void loadLvlData(int[][] lvlData) {
        model.setLvlData(lvlData);
        // Check if player should be in air based on level data
        // This logic is now handled in the controller
    }

    public void setSpawnPoint(float x, float y) {
        model.setSpawnX(x);
        model.setSpawnY(y);
    }

    public void spawnAtLevelStart() {
        model.setHitboxPosition(model.getSpawnX(), model.getSpawnY());
        // Reset state - handled by controller
    }

    public int getDeathCount() {
        return controller.getDeathCount();
    }

    public void resetDeathCount() {
        controller.resetDeathCount();
    }

    @Override
    public Rectangle2D.Float getHitbox() {
        return model.getHitbox();
    }

    public void resetDirBooleans() {
        model.setLeft(false);
        model.setRight(false);
    }

    public boolean isLeft() {
        return model.isLeft();
    }

    public void setLeft(boolean left) {
        model.setLeft(left);
    }

    public boolean isRight() {
        return model.isRight();
    }

    public void setRight(boolean right) {
        model.setRight(right);
    }

    public void setJump(boolean jump) {
        model.setJump(jump);
    }

    public void setCurrentLevel(Levels.Level level) {
        this.currentLevel = level;
        controller.setCurrentLevel(level);
    }
}
