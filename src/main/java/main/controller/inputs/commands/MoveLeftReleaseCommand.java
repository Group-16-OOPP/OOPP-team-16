package main.controller.inputs.commands;
import main.model.entities.Player;

public class MoveLeftReleaseCommand implements Command {
    private final Player player;

    public MoveLeftReleaseCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.setLeft(false);
    }
}

