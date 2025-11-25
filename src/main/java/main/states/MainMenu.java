package main.states;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import main.Game;

public class MainMenu {
    private Game game;
    private ArrayList<String> options = new ArrayList<>();
    private ArrayList<Rectangle> bounds  = new ArrayList<>();
    private int selected = -1;
    private Font titleFont = new Font("Arial", Font.BOLD, 48);
    private Font optionsFont = new Font("Arial", Font.PLAIN, 24);

    public MainMenu(Game game) {
        this.game = game;

        options.add("Play");
        options.add("Leaderboard");
        options.add("Quit");

        int buttonWidth = 300;
        int buttonHeight = 50;
        int startextNumber = (Game.GAME_WIDTH - buttonWidth) / 2;
        int startY = Game.GAME_HEIGHT / 2 - (options.size() * (buttonHeight + 10)) / 2;
        for (int i = 0; i < options.size(); i++) {
            bounds.add(new Rectangle(startextNumber, startY + i * (buttonHeight + 10), buttonWidth, buttonHeight));
        }
    }

    public void update() {
        //maybe dynamic screens later?
    }

    public void draw(Graphics g) {
        // background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // title
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "To the TOP!";
        FontMetrics fontmetrics = g.getFontMetrics();
        int textNumber = (Game.GAME_WIDTH - fontmetrics.stringWidth(title)) / 2;
        g.drawString(title, textNumber, 150);

        // the several options displayed:
        g.setFont(optionsFont);
        FontMetrics ofm = g.getFontMetrics();
        for (int i = 0; i < options.size(); i++) {
            Rectangle rectangle = bounds.get(i);
            if (i == selected) {
                g.setColor(Color.LIGHT_GRAY);
            } else {
                g.setColor(Color.DARK_GRAY);
            }
            g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            g.setColor(Color.WHITE);
            int sx = rectangle.x + (rectangle.width - ofm.stringWidth(options.get(i))) / 2;
            int sy = rectangle.y + (rectangle.height + ofm.getAscent()) / 2 - 4;
            g.drawString(options.get(i), sx, sy);
        }
    }

    //for dynamic "mouse hoover", haptics
    public void mouseMoved(int x, int y) {
        selected = -1;
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y)) {
                selected = i;
                break;
            }
        }
    }
    //for dynamic "mouse hoover", haptics
    public void mousePressed(int x, int y) {
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y)) {
                handleSelection(i);
                return;
            }
        }
    }


    private void handleSelection(int choice) {
        switch (choice) {
        case 0:
            game.setGameState(Game.GameState.PLAYING);
            break;
        case 1:
            game.setGameState(Game.GameState.LEADERBOARD);
            break;
        case 2:
            System.exit(0);
            break;
        }
    }
}
