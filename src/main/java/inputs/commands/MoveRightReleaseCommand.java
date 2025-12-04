package inputs.commands;

import main.Game;

public class MoveRightReleaseCommand implements Command {

    private final Game game;

    public MoveRightReleaseCommand(Game game) {
        this.game = game;
    }

    @Override
    public void execute() {
        game.getPlayer().setRight(false);
    }
}

