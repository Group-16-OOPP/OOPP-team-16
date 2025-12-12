package inputs.commands;

import main.Game;

public class MoveLeftReleaseCommand implements Command {

    private final Game game;

    public MoveLeftReleaseCommand(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getPlayer().setLeft(false);
    }
}

