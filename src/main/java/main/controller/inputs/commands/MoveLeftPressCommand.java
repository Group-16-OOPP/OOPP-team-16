package main.controller.inputs.commands;

import main.model.entities.Player;

public class MoveLeftPressCommand implements Command {

    private final Player player;

    public MoveLeftPressCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        player.setLeft(true);
    }
}

