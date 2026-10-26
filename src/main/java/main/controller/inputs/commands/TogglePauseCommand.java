package main.controller.inputs.commands;

import main.model.GameModel;

public class TogglePauseCommand implements Command {

    private final GameModel model;

    public TogglePauseCommand(GameModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        model.togglePause();
    }
}

