package inputs.commands;

import main.Game;

public class LevelSelectMousePressCommand implements Command {

    private final Game game;
    private final int x;
    private final int y;

    public LevelSelectMousePressCommand(Game game, int x, int y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        if (game.getGameState() == Game.GameState.LEVEL_SELECT && game.levelSelect != null) {
            game.levelSelect.mousePressed(x, y);
        }
    }
}

