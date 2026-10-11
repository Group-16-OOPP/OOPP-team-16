package main.controller.inputs.commands;
import main.controller.GameStateManager;
import main.controller.Game;

public class GoToMenuCommand implements Command {

    private final Game game;

    public GoToMenuCommand(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.setGameState(GameStateManager.GameState.MENU);
    }
}

