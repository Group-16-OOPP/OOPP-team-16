package main.view.entities;

import java.awt.Graphics;

import main.model.entities.SpikeModel;

/**
 * Renderer for Spike entity.
 * Handles all rendering logic for spikes.
 */
public class SpikeRenderer {

    private final SpikeModel model;

    public SpikeRenderer(SpikeModel model) {
        this.model = model;
    }

    /**
     * Renders the spike
     */
    public void render(Graphics g) {
        if (model.getSprite() != null) {
            g.drawImage(model.getSprite(), (int) model.getHitbox().x,
                       (int) (model.getHitbox().y - model.getHitbox().height),
                       (int) model.getHitbox().width, (int) (model.getHitbox().height * 2), null);
        } else {
            g.setColor(java.awt.Color.MAGENTA);
            g.fillRect((int) model.getHitbox().x, (int) model.getHitbox().y,
                      (int) model.getHitbox().width, (int) model.getHitbox().height);
        }
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
