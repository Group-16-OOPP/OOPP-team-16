package main;

import audio.controller.AudioController;
import entities.Player;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import Levels.LevelManager;
import main.events.GameEventListener;
import main.model.GameModel;
import main.states.GameBaseState;
import main.states.Leaderboard;
import main.states.LeaderboardState;
import main.states.LevelSelect;
import main.states.LevelSelectState;
import main.states.MainMenu;
import main.states.MenuState;
import main.states.PlayingState;
import main.view.GamePanel;
import main.view.GameView;
import main.view.GameWindow;
import utilz.LoadSave;

public class Game implements Runnable {

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

    private Player player;
    private LevelManager levelManager;
    private AudioController audioController; // audio

    public enum GameState {MENU, PLAYING, LEADERBOARD, LEVEL_SELECT}

    private GameState gameState = GameState.MENU;

    private GameBaseState currentState;
    private PlayingState playingState;
    private MenuState menuState;
    private LeaderboardState leaderboardState;
    private LevelSelectState levelSelectState;

    private BufferedImage transitionImage;

    private final List<GameEventListener> gameEventListeners = new ArrayList<>();
    private GameModel model;
    private GameView view;

    public Game() {
        audioController = new AudioController();
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();

        //menu music
        audioController.playMenuMusic();

        startGameLoop();
    }

    private void initClasses() {
        levelManager = new LevelManager(this);
        player = new Player(200, 550, (int) (32 * SCALE), (int) (32 * SCALE));
        player.setGame(this); //Did not work without????
        loadPlayerForCurrentLevel();

        model = new GameModel(player, levelManager);
        view = new GameView(model);

        transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        mainMenu = new MainMenu(this);
        levelSelect = new LevelSelect(this, levelManager);
        leaderboard = new Leaderboard(this);

        playingState = new PlayingState(this);
        menuState = new MenuState(this);
        leaderboardState = new LeaderboardState(this);
        levelSelectState = new LevelSelectState(this);

        currentState = menuState;
    }

    private void loadPlayerForCurrentLevel() {
        Levels.Level currentLevel = levelManager.getCurrentLvl();
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
            model.updateTransition();
            return;
        }
        currentState.update();
    }

    public void render(Graphics g) {
        currentState.render(g);

        view.renderTransition(g, transitionImage);
    }

    public void updateGameState() {
        model.updatePlaying();

        if (model.checkIsDead()) {
            audioController.playDead();
            levelManager.getCurrentLvl().triggerSpawnPlatform();
        }
        if (model.checkIsRespawn()) {
            audioController.playRespawn();
            levelManager.getCurrentLvl().resetPlatforms();
        }
        if (model.checkIsEndOfLevel()) {
            levelCompletedScoringUpdate();
            audioController.playNextLevel();

            player.resetLevelEnd();
        }
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

        model.setGameState(newState);

        GameBaseState previousState = currentState;
        // Going back to main menu will reset all!
        if (newState == GameState.MENU && oldState == GameState.PLAYING) {
            levelManager.resetToFirstLevel();
            model.resetRunStats();
            loadPlayerForCurrentLevel();
        }



        // If we are starting to play from the menu, start a fresh run (timer & deaths), for leaderboard
        if (newState == GameState.PLAYING && oldState == GameState.MENU) {
            model.startNewRunTimer();
        }

        switch (newState) {
        case MENU -> {
            audioController.playMenuMusic();
            currentState = menuState;
        }
        case LEVEL_SELECT -> {
            audioController.playMenuMusic();
            currentState = levelSelectState;
        }
        case PLAYING -> {
            audioController.playGameMusic();
            currentState = playingState;
        }
        case LEADERBOARD -> {
            audioController.stopAll();
            currentState = leaderboardState;
        }
        default -> {
            //?
        }
        }

        if (previousState != null && previousState != currentState) {
            previousState.onExit();
        }
        if (currentState != null && previousState != currentState) {
            currentState.onEnter();
        }
    }

    public void onPlayerDeath() {
        model.onPlayerDeath();
        for (GameEventListener listener : gameEventListeners) {
            listener.onPlayerDeath();
        }
    }

    public void levelCompletedScoringUpdate() {
        long runEndTimeNanos = System.nanoTime();
        double timeMilliSeconds = (runEndTimeNanos - model.getRunStartTimeNanos()) / 1000000.0;
        int levelIndex = levelManager.getCurrentLevelIndex();
        LoadSave.appendToScoreFile(model.getPlayerName(), levelIndex, timeMilliSeconds, model.getTotalDeathsForRun());

        for (GameEventListener listener : gameEventListeners) {
            listener.onLevelCompleted(levelIndex, model.getTotalDeathsForRun(), timeMilliSeconds);
        }
    }

    public void togglePause() {
        model.togglePause();
    }

    public LevelManager getLevelManager() {
        return model.getLevelManager();
    }
}
