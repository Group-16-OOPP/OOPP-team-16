package main.view.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.model.entities.PlayerModel;

/**
 * Renderer for Player entity.
 * Handles all rendering logic for the player character.
 */
public class PlayerRenderer {

    private final PlayerModel model;
    private final BufferedImage[][] animations;

    public PlayerRenderer(PlayerModel model, BufferedImage[][] animations) {
        this.model = model;
        this.animations = animations;
    }

    /**
     * Renders the player character
     */
    public void render(Graphics g) {
        g.drawImage(animations[model.getPlayerAction()][model.getAniIndex()],
                (int) (model.getHitbox().x - model.getXDrawOffset()),
                (int) (model.getHitbox().y - model.getYDrawOffset()),
                model.getWidth(), model.getHeight(), null);
    }

    /**
     * Optional debug rendering of hitbox
     */
    public void renderHitbox(Graphics g) {
        g.setColor(java.awt.Color.RED);
        g.drawRect((int) model.getHitbox().x, (int) model.getHitbox().y,
                  (int) model.getHitbox().width, (int) model.getHitbox().height);
    }
}
