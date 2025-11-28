package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;

public class DeathSprite extends Entity {
    private BufferedImage sprite;

    public DeathSprite(float x, float y, BufferedImage sprite) {
        super(x, y, Game.TILES_SIZE, Game.TILES_SIZE);
        this.sprite = sprite;
        initHitbox(x, y, Game.TILES_SIZE, Game.TILES_SIZE);
    }

    public void render(Graphics g) {
        g.drawImage(sprite, (int)hitbox.x, (int)hitbox.y, width, height, null);
    }
}

