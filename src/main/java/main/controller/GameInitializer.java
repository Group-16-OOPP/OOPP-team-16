package main.controller;

import java.awt.image.BufferedImage;

import audio.controller.AudioController;
import main.controller.inputs.KeyboardInputs;
import main.controller.inputs.MouseInputs;
import main.model.GameModel;
import main.model.entities.Player;
import main.model.levels.LevelManager;
import main.view.GameView;
import main.view.interfaces.GamingState;
import main.view.interfaces.LeaderboardState;
import main.view.interfaces.LevelSelectState;
import main.view.interfaces.MenuState;
import main.view.states.Leaderboard;
import main.view.states.LevelSelect;
import main.view.states.MainMenu;
import utilities.LoadSave;

public class GameInitializer {

    private final LevelManager levelManager;
    private final Player player;
    private final GameModel model;
    private final GameController controller;
    private final GameView view;
    private final BufferedImage transitionImage;
    private final GameStateManager gameStateManager;
    private final KeyboardInputs keyboardInputs;
    private final MouseInputs mouseInputs;

    public GameInitializer(Game game, AudioController audioController) {
        player = new Player(200, 550, (int) (32 * Game.SCALE), (int) (32 * Game.SCALE));
        levelManager = new LevelManager(audioController, player,
                game::reloadPlayerForCurrentLevel, game::setGameState);

        model = new GameModel(player, levelManager);
        controller = new GameController(model, player, levelManager, audioController);
        player.setPlayerEventListener(controller);
        view = new GameView(model, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        MainMenu mainMenu = new MainMenu(model, game::setGameState);
        LevelSelect levelSelect = new LevelSelect(levelManager, game::setGameState);
        Leaderboard leaderboard = new Leaderboard(levelManager);

        gameStateManager = new GameStateManager(new GamingState(controller, view), new MenuState(mainMenu),
                new LeaderboardState(leaderboard), new LevelSelectState(levelSelect));

        keyboardInputs = new KeyboardInputs(player, audioController, model, gameStateManager,
                mainMenu, leaderboard, game::setGameState);
        mouseInputs = new MouseInputs(gameStateManager, mainMenu, levelSelect);
    }

    public LevelManager getLevelManager() { return levelManager; }
    public Player getPlayer() { return player; }
    public GameModel getModel() { return model; }
    public GameController getController() { return controller; }
    public GameView getView() { return view; }
    public BufferedImage getTransitionImage() { return transitionImage; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public KeyboardInputs getKeyboardInputs() { return keyboardInputs; }
    public MouseInputs getMouseInputs() { return mouseInputs; }
}
