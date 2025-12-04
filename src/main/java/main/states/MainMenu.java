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
    private ArrayList<Rectangle> bounds = new ArrayList<>();
    private int selected = -1;
    private Font titleFont = new Font("Arial", Font.BOLD, 48);
    private Font optionsFont = new Font("Arial", Font.PLAIN, 24);

    private boolean editingName = false;
    private StringBuilder nameBuffer = new StringBuilder();

    public MainMenu(Game game) {
        this.game = game;

        options.add("Play");               // index 0
        options.add("Change Player Name"); // index 1
        options.add("Select Level");       // index 2
        options.add("Leaderboard");        // index 3
        options.add("Quit");               // index 4

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

        // current player name
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String nameText = "Player: " + game.getPlayerName();
        g.drawString(nameText, 20, 40);

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

        if (editingName) {
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Color.WHITE);
            String prompt = "Enter name (letters/numbers), ENTER to confirm, ESC to cancel";
            int pw = g.getFontMetrics().stringWidth(prompt);
            int px = (Game.GAME_WIDTH - pw) / 2;
            int py = Game.GAME_HEIGHT - 80;
            g.drawString(prompt, px, py);
            String current = nameBuffer.toString();
            int cw = g.getFontMetrics().stringWidth(current + "_");
            int cx = (Game.GAME_WIDTH - cw) / 2;
            g.drawString(current + "_", cx, py + 30);
        }
    }

    public void mouseMoved(int x, int y) {
        if (editingName) {
            return;
        }
        selected = -1;
        for (int i = 0; i < bounds.size(); i++) {
            if (bounds.get(i).contains(x, y)) {
                selected = i;
                break;
            }
        }
    }

    public void mousePressed(int x, int y) {
        if (editingName) {
            return;
        }
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
        case 1: // Change Name
            startEditingName();
            break;
        case 2: // Select Level
            game.setGameState(Game.GameState.LEVEL_SELECT);
            break;
        case 3: // Leaderboard
            game.setGameState(Game.GameState.LEADERBOARD);
            break;
        case 4: // Quit
            System.exit(0);
            break;
        default:
            break;
        }
    }

    private void startEditingName() {
        editingName = true;
        nameBuffer.setLength(0);
        nameBuffer.append(game.getPlayerName());
    }

    public boolean isEditingName() {
        return editingName;
    }

    public void handleNameKeyPressed(int keyCode, char keyChar) {
        if (!editingName) return;

        if (keyCode == java.awt.event.KeyEvent.VK_ENTER) {
            if (!nameBuffer.isEmpty()) {
                game.setPlayerName(nameBuffer.toString());
            }
            editingName = false;
            return;
        }
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            editingName = false;
            return;
        }
        if (keyCode == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            if (!nameBuffer.isEmpty()) {
                nameBuffer.deleteCharAt(nameBuffer.length() - 1);
            }
            return;
        }

        if (keyCode == 0 && Character.isLetterOrDigit(keyChar) && nameBuffer.length() < 16) {
            nameBuffer.append(keyChar);
        }
    }
}
