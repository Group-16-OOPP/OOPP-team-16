package main;

import java.awt.Graphics;

import Levels.LevelManager;
import entities.Player;
import main.states.Leaderboard;
import main.states.MainMenu;

public class Game implements Runnable {

    private GamePanel gamePanel;
    private GameWindow gameWindow;
    private Thread gametThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private Player player;
    private LevelManager levelManager;

    public final static int TILES_DEAFULT_SIZE = 32;
    public final static float SCALE = 1.0f;
    public final static int TILES_IN_WIDTH = 40;
    public final static int TILES_IN_HEIGHT = 25;
    public final static int TILES_SIZE = (int) (TILES_DEAFULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public enum GameState {MENU, PLAYING, LEADERBOARD}

    private GameState gameState = GameState.MENU;

    public MainMenu mainMenu;
    public Leaderboard leaderboard;

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses() {
        levelManager = new LevelManager(this);
        player = new Player(200, 550, (int) (32 * SCALE), (int) (32 * SCALE));
        player.loadLvlData(levelManager.getCurrentLvl().getLevelData());

        mainMenu = new MainMenu(this);

    }

    private void update() {
        switch (gameState) {
        case PLAYING:
            player.update();
            levelManager.update();
            break;
        case MENU:
            mainMenu.update();
            break;
        case LEADERBOARD:
            //leaderboard.update(); TODO
            break;
        }
    }

    public void render(Graphics g) {
        switch (gameState) {
        case PLAYING:
            levelManager.draw(g);
            player.render(g);
            break;
        case MENU:
            mainMenu.draw(g);
            break;
        case LEADERBOARD:
            // leaderboard placeholder
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            g.setColor(java.awt.Color.WHITE);
            g.drawString("Leaderboard - Press ESC to return", 50, 50);

            //leaderboard.draw(g); TODO
            break;
        }
    }

    private void startGameLoop() {
        gametThread = new Thread(this);
        gametThread.start();
    }


    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;
            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + "UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState newState) {
        this.gameState = newState;
        if (newState == GameState.PLAYING) {
            // ensure player and level are (re)initialized if needed
            player.loadLvlData(levelManager.getCurrentLvl().getLevelData());
        }
    }
}
