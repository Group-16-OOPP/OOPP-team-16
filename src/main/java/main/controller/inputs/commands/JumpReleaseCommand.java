package main.controller.inputs.commands;

import main.model.entities.Player;

public class JumpReleaseCommand implements Command {

    private final Player player;

    public JumpReleaseCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.setJump(false);
    }
}

