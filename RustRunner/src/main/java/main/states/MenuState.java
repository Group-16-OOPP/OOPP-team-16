package main.states;

import java.awt.Graphics;

import audio.controller.AudioController;
import main.Game;

// rendering and updating the main menu
public class MenuState extends GameBaseState {

    public MenuState(Game game) {
        super(game);
    }

    @Override
    public void update() {
        game.mainMenu.update();
    }

    @Override
    public void render(Graphics g) {
        game.mainMenu.draw(g);
    }

    @Override
    public void onEnter() {
        AudioController controller = AudioController.getInstance();
        controller.playMenuMusic();
    }

    @Override
    public void onExit() {
    }
}
