package main.controller.inputs.commands;

import main.model.entities.Player;

public class JumpPressCommand implements Command {

    private final Player player;

    public JumpPressCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.setJump(true);
    }
}
