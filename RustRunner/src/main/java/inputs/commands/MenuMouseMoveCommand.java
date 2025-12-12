package inputs.commands;

import main.Game;

public class MenuMouseMoveCommand implements Command {

    private final Game game;
    private final int x;
    private final int y;

    public MenuMouseMoveCommand(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        if (game.getGameState() == Game.GameState.MENU && game.mainMenu != null) {
            game.mainMenu.mouseMoved(x, y);
        }
    }
}
