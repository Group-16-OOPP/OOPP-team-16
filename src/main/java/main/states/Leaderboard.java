package main.states;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import main.Game;

public class Leaderboard {
    private Game game;
    private ArrayList<String> options = new ArrayList<>();
    private ArrayList<Rectangle> bounds  = new ArrayList<>();
    private int selected = -1;
    private Font titleFont = new Font("Arial", Font.BOLD, 48);
    private Font optFont = new Font("Arial", Font.PLAIN, 24);

    public Leaderboard(Game game) {
        this.game = game;

        options.add("Play");
        options.add("Leaderboard");
        options.add("Quit");

        int buttonWidth = 300;
        int buttonHeight = 50;
        int startX = (Game.GAME_WIDTH - buttonWidth) / 2;
        int startY = Game.GAME_HEIGHT / 2 - (options.size() * (buttonHeight + 10)) / 2;
        for (int i = 0; i < options.size(); i++) {
            bounds.add(new Rectangle(startX, startY + i * (buttonHeight + 10), buttonWidth, buttonHeight));
        }
    }


    public void update() {
        //TODO once we have the scoring parts
    }

    public void draw(Graphics g) {
        // background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        // title
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "LEADERBOARD";
        FontMetrics fontmetrics = g.getFontMetrics();
        int tx = (Game.GAME_WIDTH - fontmetrics.stringWidth(title)) / 2;
        g.drawString(title, tx, 150);
    }
}

