package main.controller.inputs.commands;

import main.model.entities.Player;

public class MoveRightReleaseCommand implements Command {

    private final Player player;

    public MoveRightReleaseCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.setRight(false);
    }
}
