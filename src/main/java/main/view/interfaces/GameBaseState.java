package main.view.interfaces;
import java.awt.Graphics;


public abstract class GameBaseState {

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
