package inputs.commands;

import main.Game;

public class MoveLeftPressCommand implements Command {

    private final Game game;

    public MoveLeftPressCommand(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getPlayer().setLeft(true);
    }
}

