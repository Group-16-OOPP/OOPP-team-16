package main.controller.inputs.commands;

import java.util.function.Consumer;

import main.controller.GameStateManager;

public class GoToMenuCommand implements Command {

    private final Consumer<GameStateManager.GameState> stateChangeAction;

    public GoToMenuCommand(Consumer<GameStateManager.GameState> stateChangeAction) {
        this.stateChangeAction = stateChangeAction;
    }

    @Override
    public void execute() {
        stateChangeAction.accept(GameStateManager.GameState.MENU);
    }
}
