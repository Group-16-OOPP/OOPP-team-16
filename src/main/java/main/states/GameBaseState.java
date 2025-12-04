package main.states;
import java.awt.Graphics;
import main.Game;

/**
 * Base abstractclass for all high-level game states:
 * (menu, playing, leaderboard, level select).
 * https://refactoring.guru/design-patterns/state
 * IMPORTANT! We have to create a new State implementing the GameBaseState for rendering
 * every new state we want.
 */
public abstract class GameBaseState {
    protected final Game game;
    protected GameBaseState(Game game) {
        this.game = game;
    }

    //Called every update tick.
    public void update() {
    }

    //Called every frame to render "this" state.
    public void render(Graphics g) {
    }

    //Go when this state becomes active.
    public void onEnter() {
    }

    //Go when this state is deactivated.
    public void onExit() {
    }
}
