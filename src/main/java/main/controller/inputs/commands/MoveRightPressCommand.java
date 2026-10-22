package main.controller.inputs.commands;

import main.model.entities.Player;

public class MoveRightPressCommand implements Command {

    private final Player player;

    public MoveRightPressCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.setRight(true);
    }
}

