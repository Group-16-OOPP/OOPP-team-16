package main.controller;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import audio.controller.AudioController;
import main.model.GameModel;
import main.model.entities.Player;
import main.model.levels.Level;
import main.model.levels.LevelManager;
import main.model.observerEvents.GameEventListener;
import main.model.observerEvents.PlayerEventListener;
import main.view.GamePanel;
import main.view.GameView;
import main.view.GameWindow;
import main.view.interfaces.GameBaseState;
import main.view.interfaces.GamingState;
import main.view.interfaces.LeaderboardState;
import main.view.interfaces.LevelSelectState;
import main.view.interfaces.MenuState;
import main.view.states.Leaderboard;
import main.view.states.LevelSelect;
import main.view.states.MainMenu;
import utilities.LoadSave;

public class Game extends PlayerEventListener{

    public static final int TILES_DEAFULT_SIZE = 32;
    public static final float SCALE = 1.0f;
    public static final int TILES_IN_WIDTH = 40;
    public static final int TILES_IN_HEIGHT = 25;
    public static final int TILES_SIZE = (int) (TILES_DEAFULT_SIZE * SCALE);
    public static final int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    private GameLoopManager gameLoopManager;

    public MainMenu mainMenu;
    public Leaderboard leaderboard;
    public LevelSelect levelSelect;

    private GamePanel gamePanel;
    private GameWindow gameWindow;

    private Player player;
    private LevelManager levelManager;
    private AudioController audioController; // audio



    public enum GameState {MENU, PLAYING, LEADERBOARD, LEVEL_SELECT}

    private GameState gameState = GameState.MENU;

    private GameBaseState currentState;
    private GamingState gamingState;
    private MenuState menuState;
    private LeaderboardState leaderboardState;
    private LevelSelectState levelSelectState;

    private BufferedImage transitionImage;

    private final List<GameEventListener> gameEventListeners = new ArrayList<>();
    private GameModel model;
    private GameController controller;
    private GameView view;

    public Game() {
        audioController = AudioController.getInstance();
        initClasses();

        if (currentState != null) {
            currentState.onEnter();
        }
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        gameLoopManager = new GameLoopManager(this::update, gamePanel::repaint);

        gameLoopManager.startGameLoop();
    }

    private void initClasses() {
        levelManager = new LevelManager(this);
        player = new Player(200, 550, (int) (32 * SCALE), (int) (32 * SCALE));
        player.setPlayerEventListener(this);
        loadPlayerForCurrentLevel();

        model = new GameModel(player, levelManager);
        controller = new GameController(model, player, levelManager);
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
        currentState.update();
    }

    public void update(String eventType, File file) {

    }

    public void render(Graphics g) {
        currentState.render(g);

        view.renderTransition(g, transitionImage);
    }

    //TODO maybe check if we can move this into observer patterns?
    public void updateGameState() {
        controller.updatePlaying();

        //TODO Abstract so that it listens for "PlayerDeath"
        if (controller.checkIsDead()) {
            audioController.playDead();
            levelManager.getCurrentLvl().triggerSpawnPlatform();
        }

        ///TODO Abstract so that it listens for "PlayerRespawn"
        if (controller.checkIsRespawn()) {
            audioController.playRespawn();
            levelManager.getCurrentLvl().resetPlatforms();
        }

        //TODO Abstract so that it listens for "LvlCompletedEvent"
        if (controller.checkIsEndOfLevel()) {
            levelCompletedScoringUpdate();
            audioController.playNextLevel();

            player.resetLevelEnd();
        }
    }

    public void renderGame(Graphics g) {
        view.renderGame(g);
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

    //AUDIO CONTROL METHODS
    public AudioController getAudioController() {
        return audioController;
    }

    //PLAYER NAME METHODS
    public String getPlayerName() {
        return model.getPlayerName();
    }

    public void setPlayerName(String playerName) {
        model.setPlayerName(playerName);
    }

    //GAME STATING
    public void setGameState(GameState newState) {
        GameState oldState = this.gameState;
        this.gameState = newState;

        GameBaseState previousState = currentState;
        //TODO Move into gamingState: onExit
        if (newState == GameState.MENU) {
            // Reset transition state when returning to menu
            if (model.isInTransition()) {
                model.resetTransition();
            }
            if (oldState == GameState.PLAYING) {
                levelManager.resetToFirstLevel();
                model.resetStats();
                loadPlayerForCurrentLevel();
            }
        }

        //TODO Move into gamingState: onEnter
        //If we are starting to play from the menu, start a fresh run (timer & deaths), for leaderboard
        if (newState == GameState.PLAYING && oldState == GameState.MENU) {
            model.startNewTimer();
            loadPlayerForCurrentLevel();
        }

        //If we are starting to play from the level select menu, load the player for the selected level
        if (newState == GameState.PLAYING && oldState == GameState.LEVEL_SELECT) {
            loadPlayerForCurrentLevel();
        }

        switch (newState) {
        case MENU -> currentState = menuState;
        case LEVEL_SELECT -> currentState = levelSelectState;
        case PLAYING -> currentState = gamingState;
        case LEADERBOARD -> currentState = leaderboardState;
        default -> {
            // needed to satisfy checkstyle
        }
        }

        if (previousState != null && previousState != currentState) {
            previousState.onExit();
        }
        if (currentState != null && previousState != currentState) {
            currentState.onEnter();
        }
    }

    //TODO move into EventListner / Observer abstration here?
    @Override
    public void onPlayerDeath() {
        controller.onPlayerDeath();

        for (GameEventListener listener : gameEventListeners) {
            listener.onPlayerDeath();
        }
    }

    //TODO move into EventListner / Observer abstration here?
    public void levelCompletedScoringUpdate() {
        long runEndTimeNanos = System.nanoTime();
        double timeSeconds = (runEndTimeNanos - model.getRunStartTimeNanos()) / 1000000000.0;
        int levelIndex = levelManager.getCurrentLevelIndex();
        LoadSave.appendToScoreFile(model.getPlayerName(), levelIndex, timeSeconds, model.getTotalDeathsForRun());

        for (GameEventListener listener : gameEventListeners) {
            listener.onLevelCompleted(levelIndex, model.getTotalDeathsForRun(), timeSeconds);
        }
    }

    public void togglePause() {
        model.togglePause();
    }

    public LevelManager getLevelManager() {
        return model.getLevelManager();
    }
}
