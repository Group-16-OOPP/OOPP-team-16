package main.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Levels.LevelManager;
import main.Game;
import utilities.LoadSave;

public class LevelSelect {
    // Grid layout
    private static final int COLUMNS = 3;
    private static final int TOP_MARGIN = 120;
    private static final int PREVIEW_WIDTH = 300;
    private static final int PREVIEW_HEIGHT = 200;
    private static final int PREVIEW_SPACING = 30;

    //variables
    private Game game;
    private LevelManager levelManager;
    private ArrayList<Rectangle> levelBounds = new ArrayList<>();
    private ArrayList<BufferedImage> levelPreviews = new ArrayList<>();
    private BufferedImage lockImage;
    private int hovered = -1;
    private Font titleFont = new Font("Arial", Font.BOLD, 48);
    private Font levelFont = new Font("Arial", Font.BOLD, 24);

    public LevelSelect(Game game, LevelManager levelManager) {
        this.game = game;
        this.levelManager = levelManager;
        loadLevelPreviews();
        calculateLevelBounds();
    }
    
    private void loadLevelPreviews() {
        // Load preview images for each level
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_ONE_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_TWO_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_THREE_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_FOUR_DATA));
        levelPreviews.add(LoadSave.getSpriteAtlas(LoadSave.LEVEL_FIVE_DATA));
        
        // Load lock image
        lockImage = LoadSave.getSpriteAtlas(LoadSave.LOCK);
    }
    
    private void calculateLevelBounds() {
        int totalLevels = levelManager.getLevelCount();
        int startX = (Game.GAME_WIDTH - (COLUMNS * PREVIEW_WIDTH + (COLUMNS - 1) * PREVIEW_SPACING)) / 2;
        
        for (int i = 0; i < totalLevels; i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = startX + col * (PREVIEW_WIDTH + PREVIEW_SPACING);
            int y = TOP_MARGIN + row * (PREVIEW_HEIGHT + PREVIEW_SPACING);
            levelBounds.add(new Rectangle(x, y, PREVIEW_WIDTH, PREVIEW_HEIGHT));
        }
    }
    
    public void update() {
        // Maybe add animations later?
    }
    
    public void draw(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        
        // Title
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "Select Level";
        FontMetrics fontMetrics = g.getFontMetrics();
        int titleX = (Game.GAME_WIDTH - fontMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 60);
        
        // Draw level previews in grid
        g.setFont(levelFont);
        FontMetrics levelFontMetrics = g.getFontMetrics();
        
        for (int i = 0; i < levelBounds.size() && i < levelPreviews.size(); i++) {
            Rectangle bounds = levelBounds.get(i);
            BufferedImage preview = levelPreviews.get(i);
            boolean isUnlocked = levelManager.isLevelUnlocked(i);
            
            // Draw border
            if (i == hovered && isUnlocked) {
                g.setColor(Color.YELLOW);
                g.fillRect(bounds.x - 3, bounds.y - 3, bounds.width + 6, bounds.height + 6);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
            }
            
            // Draw preview image (scaled to fit)
            if (preview != null) {
                // Darken locked levels
                if (!isUnlocked) {
                    g.setColor(new Color(0, 0, 0, 180));
                    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                }
                g.drawImage(preview, bounds.x, bounds.y, bounds.width, bounds.height, null);
            } else {
                // Fallback if image not loaded
                g.setColor(Color.GRAY);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
            
            // Draw lock overlay for locked levels
            if (!isUnlocked && lockImage != null) {
                int lockSize = 80;
                int lockX = bounds.x + (bounds.width - lockSize) / 2;
                int lockY = bounds.y + (bounds.height - lockSize) / 2;
                g.drawImage(lockImage, lockX, lockY, lockSize, lockSize, null);
            }
            
            // Draw level number overlay
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(bounds.x, bounds.y + bounds.height - 40, bounds.width, 40);
            g.setColor(isUnlocked ? Color.WHITE : Color.GRAY);
            String levelText = "Level " + (i + 1);
            int textX = bounds.x + (bounds.width - levelFontMetrics.stringWidth(levelText)) / 2;
            int textY = bounds.y + bounds.height - 10;
            g.drawString(levelText, textX, textY);
        }
        
        // Back button
        g.setColor(Color.DARK_GRAY);
        Rectangle backButton = new Rectangle(20, 20, 100, 40);
        g.fillRect(backButton.x, backButton.y, backButton.width, backButton.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        FontMetrics backFontMetrics = g.getFontMetrics();
        int backTextX = backButton.x + (backButton.width - backFontMetrics.stringWidth("Back")) / 2;
        int backTextY = backButton.y + (backButton.height + backFontMetrics.getAscent()) / 2 - 4;
        g.drawString("Back", backTextX, backTextY);
    }
    
    public void mouseMoved(int x, int y) {
        hovered = -1;
        for (int i = 0; i < levelBounds.size(); i++) {
            if (levelBounds.get(i).contains(x, y)) {
                hovered = i;
                break;
            }
        }
    }
    
    public void mousePressed(int x, int y) {
        // Check back button
        Rectangle backButton = new Rectangle(20, 20, 100, 40);
        if (backButton.contains(x, y)) {
            game.setGameState(Game.GameState.MENU);
            return;
        }
        
        // Check level selection
        for (int i = 0; i < levelBounds.size(); i++) {
            if (levelBounds.get(i).contains(x, y)) {
                handleLevelSelection(i);
                return;
            }
        }
    }
    
    private void handleLevelSelection(int levelIndex) {
        // Only allow selecting unlocked levels
        if (!levelManager.isLevelUnlocked(levelIndex)) {
            return; // Level is locked, do nothing
        }
        
        // Set the current level index and start playing
        levelManager.setCurrentLevelIndex(levelIndex);
        game.setGameState(Game.GameState.PLAYING);
    }
}

