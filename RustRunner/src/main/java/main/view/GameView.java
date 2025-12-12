package main.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import main.model.GameModel;

//ONLY esponsible for rendering game visuals based on the model, all view things go here
public class GameView {
    private final GameModel model;

    public GameView(GameModel model) {
        this.model = model;
    }

    public void renderGame(Graphics g) {
        model.getLevelManager().draw(g);
        model.getLevelManager().drawObjectLayer(g);
        model.getPlayer().render(g);
        model.getLevelManager().getCurrentLvl().drawSpawnPlatform(g);
        drawHUD(g);

        if (model.isPaused()) {
            drawPauseOverlay(g);
        }
    }

    //
    public void renderTransition(Graphics g, BufferedImage transitionImage) {
        if (!model.isInTransition() || transitionImage == null) {
            return;
        }

        int gameWidth = Game.GAME_WIDTH;
        int gameHeight = Game.GAME_HEIGHT;
        float transitionScale = model.getTransitionScale();

        int scaledWidth = (int) (gameWidth * transitionScale * 1.5f);
        int scaledHeight = (int) (gameHeight * transitionScale * 1.5f);

        int x = (int) (model.getPlayer().getHitbox().x - (scaledWidth / 2.0f));
        int y = (int) (model.getPlayer().getHitbox().y - (scaledHeight / 2.0f));

        g.drawImage(transitionImage, x, y, scaledWidth, scaledHeight, null);
    }

    private void drawHUD(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, 200, 60, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level: " + (model.getLevelManager().getCurrentLevelIndex() + 1), 20, 35);
        g.drawString("Deaths: " + model.getPlayer().getDeathCount(), 20, 55);
    }

    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String text = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (Game.GAME_WIDTH - textWidth) / 2;
        int y = (Game.GAME_HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String hint = "Press P to resume game";
        fm = g.getFontMetrics();
        int hintWidth = fm.stringWidth(hint);
        int hx = (Game.GAME_WIDTH - hintWidth) / 2;
        int hy = y + 40;
        g.drawString(hint, hx, hy);
    }
}

