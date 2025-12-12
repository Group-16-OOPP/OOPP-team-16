package main.states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import main.Game;
import utilities.LoadSave;

public class Leaderboard {
    private final Game game;
    private final Font titleFont = new Font("Arial", Font.BOLD, 48);
    private final Font headerFont = new Font("Arial", Font.BOLD, 24);
    private final Font rowFont = new Font("Arial", Font.PLAIN, 20);

    // Track which level's leaderboard is currently shown +1
    private int currentLevelIndex = 0;

    public Leaderboard(Game game) {
        this.game = game;
    }

    public void update() {
        //old
    }

    private static class Entry {
        final String name;
        final int level;
        final int deaths;
        final double time;

        Entry(String name, int level, int deaths, double time) {
            this.name = name;
            this.level = level;
            this.deaths = deaths;
            this.time = time;
        }
    }

    private List<Entry> loadEntriesForLevel(int levelIndex) {
        List<String> lines = LoadSave.readScoreFile();
        List<Entry> entries = new ArrayList<>();
        int levelNumber = levelIndex + 1;

        for (String line : lines) {
            String[] parts = line.split(";");
            if (parts.length < 4) {
                continue;
            }
            try {
                String name = parts[0];
                int level = Integer.parseInt(parts[1]);
                int deaths = Integer.parseInt(parts[2]);
                double time = Double.parseDouble(parts[3]);
                if (level == levelNumber) {
                    entries.add(new Entry(name, level, deaths, time));
                }
            } catch (NumberFormatException ignored) {
                System.out.println(line);
            }
        }

        // score per - Fewest deaths & lowest time
        entries.sort(Comparator.comparingInt((Entry e) -> e.deaths).thenComparingDouble(e -> e.time));

        // Keep only top 5 for this level
        if (entries.size() > 5) {
            return new ArrayList<>(entries.subList(0, 5));
        }
        return entries;
    }

    //Switch the levels using keyboard in leaderboards
    public void nextLevel() {
        int totalLevels = 1;

        if (game != null) {
            totalLevels = game.getLevelManager().getLevelCount();
            if (totalLevels <= 0) {
                totalLevels = 1;
            }
        }

        currentLevelIndex = (currentLevelIndex + 1) % totalLevels;
    }

    public void previousLevel() {
        int totalLevels = 1;

        if (game != null) {
            totalLevels = game.getLevelManager().getLevelCount();
            if (totalLevels <= 0) {
                totalLevels = 1;
            }
        }
        currentLevelIndex = (currentLevelIndex - 1 + totalLevels) % totalLevels;
    }

    public void draw(Graphics g) {
        // background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        //Current level
        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        String title = "LEADERBOARD - LEVEL " + (currentLevelIndex + 1);
        FontMetrics font = g.getFontMetrics();
        int calculatedWidth = (Game.GAME_WIDTH - font.stringWidth(title)) / 2;
        g.drawString(title, calculatedWidth, 100);

        //Left and Right arrows
        g.setFont(headerFont);
        String leftArrow = "<";
        String rightArrow = ">";
        int arrowY = 100;
        g.drawString(leftArrow, calculatedWidth - 60, arrowY);
        g.drawString(rightArrow, calculatedWidth + font.stringWidth(title) + 40, arrowY);

        // headers
        int startX = 100;
        int startY = 160;
        int headerCalculatedColumnSpaceRank = startX;
        int headerCalculatedColumnSpaceName = startX + 80;
        int headerCalculatedColumnSpaceDeaths = startX + 360;
        int headerCalculatedColumnSpaceTime = startX + 520;

        g.drawString("#", headerCalculatedColumnSpaceRank, startY);
        g.drawString("Name", headerCalculatedColumnSpaceName, startY);
        g.drawString("Deaths", headerCalculatedColumnSpaceDeaths, startY);
        g.drawString("Time (ms)", headerCalculatedColumnSpaceTime, startY);

        // rows
        g.setFont(rowFont);
        List<Entry> entries = loadEntriesForLevel(currentLevelIndex);
        int rowY = startY + 30;
        for (int i = 0; i < entries.size(); i++) {
            Entry e = entries.get(i);
            g.drawString(String.valueOf(i + 1), headerCalculatedColumnSpaceRank, rowY);
            g.drawString(e.name, headerCalculatedColumnSpaceName, rowY);
            g.drawString(String.valueOf(e.deaths), headerCalculatedColumnSpaceDeaths, rowY);
            g.drawString(String.format("%.4f", e.time), headerCalculatedColumnSpaceTime, rowY);
            rowY += 26;
        }

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Use LEFT or RIGHT to change level, ESC to return", 20, Game.GAME_HEIGHT - 30);
    }
}
