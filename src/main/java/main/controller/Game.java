package main.controller;

import java.awt.Graphics;
import java.awt.image.BufferedImage;


import audio.controller.AudioController;
import main.controller.inputs.KeyboardInputs;
import main.controller.inputs.MouseInputs;
import main.model.GameModel;
import main.model.entities.Player;
import main.model.levels.Level;
import main.model.levels.LevelManager;
import main.view.GamePanel;
import main.view.GameView;
import main.view.GameWindow;

public class Game {

    public static final int TILES_DEAFULT_SIZE = 32;
    public static final float SCALE = 1.0f;
    public static final int TILES_IN_WIDTH = 40;
    public static final int TILES_IN_HEIGHT = 25;
    public static final int TILES_SIZE = (int) (TILES_DEAFULT_SIZE * SCALE);
    public static final int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    private GameLoopManager gameLoopManager;
    private GamePanel gamePanel;
    private GameWindow gameWindow;

    private Player player;
    private LevelManager levelManager;
    private AudioController audioController;

    private GameStateManager gameStateManager;
    private BufferedImage transitionImage;

    private GameModel model;
    private GameController controller;
    private GameView view;

    private KeyboardInputs keyboardInputs;
    private MouseInputs mouseInputs;

    public Game() {
        audioController = AudioController.getInstance();
        initClasses();
        gamePanel = new GamePanel(this::render, keyboardInputs, mouseInputs);
        gameWindow = new GameWindow(gamePanel, this::windowFocusLost);
        gamePanel.requestFocus();

        gameLoopManager = new GameLoopManager(this::update, gamePanel::repaint);

        gameLoopManager.startGameLoop();
    }

    private void initClasses() {
        GameInitializer init = new GameInitializer(this, audioController);
        levelManager = init.getLevelManager();
        player = init.getPlayer();
        model = init.getModel();
        controller = init.getController();
        view = init.getView();
        transitionImage = init.getTransitionImage();
        gameStateManager = init.getGameStateManager();
        keyboardInputs = init.getKeyboardInputs();
        mouseInputs = init.getMouseInputs();

        loadPlayerForCurrentLevel();
    }

    private void loadPlayerForCurrentLevel() {
        Level currentLevel = levelManager.getCurrentLvl();
        player.setSpawnPoint(currentLevel.getSpawnX(), currentLevel.getSpawnY());
        player.loadLvlData(currentLevel.getLevelData());
        player.setCurrentLevel(currentLevel);
        player.spawnAtLevelStart();
        currentLevel.resetPlatforms();
        currentLevel.clearDeathPositions();
    }

    public void reloadPlayerForCurrentLevel() {
        loadPlayerForCurrentLevel();
    }

    private void update() {
        if (model.isInTransition()) {
            controller.updateTransition();
            return;
        }
        gameStateManager.getCurrentState().update();
    }


    private void render(Graphics g) {
        gameStateManager.getCurrentState().render(g);

        view.renderTransition(g, transitionImage);
    }


    private void windowFocusLost() {
        player.resetDirBooleans();
    }


    public void setGameState(GameStateManager.GameState newState) {
        GameStateManager.GameState oldState = gameStateManager.getGameState();
        gameStateManager.setGameState(newState);
        handleStateTransition(oldState, newState);
    }

    private void handleStateTransition(GameStateManager.GameState oldState,
            GameStateManager.GameState newState) {
        if (newState == GameStateManager.GameState.MENU) {
            if (model.isInTransition()) {
                model.resetTransition();
            }
            if (oldState == GameStateManager.GameState.PLAYING) {
                levelManager.resetToFirstLevel();
                model.resetStats();
                loadPlayerForCurrentLevel();
            }
        }

        if (newState == GameStateManager.GameState.PLAYING
                && oldState == GameStateManager.GameState.MENU) {
            model.startNewTimer();
            loadPlayerForCurrentLevel();
        }

        if (newState == GameStateManager.GameState.PLAYING
                && oldState == GameStateManager.GameState.LEVEL_SELECT) {
            loadPlayerForCurrentLevel();
        }
    }
}
