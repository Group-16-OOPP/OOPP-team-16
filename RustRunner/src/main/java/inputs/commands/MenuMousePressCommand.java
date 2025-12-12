package inputs.commands;

import main.Game;

public class MenuMousePressCommand implements Command {

    private final Game game;
    private final int x;
    private final int y;

    public MenuMousePressCommand(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        if (game.getGameState() == Game.GameState.MENU && game.mainMenu != null) {
            game.mainMenu.mousePressed(x, y);
        }
    }
}

