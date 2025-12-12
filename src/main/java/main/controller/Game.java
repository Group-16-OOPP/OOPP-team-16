package main.controller;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.model.Levels.LevelManager;
import audio.controller.AudioController;
import main.model.entities.Player;
import main.model.GameModel;
import main.model.observerEvents.GameObserver;
import main.view.interfaces.GameBaseState;
import main.view.interfaces.GamingState;
import main.view.interfaces.LeaderboardState;
import main.view.interfaces.LevelSelectState;
import main.view.interfaces.MenuState;
import main.view.states.*;
import main.view.GamePanel;
import main.view.GameView;
import main.view.GameWindow;
import utilities.LoadSave;

public class Game implements Runnable, GameObserver {

    public static final int TILES_DEAFULT_SIZE = 32;
    public static final float SCALE = 1.0f;
    public static final int TILES_IN_WIDTH = 40;
    public static final int TILES_IN_HEIGHT = 25;
    public static final int TILES_SIZE = (int) (TILES_DEAFULT_SIZE * SCALE);
    public static final int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public MainMenu mainMenu;
    public Leaderboard leaderboard;
    public LevelSelect levelSelect;

    private GamePanel gamePanel;
    private GameWindow gameWindow;
    private Thread gametThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    //MVC Components
    private GameModel model;
    private GameView view;

    private Player player;
    private LevelManager levelManager;
    private AudioController audioController;

    public enum GameState {MENU, PLAYING, LEADERBOARD, LEVEL_SELECT}

    private GameState gameState = GameState.MENU;

    private GameBaseState currentState;
    private GamingState gamingState;
    private MenuState menuState;
    private LeaderboardState leaderboardState;
    private LevelSelectState levelSelectState;

    private BufferedImage transitionImage;

    public Game() {
        audioController = AudioController.getInstance();
        initClasses();

        if (currentState != null) {
            currentState.onEnter();
        }
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses() {
        //Initialize Data/Logic (Model parts)
        levelManager = new LevelManager(this);
        player = new Player(200, 550, (int) (32 * SCALE), (int) (32 * SCALE));

        loadPlayerForCurrentLevel();

        //Setup MVC
        model = new GameModel(player, levelManager);
        model.addObserver(this); //Controller listens to Model

        view = new GameView(model, GAME_WIDTH, GAME_HEIGHT);

        transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        mainMenu = new MainMenu(this);
        levelSelect = new LevelSelect(this, levelManager);
        leaderboard = new Leaderboard(this);

        gamingState = new GamingState(this);
        menuState = new MenuState(this);
        leaderboardState = new LeaderboardState(this);
        levelSelectState = new LevelSelectState(this);

        currentState = menuState;
    }

    //OBSERVER IMPLEMENTATION

    @Override
    public void onPlayerDied() {
        //Controller Logic: Play Audio
        audioController.playDead();
        //Controller Logic: Update Environment
        levelManager.getCurrentLvl().triggerSpawnPlatform();
    }

    @Override
    public void onPlayerRespawn() {
        audioController.playRespawn();
        levelManager.getCurrentLvl().resetPlatforms();
    }

    @Override
    public void onLevelCompleted() {
        model.recordLevelCompletion();
        //Audio feedback remains a controller concern
        audioController.playNextLevel();
    }

    @Override
    public void onLevelLoadRequested() {
        //Called when transition covers the screen, safe to swap heavy data if needed
    }

    @Override
    public void onTransitionComplete() {
        //Transition animation finished
    }

    //==========================================================

    private void loadPlayerForCurrentLevel() {
        main.model.Levels.Level currentLevel = levelManager.getCurrentLvl();
        player.setSpawnPoint(currentLevel.getSpawnX(), currentLevel.getSpawnY());
        player.loadLvlData(currentLevel.getLevelData());
        player.setCurrentLevel(currentLevel);
        player.spawnAtLevelStart();
        currentLevel.resetPlatforms();
        currentLevel.clearDeathPositions();
    }

    public void reloadPlayerCurrentLevel() {
        loadPlayerForCurrentLevel();
    }

    private void update() {
        //Update Model (Rules & Physics)
        model.update();
        //Update Current State (Input/UI)
        currentState.update();
    }

    public void render(Graphics g) {
        currentState.render(g);
        view.renderTransition(g, transitionImage);
    }

    public void renderGame(Graphics g) {
        view.renderGame(g);
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
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    //Getters & Setters
    public Player getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public GameState getGameState() {
        return gameState;
    }

    public AudioController getAudioController() {
        return audioController;
    }

    public String getPlayerName() {
        return model.getPlayerName();
    }

    public void setPlayerName(String playerName) {
        model.setPlayerName(playerName);
    }

    public void setGameState(GameState newState) {
        GameState oldState = this.gameState;
        this.gameState = newState;

        model.setGameActive(newState == GameState.PLAYING);

        GameBaseState previousState = currentState;

        if (newState == GameState.MENU && oldState == GameState.PLAYING) {
            model.onEnterMenuFromPlaying();
        }

        if (newState == GameState.PLAYING && oldState == GameState.MENU) {
            model.onEnterPlayingFromMenu();
        }

        if (newState == GameState.PLAYING && oldState == GameState.LEVEL_SELECT) {
            model.onEnterPlayingFromLevelSelect();
        }

        switch (newState) {
        case MENU -> currentState = menuState;
        case LEVEL_SELECT -> currentState = levelSelectState;
        case PLAYING -> currentState = gamingState;
        case LEADERBOARD -> currentState = leaderboardState;
        }

        if (previousState != null && previousState != currentState) previousState.onExit();
        if (currentState != null && previousState != currentState) currentState.onEnter();
    }

    public void togglePause() {
        model.togglePause();
    }

    public LevelManager getLevelManager() {
        return model.getLevelManager();
    }
}
