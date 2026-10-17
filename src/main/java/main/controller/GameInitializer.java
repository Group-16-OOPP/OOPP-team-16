package main.controller;

import java.awt.image.BufferedImage;

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
    private final MainMenu mainMenu;
    private final LevelSelect levelSelect;
    private final Leaderboard leaderboard;
    private final GameStateManager gameStateManager;

    public GameInitializer(Game game) {
        levelManager = new LevelManager(game);
        player = new Player(200, 550, (int) (32 * Game.SCALE), (int) (32 * Game.SCALE));
        player.setPlayerEventListener(game);

        model = new GameModel(player, levelManager);
        controller = new GameController(model, player, levelManager);
        view = new GameView(model, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        transitionImage = LoadSave.getSpriteAtlas(LoadSave.TRANSITION_IMG);

        mainMenu = new MainMenu(game);
        levelSelect = new LevelSelect(game, levelManager);
        leaderboard = new Leaderboard(game);

        gameStateManager = new GameStateManager(new GamingState(game), new MenuState(game),
                new LeaderboardState(game), new LevelSelectState(game));
    }

    public LevelManager getLevelManager() { return levelManager; }
    public Player getPlayer() { return player; }
    public GameModel getModel() { return model; }
    public GameController getController() { return controller; }
    public GameView getView() { return view; }
    public BufferedImage getTransitionImage() { return transitionImage; }
    public MainMenu getMainMenu() { return mainMenu; }
    public LevelSelect getLevelSelect() { return levelSelect; }
    public Leaderboard getLeaderboard() { return leaderboard; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
}
