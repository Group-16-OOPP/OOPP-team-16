package inputs.commands;

import main.Game;

public class MoveRightPressCommand implements Command {

    private final Game game;

    public MoveRightPressCommand(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getPlayer().setRight(true);
    }
}

